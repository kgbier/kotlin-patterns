package dev.kgbier.patterns.sample.screen.dashboard

import dev.kgbier.patterns.xstore.Reducer

object DashboardReducer : Reducer<DashboardState, DashboardAction> {
    override fun invoke(
        state: DashboardState,
        action: DashboardAction,
    ): DashboardState = when (action) {
        is DashboardAction.ReceiveErrors -> state.copy(errors = action.errors)
        is DashboardAction.ReceiveIssues -> state.copy(issues = action.issues)
        is DashboardAction.ReceiveActivity -> state.copy(activity = action.activity)
    }
}
