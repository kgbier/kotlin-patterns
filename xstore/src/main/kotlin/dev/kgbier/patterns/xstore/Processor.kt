package dev.kgbier.patterns.xstore

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

/**
 * Processes activity and dispatches events to the store.
 */
interface Processor<TState : Any, TAction : Any> {

    // region Action handling

    val actionChannel: Channel<TAction>
    val actionStream: Flow<TAction>

    /**
     * Dispatches an Action.
     */
    suspend fun TAction.dispatch() = actionChannel.send(this)

    // endregion Action handling

    // region Thunk handling

    val thunkChannel: Channel<Thunk<TState, TAction>>

    /**
     * Dispatches a Thunk.
     *
     * @param thunk The [Thunk] dispatch.
     */
    suspend fun thunk(
        thunk: Thunk<TState, TAction>,
    ) = thunkChannel.send(thunk)

    /**
     * Dispatches a Thunk.
     *
     * @param body The Thunk body to dispatch.
     */
    suspend fun thunk(
        body: suspend Thunk.Scope<TState, TAction>.() -> Unit,
    ) = thunk(Thunk(body))

    /**
     * Launches a runnable thunk.
     *
     * @param body The Thunk body to launch.
     */
    fun CoroutineScope.launchThunk(
        body: suspend Thunk.Scope<TState, TAction>.() -> Unit,
    ) {
        launch { this@Processor.thunk(body) }
    }

    /**
     * Launches a runnable thunk.
     *
     * @param thunk The [Thunk] dispatch.
     */
    fun CoroutineScope.launchThunk(
        thunk: Thunk<TState, TAction>,
    ) {
        launch { thunk(thunk) }
    }

    // endregion Thunk handling
}

/**
 * Default implementation of Processor.
 *
 * Opines on the behaviour of [actionChannel] and [thunkChannel] - exposing them as unbuffered channels.
 */
class DefaultProcessor<TState : Any, TAction : Any>(
    override val actionStream: Flow<TAction> = emptyFlow(),
) : Processor<TState, TAction> {

    override val actionChannel: Channel<TAction> = Channel()

    override val thunkChannel: Channel<Thunk<TState, TAction>> = Channel()
}

/**
 * Creates a default instance of Processor.
 */
inline fun <reified TState : Any, reified TAction : Any> defaultProcessor(
    actions: Flow<TAction> = emptyFlow(),
) = DefaultProcessor<TState, TAction>(actionStream = actions)

