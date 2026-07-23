package dev.kgbier.patterns.sample.screen.dashboard

sealed interface DashboardIntent {
    data object RetryErrors : DashboardIntent
    data object RetryIssues : DashboardIntent
    data object RetryActivity : DashboardIntent
}
