package dev.kgbier.patterns.sample.screen.dashboard

sealed interface DashboardViewState {
    data object Wireframe : DashboardViewState
    data class Dashboard(
        val errors: MetricState,
        val issues: MetricState,
        val activity: HistogramState,
    ) : DashboardViewState

    sealed interface MetricState {
        data class Metric(val title: String, val value: String) : MetricState
        data object Error : MetricState
        data object Loading : MetricState
    }

    sealed interface HistogramState {
        data class Histogram(val title: String, val values: List<Float>) : HistogramState
        data object Error : HistogramState
        data object Loading : HistogramState
    }
}
