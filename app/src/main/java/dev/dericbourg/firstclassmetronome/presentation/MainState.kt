package dev.dericbourg.firstclassmetronome.presentation

import dev.dericbourg.firstclassmetronome.data.settings.AppSettings
import dev.dericbourg.firstclassmetronome.data.settings.ThemeMode

data class MainState(
    val themeMode: ThemeMode = AppSettings.DEFAULT_THEME_MODE,
    val dynamicColorsEnabled: Boolean = AppSettings.DEFAULT_DYNAMIC_COLORS_ENABLED
)
