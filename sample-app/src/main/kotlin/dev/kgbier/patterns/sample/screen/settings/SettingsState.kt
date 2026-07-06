package dev.kgbier.patterns.sample.screen.settings

data class SettingsState(
    val areGizmoTermsAgreed: Boolean,
    val remoteSettings: RemoteSettings,
    val localSettings: LocalSettings,
) {

    data class RemoteSettings(
        val isThingyEnabled: Boolean,
        val isWidgetEnabled: Boolean,
        val isFlimEnabled: Boolean,
        val flamString: Boolean,
        val isGizmoEnabled: Boolean,
    )

    data class LocalSettings(
        val isThingyEnabled: Boolean?,
        val isWidgetEnabled: Boolean?,
        val isFlimEnabled: Boolean?,
        val flamString: Boolean?,
        val isGizmoEnabled: Boolean?,
    )
}

