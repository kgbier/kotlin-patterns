package dev.kgbier.patterns.sample.screen.settings

import dev.kgbier.patterns.xmvi.MviViewModel
import dev.kgbier.patterns.xmvi.createMviViewModel
import kotlinx.coroutines.CoroutineScope

class SettingsViewModel(
    scope: CoroutineScope,
    processor: SettingsProcessor,
) : MviViewModel<SettingsIntent, SettingsViewState> by createMviViewModel(
    scope = scope,
    processor = processor,
    initialState = initialState,
    reducer = SettingsReducer,
    viewStateMapper = SettingsViewStateMapper,
    debugIdentifier = "SettingsViewModel"
) {

    companion object {
        val initialState: SettingsState = TODO()
    }
}