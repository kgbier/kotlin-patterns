package dev.kgbier.patterns.sample.screen.settings

import dev.kgbier.patterns.sample.repo.SettingsRepo
import dev.kgbier.patterns.xmvi.IntentProcessor
import dev.kgbier.patterns.xmvi.defaultIntentProcessor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach

class SettingsProcessor(
    private val settingsRepo: SettingsRepo,
) : IntentProcessor<SettingsIntent, SettingsState, SettingsAction> by defaultIntentProcessor() {

    override val actionStream: Flow<SettingsAction> = run {
        val settingsRepoActions = settingsRepo.subscribe().map {
            SettingsAction.ReceiveRemoteSettings(it.toRemoteSettings())
        }

        val sidechannelStateFlow: Flow<SettingsAction> = flow {
            thunk {
                stateFlow.onEach { sidechannelStateFlow.value = it }
                    .collect()
            }
        }

        merge(settingsRepoActions, sidechannelStateFlow)
    }

    override suspend fun process(intent: SettingsIntent) = when (intent) {
        SettingsIntent.ToggleThingy -> SettingsAction.ToggleThingy.dispatch()

        SettingsIntent.EnableWidget -> SettingsAction.ToggleWidget(enabled = true).dispatch()
        SettingsIntent.DisableWidget -> SettingsAction.ToggleWidget(enabled = false).dispatch()

        SettingsIntent.ToggleFlim -> SettingsAction.ToggleFlim.dispatch()
        is SettingsIntent.UpdateFlam -> SettingsAction.UpdateFlam(intent.string).dispatch()

        SettingsIntent.ToggleGizmo -> SettingsAction.ToggleGizmo.dispatch()
        SettingsIntent.AcceptGizmoTerms -> SettingsAction.AcceptGizmoTerms.dispatch()
    }

    val sidechannelStateFlow = MutableStateFlow(SettingsViewModel.buildInitialState())
}