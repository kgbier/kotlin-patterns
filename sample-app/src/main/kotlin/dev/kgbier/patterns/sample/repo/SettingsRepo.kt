package dev.kgbier.patterns.sample.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.update

data class Settings(
    val thingy: Boolean = true,
    val widget: Boolean = true,
    val flim: Boolean = false,
    val flam: String = "Value",
    val gizmo: Boolean = false,
)

class SettingsRepo {

    private val fakeSettings = MutableStateFlow(Settings())

    fun subscribe(): Flow<Settings> = fakeSettings.asFlow()

    fun poke() {
        fakeSettings.update { updateOptions.random() }
    }

    private val updateOptions = listOf(
        Settings(),
        Settings(thingy = false),
        Settings(widget = false),
        Settings(flim = true, flam = "Amount"),
        Settings(gizmo = true),
    )
}
