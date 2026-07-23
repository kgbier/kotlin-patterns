package dev.kgbier.patterns.sample.screen.dashboard

import dev.kgbier.patterns.xmvi.IntentProcessor
import dev.kgbier.patterns.xmvi.defaultIntentProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class DashboardProcessor(
    val scope: CoroutineScope,
) : IntentProcessor<DashboardIntent, DashboardState, DashboardAction> by defaultIntentProcessor() {

    override val actionStream: Flow<DashboardAction> = flow {
        loadErrors()
        loadIssues()
        loadActivity()
    }

    override suspend fun process(intent: DashboardIntent) = when (intent) {
        DashboardIntent.RetryActivity -> loadActivity(reload = true)
        DashboardIntent.RetryErrors -> loadErrors(reload = true)
        DashboardIntent.RetryIssues -> loadIssues(reload = true)
    }

    private fun loadErrors(reload: Boolean = false) = scope.launchThunk {
        if (reload) {
            DashboardAction.ReceiveErrors(null).dispatch()
        }

        simulate(77)
            .let { DashboardAction.ReceiveErrors(it) }.dispatch()
    }

    private fun loadIssues(reload: Boolean = false) = scope.launchThunk {
        if (reload) {
            DashboardAction.ReceiveIssues(null).dispatch()
        }

        simulate(220)
            .let { DashboardAction.ReceiveIssues(it) }.dispatch()
    }

    private fun loadActivity(reload: Boolean = false) = scope.launchThunk {
        if (reload) {
            DashboardAction.ReceiveActivity(null).dispatch()
        }

        simulate(List(16) { Random.nextInt(from = 1, until = 10) })
            .let { DashboardAction.ReceiveActivity(it) }.dispatch()
    }

    private suspend fun <T> simulate(value: T): Result<T> {
        delay(Random.nextInt(200, 1000).milliseconds)

        return if (Random.nextBoolean()) {
            Result.success(value)
        } else {
            Result.failure(Throwable())
        }
    }
}
