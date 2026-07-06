package dev.kgbier.patterns.sample.screen.settings

sealed interface SettingsIntent {
    data object ToggleThingy : SettingsIntent
    data object ToggleWidget : SettingsIntent
    data object ToggleFlim : SettingsIntent
    data object ToggleGizmo : SettingsIntent
    data class UpdateFlam(val string: String) : SettingsIntent
}
