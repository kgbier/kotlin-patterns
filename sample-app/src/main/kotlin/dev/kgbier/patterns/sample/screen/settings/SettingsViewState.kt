package dev.kgbier.patterns.sample.screen.settings

data class SettingsViewState(
    val isThingyEnabled: Boolean,
    val widgetSettings: WidgetSettings,
    val isGizmoEnabled: Boolean,
    val isGizmoAllowed: Boolean,
) {
    sealed interface WidgetSettings {
        data object Disabled : WidgetSettings

        data class Enabled(
            val isFlimEnabled: Boolean,
            val flamString: String?,
        ) : WidgetSettings

        val isEnabled: Boolean
            get() = this is Enabled
    }
}

