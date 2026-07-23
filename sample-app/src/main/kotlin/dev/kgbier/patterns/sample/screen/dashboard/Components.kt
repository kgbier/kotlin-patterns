package dev.kgbier.patterns.sample.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.withCompositionLocal
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import dev.kgbier.patterns.sample.components.rememberLocalTextSizeDp

@Composable
fun MetricTile(
    title: String,
    value: String,
    contentPadding: PaddingValues = PaddingValues(16.dp),
) = MetricTile(
    title = { Text(title) },
    value = { Text(value) },
    contentPadding = contentPadding,
)

@Composable
fun MetricTileWireframe(
    contentPadding: PaddingValues = PaddingValues(16.dp),
) {
    val textMeasurer = rememberTextMeasurer()
    MetricTile(
        title = { Spacer(modifier = Modifier.size(rememberLocalTextSizeDp(textMeasurer = textMeasurer))) },
        value = { Spacer(modifier = Modifier.size(rememberLocalTextSizeDp(textMeasurer = textMeasurer))) },
        contentPadding = contentPadding,
    )
}

@Composable
fun MetricTile(
    title: @Composable () -> Unit,
    value: @Composable () -> Unit,
    contentPadding: PaddingValues = PaddingValues(16.dp),
) {
    Card {
        Column(modifier = Modifier.padding(contentPadding)) {
            withCompositionLocal(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                title()
            }
            withCompositionLocal(LocalTextStyle provides MaterialTheme.typography.displayLarge) {
                value()
            }
        }
    }
}

@Composable
fun HistogramTile(
    title: String,
    values: List<Float>,
    contentPadding: PaddingValues = PaddingValues(16.dp),
) {
    Card {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(contentPadding)
        ) {
            Text(title)
            Row(
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                val maxVal = remember(values) { values.maxOrNull() ?: 1f }
                values.forEach { value ->
                    val fraction = if (maxVal > 0f) value / maxVal else 0f
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .fillMaxHeight(fraction)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorTile(
    onRetryClicked: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(16.dp),
) {
    Card {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Text("Error, could not load")
            TextButton(onClick = onRetryClicked) {
                Icon(
                    imageVector = Icons.Default.Refresh, contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Retry")
            }
        }
    }
}