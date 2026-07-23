package dev.kgbier.patterns.sample.screen.dashboard

data class DashboardState(
    val errors: Result<Int>?,
    val issues: Result<Int>?,
    val activity: Result<List<Int>>?,
)
