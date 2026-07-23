package dev.kgbier.patterns.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.kgbier.patterns.sample.components.ItemRow

@Composable
fun MainScreen(
    navigate: (Route) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        ItemRow(
            title = "Settings",
            subtitle = "A live settings page which determines if submitting an update is necessary",
            icon = Icons.Default.Settings,
            onClick = { navigate(Route.Settings) }
        )
        HorizontalDivider()
        ItemRow(
            title = "Dashboard",
            subtitle = "A dashboard with a wireframe loader that hosts a variety of asynchronous widgets",
            icon = Icons.Default.Home,
            onClick = { navigate(Route.Dashboard) }
        )
        HorizontalDivider()
        ItemRow(
            title = "To-Do",
            subtitle = "A To-Do list featuring optimistic updates and automated rollback on error",
            icon = Icons.Default.CheckCircle,
            onClick = { }
        )
    }
}