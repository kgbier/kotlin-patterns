package dev.kgbier.patterns.sample.screen.settings

sealed interface SettingsAction {
    data class ReceiveRemoteSettings(val settings: SettingsState.RemoteSettings) : SettingsAction
    data object ToggleThingy : SettingsAction
    data object ToggleWidget : SettingsAction
    data object ToggleFlim : SettingsAction
    data object ToggleGizmo : SettingsAction
    data class UpdateFlam(val string: String) : SettingsAction
}
