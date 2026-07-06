package dev.kgbier.patterns.xmvi

import dev.kgbier.patterns.xstore.DefaultProcessor
import dev.kgbier.patterns.xstore.Processor
import dev.kgbier.patterns.xstore.defaultProcessor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Processes user activity, activity and dispatches events to the store.
 */
interface IntentProcessor<TIntent : Any, TState : Any, TAction : Any> : Processor<TState, TAction> {

    /**
     * Processes an Intent.
     *
     * @param intent The Intent to process.
     */
    suspend fun process(intent: TIntent)
}

/**
 * Default implementation of Processor.
 *
 * Opines on the behaviour of [actionChannel] and [thunkChannel] - exposing them as unbuffered channels.
 */
class DefaultIntentProcessor<TIntent : Any, TState : Any, TAction : Any>(
    delegate: Processor<TState, TAction> = DefaultProcessor(),
) :
    IntentProcessor<TIntent, TState, TAction>,
    Processor<TState, TAction> by delegate {

    /**
     * Override this to observe Intents.
     */
    override suspend fun process(intent: TIntent) = Unit
}

/**
 * Creates a default instance of Processor.
 */
inline fun <reified TIntent : Any, reified TState : Any, reified TAction : Any> defaultIntentProcessor(
    actions: Flow<TAction> = emptyFlow(),
) = DefaultIntentProcessor<TIntent, TState, TAction>(delegate = defaultProcessor(actions = actions))

