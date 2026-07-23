package dev.kgbier.patterns.sample.screen.settings

sealed interface SettingsAction {
    data class ReceiveRemoteSettings(val settings: SettingsState.RemoteSettings) : SettingsAction

    data object ToggleThingy : SettingsAction

    data class ToggleWidget(val enabled: Boolean) : SettingsAction

    data object ToggleFlim : SettingsAction
    data class UpdateFlam(val string: String) : SettingsAction

    data object ToggleGizmo : SettingsAction
    data object AcceptGizmoTerms : SettingsAction
}
