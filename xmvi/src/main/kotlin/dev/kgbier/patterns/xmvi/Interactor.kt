package dev.kgbier.patterns.xmvi

import kotlinx.coroutines.channels.Channel

/**
 * Receives User Intents.
 */
interface Interactor<TIntent> {
    val userIntentChannel: Channel<TIntent>

    /**
     * Posts a User Intent.
     */
    fun interact(intent: TIntent) {
        userIntentChannel.trySend(intent)
    }

    /**
     * Posts a User Intent.
     */
    fun TIntent.post() = interact(this)
}

/**
 * Default implementation of Interactor.
 *
 * Opines on the buffer capacity of the [userIntentChannel],
 * limiting the number of concurrent actions the user can submit.
 */
class DefaultInteractor<TIntent> : Interactor<TIntent> {
    override val userIntentChannel: Channel<TIntent> = Channel(capacity = 1)
}

/**
 * Creates a default instance of Interactor.
 */
inline fun <reified TIntent> defaultInteractor() = DefaultInteractor<TIntent>()
