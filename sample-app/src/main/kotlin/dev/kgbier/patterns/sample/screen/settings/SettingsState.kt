package dev.kgbier.patterns.sample.screen.settings

import dev.kgbier.patterns.sample.repo.Settings

data class SettingsState(
    val areGizmoTermsAgreed: Boolean,
    val remoteSettings: RemoteSettings,
    val localSettings: LocalSettings,
) {

    data class RemoteSettings(
        val isThingyEnabled: Boolean,
        val isWidgetEnabled: Boolean,
        val isFlimEnabled: Boolean,
        val flamString: String,
        val isGizmoEnabled: Boolean,
    )

    data class LocalSettings(
        val isThingyEnabled: Boolean? = null,
        val isWidgetEnabled: Boolean? = null,
        val isFlimEnabled: Boolean? = null,
        val flamString: String? = null,
        val isGizmoEnabled: Boolean? = null,
    )
}

internal fun Settings.toRemoteSettings() = SettingsState.RemoteSettings(
    isThingyEnabled = thingy,
    isWidgetEnabled = widget,
    isFlimEnabled = flim,
    flamString = flam,
    isGizmoEnabled = gizmo,
)

data class RemoteSettingsUpdate(
    val isThingyEnabled: Boolean?,
    val isWidgetEnabled: Boolean?,
    val isFlimEnabled: Boolean?,
    val flamString: String?,
    val isGizmoEnabled: Boolean?,
) {
    val isDirty: Boolean
        get() = !(isThingyEnabled == null
                && isWidgetEnabled == null
                && isFlimEnabled == null
                && flamString == null
                && isGizmoEnabled == null)

}

internal fun SettingsState.buildRemoteUpdate(): RemoteSettingsUpdate = RemoteSettingsUpdate(
    isThingyEnabled = if (localSettings.isThingyEnabled != null && localSettings.isThingyEnabled != remoteSettings.isThingyEnabled) localSettings.isThingyEnabled else null,
    isWidgetEnabled = if (localSettings.isWidgetEnabled != null && localSettings.isWidgetEnabled != remoteSettings.isWidgetEnabled) localSettings.isWidgetEnabled else null,
    isFlimEnabled = if (localSettings.isFlimEnabled != null && localSettings.isFlimEnabled != remoteSettings.isFlimEnabled) localSettings.isFlimEnabled else null,
    flamString = if (localSettings.flamString != null && localSettings.flamString != remoteSettings.flamString) localSettings.flamString else null,
    isGizmoEnabled = if (localSettings.isGizmoEnabled != null && localSettings.isGizmoEnabled != remoteSettings.isGizmoEnabled) localSettings.isGizmoEnabled else null,
)