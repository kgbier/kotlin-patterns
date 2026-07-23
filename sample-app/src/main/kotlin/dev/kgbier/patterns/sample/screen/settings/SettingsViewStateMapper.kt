package dev.kgbier.patterns.sample.screen.settings

import dev.kgbier.patterns.xmvi.ViewStateMapper

object SettingsViewStateMapper : ViewStateMapper<SettingsState, SettingsViewState> {
    override fun invoke(state: SettingsState): SettingsViewState = SettingsViewState(
        isThingyEnabled = state.localSettings.isThingyEnabled ?: state.remoteSettings.isThingyEnabled,
        widgetSettings = when (state.localSettings.isWidgetEnabled ?: state.remoteSettings.isWidgetEnabled) {
            true -> SettingsViewState.WidgetSettings.Enabled(
                isFlimEnabled = state.localSettings.isFlimEnabled ?: state.remoteSettings.isFlimEnabled,
                flamString = state.localSettings.flamString ?: state.remoteSettings.flamString,
            )
            false -> SettingsViewState.WidgetSettings.Disabled
        },
        isGizmoEnabled = state.localSettings.isGizmoEnabled ?: state.remoteSettings.isGizmoEnabled,
        isGizmoAllowed = state.areGizmoTermsAgreed,
    )
}