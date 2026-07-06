package dev.kgbier.patterns.xmvi

import dev.kgbier.patterns.xstore.DefaultXStore
import dev.kgbier.patterns.xstore.Reducer
import dev.kgbier.patterns.xstore.optionallyDebug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Interface representing a ViewModel in the MVI architecture.
 */
interface MviViewModel<TIntent, TViewState> {

    /**
     * A user interaction, producing a single Intent.
     */
    fun interact(intent: TIntent)

    /**
     * The current View State.
     */
    val viewState: StateFlow<TViewState>
}

/**
 * Creates an instance of MviViewModel.
 */
inline fun <
        reified TIntent : Any,
        reified TState : Any,
        reified TViewState : Any,
        reified TAction : Any,
        > createMviViewModel(
    scope: CoroutineScope,
    processor: IntentProcessor<TIntent, TState, TAction>,
    initialState: TState,
    reducer: Reducer<TState, TAction>,
    viewStateMapper: ViewStateMapper<TState, TViewState>,
    interactor: Interactor<TIntent> = DefaultInteractor(),
    debugIdentifier: String? = null,
) = DefaultMviViewModel(
    scope = scope,
    processor = processor,
    interactor = interactor,
    initialState = initialState,
    reducer = reducer,
    viewStateMapper = viewStateMapper,
    debugIdentifier = debugIdentifier,
)

/**
 * A deconstructed View Model.
 */
class DefaultMviViewModel<
        TIntent : Any,
        TState : Any,
        TViewState : Any,
        TAction : Any,
        >(
    scope: CoroutineScope,
    val interactor: Interactor<TIntent> = DefaultInteractor(),
    val processor: IntentProcessor<TIntent, TState, TAction>,
    initialState: TState,
    reducer: Reducer<TState, TAction>,
    viewStateMapper: ViewStateMapper<TState, TViewState>,
    debugIdentifier: String? = null,
) : MviViewModel<TIntent, TViewState> {

    val store = DefaultXStore(
        scope = scope,
        initialState = initialState,
        reducer = reducer,
        processor = processor,
        debugIdentifier = debugIdentifier,
    )

    override fun interact(intent: TIntent) = interactor.interact(intent)

    private val intents = interactor.userIntentChannel.receiveAsFlow()
        .optionallyDebug(debugIdentifier) { "INTENT: $it" }
        .onEach { scope.launch { processor.process(it) } }
        .mapNotNull { null } // Erases the output of this flow

    override val viewState = merge(
        intents, // Runs the Intents stream hot while a ViewState is observed
        store.state,
    ).buffer()
        .map(viewStateMapper::invoke)
        .optionallyDebug(debugIdentifier) { "VSTATE: $it" }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = viewStateMapper(initialState),
        )
}

