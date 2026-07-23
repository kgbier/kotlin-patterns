package dev.kgbier.patterns.sample.screen.dashboard

sealed interface DashboardAction {
    data class ReceiveErrors(val errors: Result<Int>?) : DashboardAction
    data class ReceiveIssues(val issues: Result<Int>?) : DashboardAction
    data class ReceiveActivity(val activity: Result<List<Int>>?) : DashboardAction
}
