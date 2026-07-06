package dev.kgbier.patterns.xstore

/**
 * An event dispatched to the Store.
 */
sealed interface Dispatchable {
    /**
     * Wraps a [dev.kgbier.patterns.xstore.Thunk].
     *
     * @property thunk The Thunk.
     */
    @JvmInline
    value class Thunk<TState : Any, TAction : Any>(
        val thunk: dev.kgbier.patterns.xstore.Thunk<TState, TAction>,
    ) : Dispatchable

    /**
     * Wraps an Action.
     *
     * @property action The Action.
     */
    @JvmInline
    value class Action<TAction>(val action: TAction) : Dispatchable
}

/**
 * Represents a Thunk, which is a function that can dispatch additional Actions while accessing State.
 */
fun interface Thunk<TState : Any, TAction : Any> {
    /**
     * Invokes the Thunk within the provided scope.
     */
    suspend operator fun Scope<TState, TAction>.invoke()

    /**
     * A running scope for the Thunk, permitting Action dispatch and State access.
     */
    interface Scope<TState, TAction> {
        /**
         * Dispatches an asynchronous Action.
         *
         * @param action The asynchronous Action to dispatch.
         */
        suspend fun dispatchAction(action: TAction): TState

        /**
         * Extension function to dispatch the current asynchronous Action.
         */
        suspend fun TAction.dispatch(): TState = dispatchAction(this)

        /**
         * Retrieves the current State.
         *
         * @return The current State.
         */
        suspend fun getState(): TState
    }
}

