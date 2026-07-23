package dev.kgbier.patterns.sample.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SwitchRow(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier,
) = SwitchRow(
    isChecked = isChecked,
    onCheckedChange = onCheckedChange,
    isEnabled = isEnabled,
    modifier = modifier,
) {
    Text(label, modifier = Modifier.weight(1f))
}

@Composable
fun SwitchRow(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) = Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
    content()
    Switch(checked = isChecked, onCheckedChange = onCheckedChange, enabled = isEnabled)
}
