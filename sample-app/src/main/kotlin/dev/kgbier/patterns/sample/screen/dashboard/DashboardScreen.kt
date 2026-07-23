@file:OptIn(ExperimentalMaterial3Api::class)

package dev.kgbier.patterns.sample.screen.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.kgbier.patterns.sample.components.rememberLocalTextSizeDp
import dev.kgbier.patterns.xmvi.MviViewModel
import dev.kgbier.patterns.xmvi.createMviViewModel
import kotlinx.coroutines.CoroutineScope
import kotlin.random.Random

class DashboardViewModel(
    scope: CoroutineScope,
    val processor: DashboardProcessor = DashboardProcessor(scope = scope),
) : MviViewModel<DashboardIntent, DashboardViewState> by createMviViewModel(
    scope = scope,
    processor = processor,
    initialState = initialState,
    reducer = DashboardReducer,
    viewStateMapper = DashboardViewStateMapper,
    debugIdentifier = "DashboardViewModel",
) {
    companion object {
        val initialState = DashboardState(
            errors = null,
            issues = null,
            activity = null
        )
    }
}

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onBackPressed: (() -> Unit)? = null,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: DashboardViewModel = remember {
        DashboardViewModel(
            scope = scope,
        )
    },
) = Column(modifier = modifier) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    TopAppBar(
        title = { Text("Dashboard") },
        navigationIcon = {
            onBackPressed?.let {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        }
    )

    HorizontalDivider()

    DashboardContent(
        state = state,
        onIntent = viewModel::interact,
    )
}

@Composable
fun DashboardContent(
    state: DashboardViewState,
    onIntent: (DashboardIntent) -> Unit,
) {
    when (state) {
        DashboardViewState.Wireframe -> DashboardWireframe()
        is DashboardViewState.Dashboard -> DashboardMain(
            state = state,
            onIntent = onIntent,
        )
    }
}

@Composable
fun DashboardWireframe() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Box {
                Card {
                    Spacer(
                        modifier = Modifier.size(
                            width = 240.dp,
                            height = rememberLocalTextSizeDp(style = MaterialTheme.typography.displaySmall),
                        )
                    )
                }
            }
        }
        item { MetricTileWireframe() }
        item { MetricTileWireframe() }
        item(span = { GridItemSpan(2) }) {
            MetricTileWireframe()
        }
    }
}

@Composable
fun DashboardMain(
    state: DashboardViewState.Dashboard,
    onIntent: (DashboardIntent) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text("Headline", style = MaterialTheme.typography.displaySmall)
        }
        item {
            DashboardMetricTile(
                metric = state.errors,
                onRetryClicked = { onIntent(DashboardIntent.RetryErrors) },
            )
        }
        item {
            DashboardMetricTile(
                metric = state.issues,
                onRetryClicked = { onIntent(DashboardIntent.RetryIssues) },
            )
        }
        item(span = { GridItemSpan(2) }) {
            DashboardHistogramTile(
                histogram = state.activity,
                onRetryClicked = { onIntent(DashboardIntent.RetryActivity) },
            )
        }
    }
}


@Composable
fun DashboardMetricTile(
    metric: DashboardViewState.MetricState,
    onRetryClicked: () -> Unit,
) {
    when (metric) {
        DashboardViewState.MetricState.Loading -> MetricTileWireframe()
        DashboardViewState.MetricState.Error -> ErrorTile(onRetryClicked)
        is DashboardViewState.MetricState.Metric -> MetricTile(
            title = metric.title,
            value = metric.value
        )
    }
}

@Composable
fun DashboardHistogramTile(
    histogram: DashboardViewState.HistogramState,
    onRetryClicked: () -> Unit,

    ) {
    when (histogram) {
        DashboardViewState.HistogramState.Loading -> MetricTileWireframe()
        DashboardViewState.HistogramState.Error -> ErrorTile(onRetryClicked)
        is DashboardViewState.HistogramState.Histogram -> HistogramTile(
            title = histogram.title,
            values = histogram.values,
        )
    }
}

@Preview
@Composable
fun DashboardContentPreviewWireframe() = DashboardContent(
    state = DashboardViewState.Wireframe,
    onIntent = {},
)

@Preview
@Composable
fun DashboardContentPreviewIntermediate() = DashboardContent(
    state = DashboardViewState.Dashboard(
        errors = DashboardViewState.MetricState.Error,
        issues = DashboardViewState.MetricState.Metric("Issues", "201k"),
        activity = DashboardViewState.HistogramState.Loading,
    ),
    onIntent = {},
)

@Preview
@Composable
fun DashboardContentPreview() = DashboardContent(
    state = DashboardViewState.Dashboard(
        errors = DashboardViewState.MetricState.Metric("Errors", "77k"),
        issues = DashboardViewState.MetricState.Metric("Issues", "201k"),
        activity = DashboardViewState.HistogramState.Histogram(
            title = "Activity",
            values = List(16) { Random.nextFloat() }),
    ),
    onIntent = {},
)
