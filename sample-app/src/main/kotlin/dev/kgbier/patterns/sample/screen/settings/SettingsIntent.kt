package dev.kgbier.patterns.sample.screen.settings

sealed interface SettingsIntent {
    data object ToggleThingy : SettingsIntent

    data object EnableWidget : SettingsIntent
    data object DisableWidget : SettingsIntent

    data object ToggleFlim : SettingsIntent
    data class UpdateFlam(val string: String) : SettingsIntent

    data object ToggleGizmo : SettingsIntent
    data object AcceptGizmoTerms : SettingsIntent
}
