package dev.kgbier.patterns.xmvi

/**
 * Maps a State to a View State.
 */
fun interface ViewStateMapper<TState, TViewState> {
    /**
     * Maps the given State to a View State.
     *
     * @param state The State to map.
     * @return The mapped View State.
     */
    operator fun invoke(state: TState): TViewState
}
