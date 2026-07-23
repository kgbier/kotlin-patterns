package dev.kgbier.patterns.sample.screen.settings

import dev.kgbier.patterns.xstore.Reducer

object SettingsReducer : Reducer<SettingsState, SettingsAction> {
    override fun invoke(
        state: SettingsState,
        action: SettingsAction,
    ): SettingsState = when (action) {
        is SettingsAction.ReceiveRemoteSettings -> state.copy(remoteSettings = action.settings)

        SettingsAction.ToggleThingy -> state.copy(
            localSettings = state.localSettings.copy(
                isThingyEnabled = !(state.localSettings.isThingyEnabled
                    ?: state.remoteSettings.isThingyEnabled)
            )
        )

        is SettingsAction.ToggleWidget -> state.copy(
            localSettings = state.localSettings.copy(
                isWidgetEnabled = action.enabled,
            )
        )

        SettingsAction.ToggleFlim -> state.copy(
            localSettings = state.localSettings.copy(
                isFlimEnabled = !(state.localSettings.isFlimEnabled
                    ?: state.remoteSettings.isFlimEnabled)
            )
        )

        is SettingsAction.UpdateFlam -> state.copy(
            localSettings = state.localSettings.copy(
                flamString = action.string
            )
        )

        SettingsAction.ToggleGizmo -> state.copy(
            localSettings = state.localSettings.copy(
                isGizmoEnabled = !(state.localSettings.isGizmoEnabled
                    ?: state.remoteSettings.isGizmoEnabled)
            )
        )

        SettingsAction.AcceptGizmoTerms -> state.copy(areGizmoTermsAgreed = true)
    }
}
