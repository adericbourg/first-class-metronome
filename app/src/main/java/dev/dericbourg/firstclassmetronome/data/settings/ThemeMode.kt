package dev.dericbourg.firstclassmetronome.data.settings

enum class ThemeMode {
    SYSTEM_DEFAULT,
    LIGHT,
    DARK;

    companion object {
        fun fromOrdinal(ordinal: Int): ThemeMode =
            entries.getOrNull(ordinal) ?: SYSTEM_DEFAULT
    }
}

val ThemeMode.displayName: String
    get() = when (this) {
        ThemeMode.SYSTEM_DEFAULT -> "Follow system settings"
        ThemeMode.LIGHT -> "Light"
        ThemeMode.DARK -> "Dark"
    }
