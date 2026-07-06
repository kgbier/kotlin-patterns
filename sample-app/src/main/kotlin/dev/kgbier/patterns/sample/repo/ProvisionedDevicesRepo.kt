package dev.kgbier.patterns.sample

import dev.kgbier.patterns.xstore.Reducer
import dev.kgbier.patterns.xstore.XStore
import dev.kgbier.patterns.xstore.createStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

interface DeviceService {
    suspend fun provision(): String
    suspend fun getDevices(): List<String>
    suspend fun getDeviceStatus(id: String): Boolean
    fun statusUpdateFlow(): Flow<Pair<String, Boolean>>
}

object ProvisionedDevicesReducer :
    Reducer<ProvisionedDevicesRepo.State, ProvisionedDevicesRepo.Action> {

    override fun invoke(
        state: ProvisionedDevicesRepo.State,
        action: ProvisionedDevicesRepo.Action
    ): ProvisionedDevicesRepo.State = when (action) {
        is ProvisionedDevicesRepo.Action.ReceiveNewDevice -> state.copy(devices = state.devices + action.id)

        is ProvisionedDevicesRepo.Action.ReceiveDeviceStatus -> {
            val newMap = state.healthStatus.toMutableMap()
            newMap[action.id] = action.isHealthy
            state.copy(healthStatus = newMap)
        }
    }
}

class ProvisionedDevicesRepo(
    val deviceService: DeviceService,
    val scope: CoroutineScope,
) {

    sealed interface Action {
        data class ReceiveNewDevice(val id: String) : Action
        data class ReceiveDeviceStatus(val id: String, val isHealthy: Boolean) : Action
    }

    data class State(
        val devices: List<String>,
        val healthStatus: Map<String, Boolean>,
    )

    val actions: Flow<Action> = run {
        val initialDevicesFlow = flow {
            deviceService.getDevices().forEach { id ->
                emit(Action.ReceiveNewDevice(id))
            }
        }

        val deviceStatusFlow = deviceService.statusUpdateFlow()
            .map { (id, status) ->
                Action.ReceiveDeviceStatus(id, isHealthy = status)
            }

        merge(initialDevicesFlow, deviceStatusFlow)
    }

    private val store: XStore<State, Action> = createStore(
        scope = scope,
        initialState = initialState,
        reducer = ProvisionedDevicesReducer,
        actions = actions,
        debugIdentifier = "ProvisionedDevicesRepo",
    )

    val state: StateFlow<State>
        get() = store.state

    suspend fun provision() = store.thunk {
        val ticketId = deviceService.provision()

        val newState = Action.ReceiveNewDevice(ticketId).dispatch()

        coroutineScope {
            newState.devices.forEach {
                launch {
                    val status = deviceService.getDeviceStatus(it)
                    Action.ReceiveDeviceStatus(ticketId, isHealthy = status).dispatch()
                }
            }
        }
    }

    companion object {
        val initialState = State(devices = emptyList(), healthStatus = emptyMap())
    }
}

