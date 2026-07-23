package dev.kgbier.patterns.sample.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun RadioRow(
    isChecked: Boolean,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) = Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
    content()
    RadioButton(selected = isChecked, onClick = onClick, enabled = isEnabled)
}

@Composable
fun RadioRow(
    label: String,
    isChecked: Boolean,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier,
) = RadioRow(
    isChecked = isChecked,
    onClick = onClick,
    isEnabled = isEnabled,
    modifier = modifier,
) {
    Text(label, modifier = Modifier.weight(1f))
}
