package dev.kgbier.patterns.sample.screen.settings

import dev.kgbier.patterns.sample.repo.Settings
import dev.kgbier.patterns.xmvi.MviViewModel
import dev.kgbier.patterns.xmvi.createMviViewModel
import kotlinx.coroutines.CoroutineScope

class SettingsViewModel(
    scope: CoroutineScope,
    val processor: SettingsProcessor,
) : MviViewModel<SettingsIntent, SettingsViewState> by createMviViewModel(
    scope = scope,
    processor = processor,
    initialState = buildInitialState(),
    reducer = SettingsReducer,
    viewStateMapper = SettingsViewStateMapper,
    debugIdentifier = "SettingsViewModel",
) {

    companion object {
        fun buildInitialState(
            settings: Settings = Settings(),
        ): SettingsState = initialState.copy(
            remoteSettings = settings.toRemoteSettings()
        )

        val initialState: SettingsState = SettingsState(
            areGizmoTermsAgreed = false,

            remoteSettings = SettingsState.RemoteSettings(
                isThingyEnabled = false,
                isWidgetEnabled = true,
                isFlimEnabled = false,
                flamString = "",
                isGizmoEnabled = false,
            ),
            localSettings = SettingsState.LocalSettings(),
        )
    }
}
