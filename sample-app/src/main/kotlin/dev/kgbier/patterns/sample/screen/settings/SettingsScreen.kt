@file:OptIn(ExperimentalMaterial3Api::class)

package dev.kgbier.patterns.sample.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.kgbier.patterns.sample.components.ExpandableCard
import dev.kgbier.patterns.sample.components.RadioRow
import dev.kgbier.patterns.sample.components.SwitchRow
import dev.kgbier.patterns.sample.repo.Settings
import dev.kgbier.patterns.sample.repo.SettingsRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop

@Composable
fun SettingsScreen(
    settingsRepo: SettingsRepo,
    modifier: Modifier = Modifier,
    onBackPressed: (() -> Unit)? = null,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: SettingsViewModel = remember {
        SettingsViewModel(
            processor = SettingsProcessor(
                settingsRepo = settingsRepo,
            ), scope = scope
        )
    },
) = Column(modifier = modifier) {

    val state by viewModel.viewState.collectAsStateWithLifecycle()

    TopAppBar(
        title = { Text("Settings") },
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

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        ExpandableCard(
            titleContent = { Text("Debug") },
            modifier = Modifier.padding(8.dp)
        ) {
            val remoteState by settingsRepo.subscribe()
                .collectAsStateWithLifecycle(Settings())
            val localState by viewModel.processor.sidechannelStateFlow.collectAsStateWithLifecycle()

            Text("Remote Settings:", style = MaterialTheme.typography.titleSmall)
            Text(remoteState.toString())

            HorizontalDivider()

            val remoteUpdate = localState.buildRemoteUpdate()
            Text(
                "Delta:",
                style = MaterialTheme.typography.titleSmall
            )
            Text(remoteUpdate.toString())

            HorizontalDivider()

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    when (remoteUpdate.isDirty) {
                        true -> {
                            Text(
                                "Update State: Dirty",
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text("a submission is needed")
                        }

                        false -> {
                            Text(
                                "Update State: Clean",
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text("in-sync with the remote configuration")
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = { settingsRepo.poke() },
                    modifier = Modifier.weight(1f)
                ) { Text("Trigger remote update") }
            }
        }

        HorizontalDivider()

        SettingsList(
            state = state,
            interact = viewModel::interact,
        )
    }
}

@Composable
fun SettingsList(
    state: SettingsViewState,
    interact: (SettingsIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier = modifier) {
    SwitchRow(
        label = "Thingy",
        isChecked = state.isThingyEnabled,
        onCheckedChange = { interact(SettingsIntent.ToggleThingy) },
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )

    OutlinedCard(
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        RadioRow(
            isChecked = state.widgetSettings is SettingsViewState.WidgetSettings.Disabled,
            onClick = { interact(SettingsIntent.DisableWidget) },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("No Widget", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        }

        HorizontalDivider()

        RadioRow(
            isChecked = state.widgetSettings is SettingsViewState.WidgetSettings.Enabled,
            onClick = { interact(SettingsIntent.EnableWidget) },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("With Widget", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        }

        AnimatedVisibility(
            visible = state.widgetSettings.isEnabled,
        ) {
            Column {
                val widgetSettings =
                    state.widgetSettings as? SettingsViewState.WidgetSettings.Enabled

                SwitchRow(
                    label = "Flim",
                    isChecked = widgetSettings?.isFlimEnabled ?: false,
                    onCheckedChange = { interact(SettingsIntent.ToggleFlim) },
                    modifier = Modifier.padding(8.dp)
                )

                val flamTextState =
                    rememberTextFieldState(initialText = widgetSettings?.flamString ?: "")

                LaunchedEffect(flamTextState) {
                    snapshotFlow { flamTextState.text.toString() }.drop(1).collectLatest {
                        interact(SettingsIntent.UpdateFlam(it))
                    }
                }

                OutlinedTextField(
                    state = flamTextState,
                    label = { Text("Flam") },
                    enabled = widgetSettings?.isFlimEnabled ?: false,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                )
            }
        }
    }

    SwitchRow(
        label = "Gizmo",
        isChecked = state.isGizmoEnabled,
        isEnabled = state.isGizmoAllowed,
        onCheckedChange = { interact(SettingsIntent.ToggleGizmo) },
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
    if (!state.isGizmoAllowed) {
        Card(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    "You need to accept these terms to enable Gizmo",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("By accessing Gizmo functionality, you accept and agree to be bound by these terms and conditions.")
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { interact(SettingsIntent.AcceptGizmoTerms) },
                    modifier = Modifier.align(Alignment.End)
                ) { Text("Accept") }
            }
        }
    }
}


@Preview
@Composable
fun SettingsScreenPreview() {
    Surface {
        SettingsList(
            state = SettingsViewState(
                isThingyEnabled = false,
                widgetSettings = SettingsViewState.WidgetSettings.Enabled(
                    isFlimEnabled = true,
                    flamString = null,
                ),
                isGizmoEnabled = false,
                isGizmoAllowed = false,
            ),
            interact = {},
        )
    }
}