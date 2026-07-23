package dev.kgbier.patterns.sample.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableCard(
    titleContent: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit),
) {
    Card(modifier = modifier) {
        var isExpanded by remember { mutableStateOf(false) }
        Column {
            Row(
                modifier = Modifier
                    .clickable(onClick = { isExpanded = !isExpanded })
                    .padding(8.dp)
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.titleLarge,
                ) {
                    titleContent()
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(if (isExpanded) 180f else 0f)
                )
            }

            AnimatedVisibility(
                visible = isExpanded
            ) {
                HorizontalDivider()
                Column(modifier = Modifier.padding(8.dp)) {
                    content()
                }
            }
        }
    }
}