package dev.kgbier.patterns.xstore

/**
 * Reduces a State and an Action to a new State.
 */
fun interface Reducer<TState : Any, TAction : Any> {
    /**
     * Reduces the given State and Action into a new state.
     *
     * @param state The current State.
     * @param action The Action to process.
     * @return The new State.
     */
    operator fun invoke(state: TState, action: TAction): TState
}
