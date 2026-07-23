package dev.kgbier.patterns.sample.screen.dashboard

import dev.kgbier.patterns.xmvi.ViewStateMapper

object DashboardViewStateMapper : ViewStateMapper<DashboardState, DashboardViewState> {
    override fun invoke(state: DashboardState): DashboardViewState {
        if (state.errors == null && state.issues == null && state.activity == null) {
            return DashboardViewState.Wireframe
        }

        return DashboardViewState.Dashboard(
            errors = state.errors?.fold(
                onSuccess = { DashboardViewState.MetricState.Metric("Errors", "${it}k") },
                onFailure = { DashboardViewState.MetricState.Error },
            ) ?: DashboardViewState.MetricState.Loading,

            issues = state.issues?.fold(
                onSuccess = { DashboardViewState.MetricState.Metric("Issues", "${it}k") },
                onFailure = { DashboardViewState.MetricState.Error },
            ) ?: DashboardViewState.MetricState.Loading,

            activity = state.activity?.fold(
                onSuccess = {
                    DashboardViewState.HistogramState.Histogram("Activity", it.map { it.toFloat() })
                },
                onFailure = { DashboardViewState.HistogramState.Error },
            ) ?: DashboardViewState.HistogramState.Loading,
        )
    }
}
