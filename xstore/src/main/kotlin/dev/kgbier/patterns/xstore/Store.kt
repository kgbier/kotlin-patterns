package dev.kgbier.patterns.xstore

import dev.kgbier.patterns.xstore.Thunk.Scope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.newCoroutineContext
import kotlinx.coroutines.withContext
import kotlin.coroutines.ContinuationInterceptor

interface Store<TState : Any> {
    val state: StateFlow<TState>
}

interface XStore<TState : Any, TAction : Any> :
    Store<TState>,
    Processor<TState, TAction>

inline fun <reified TState : Any, reified TAction : Any> createStore(
    scope: CoroutineScope,
    initialState: TState,
    actions: Flow<TAction> = emptyFlow(),
    reducer: Reducer<TState, TAction>,
    processor: Processor<TState, TAction> = defaultProcessor(actions = actions),
    debugIdentifier: String? = null,
) = DefaultXStore(
    scope = scope,
    initialState = initialState,
    processor = processor,
    reducer = reducer,
    debugIdentifier = debugIdentifier,
)

class DefaultXStore<TState : Any, TAction : Any>(
    scope: CoroutineScope,
    initialState: TState,
    processor: Processor<TState, TAction>,
    reducer: Reducer<TState, TAction>,
    debugIdentifier: String? = null,
) : XStore<TState, TAction>, Processor<TState, TAction> by processor {

    private val actions = merge(
        actionStream,
        actionChannel.receiveAsFlow(),
    ).optionallyDebug(debugIdentifier) { "ACTION: $it" }
        .map { Dispatchable.Action(it) }

    private val thunks = thunkChannel.receiveAsFlow()
        .optionallyDebug(debugIdentifier) { " THUNK: $it" }
        .map { Dispatchable.Thunk(it) }

    override val state = merge(
        actions,
        thunks,
    ).buffer()
        .dispatchStateScan(initialState, reducer::invoke)
        .optionallyDebug(debugIdentifier) { " STATE: $it" }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = initialState,
        )
}

/**
 * Extension function to scan the flow of [Dispatchable] and reduce it to a State.
 */
fun <TState : Any, TAction : Any> Flow<Dispatchable>.dispatchStateScan(
    initial: TState,
    reducer: (accumulator: TState, value: TAction) -> TState,
): Flow<TState> {
    // Local state to be reused for new subscribers to the same instance
    var cachedState = initial

    return channelFlow {

        val continuationInterceptorOrNull = currentCoroutineContext()[ContinuationInterceptor.Key]
        val currentDispatcher =
            (continuationInterceptorOrNull as? CoroutineDispatcher) ?: Dispatchers.Default

        // Record a new CoroutineContext - copy the currently scoped CoroutineDispatcher,
        // but only permit a single coroutine to execute on it at a time.
        // Ensure State access and Action dispatch is enqueued when concurrent Dispatchables are executed
        val singleParallelismContext = currentDispatcher.limitedParallelism(parallelism = 1)
            .let { newDispatcher ->
                @OptIn(ExperimentalCoroutinesApi::class) newCoroutineContext(newDispatcher)
            }

        // Honour standard scan behaviour and emit the initial State
        var state: TState = cachedState
        send(state)

        // Share a common Action dispatch handler
        val dispatchIntoProducer: suspend ProducerScope<TState>.(
            action: TAction,
        ) -> TState = { action ->
            withContext(singleParallelismContext) {
                // Reduce the dispatched Action into a new State
                state = reducer(state, action)

                // Record the State for future subscriptions
                cachedState = state

                // Emit it downstream, can be consumed to update View State
                send(state)

                // Return it to the dispatcher
                state
            }
        }

        val thunkScope = object : Scope<TState, TAction> {
            override suspend fun dispatchAction(action: TAction): TState =
                dispatchIntoProducer(action)

            override suspend fun getState(): TState =
                withContext(singleParallelismContext) { cachedState }
        }

        collect { value ->
            // Interpret an MVI Dispatchable as either a plain Action, or a Thunk
            @Suppress("UNCHECKED_CAST")
            when (value) {
                is Dispatchable.Action<*> -> dispatchIntoProducer(value.action as TAction)
                is Dispatchable.Thunk<*, *> -> with(value.thunk as Thunk<TState, TAction>) {
                    launch { thunkScope.invoke() }
                }
            }
        }
    }
}

/**
 * Extension function to optionally log debug messages.
 */
inline fun <T> Flow<T>.optionallyDebug(
    debugIdentifier: String?,
    crossinline message: (T) -> String,
) = if (debugIdentifier != null) onEach { log(debugIdentifier, message(it)) } else this

/**
 * Logs a debug message.
 */
fun log(debugIdentifier: String, message: String) {
    println("::MVI-$debugIdentifier: $message")
}
