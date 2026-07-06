package dev.kgbier.patterns.sample.screen.settings

import dev.kgbier.patterns.xmvi.IntentProcessor
import dev.kgbier.patterns.xmvi.defaultIntentProcessor

class SettingsProcessor(

) : IntentProcessor<SettingsIntent, SettingsState, SettingsAction> by defaultIntentProcessor() {
    override suspend fun process(intent: SettingsIntent) = when (intent) {
        SettingsIntent.ToggleFlim -> TODO()
        SettingsIntent.ToggleGizmo -> TODO()
        SettingsIntent.ToggleThingy -> TODO()
        SettingsIntent.ToggleWidget -> TODO()
        is SettingsIntent.UpdateFlam -> TODO()
    }
}