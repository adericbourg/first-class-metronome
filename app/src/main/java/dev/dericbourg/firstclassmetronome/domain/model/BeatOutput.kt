package dev.dericbourg.firstclassmetronome.domain.model

/**
 * What a single beat of a measure produces.
 *
 * - [NoSound]: silence, no vibration.
 * - [HapticOnly]: a vibration only, no click. Fires regardless of the master haptic setting.
 * - [Sound]: a click sound; vibrates only when the master haptic setting is enabled.
 */
sealed interface BeatOutput {
    data object NoSound : BeatOutput
    data object HapticOnly : BeatOutput
    data class Sound(val sound: ClickSound) : BeatOutput
}

/** Available click sounds. Only one exists today. The audio layer maps these to raw resources. */
enum class ClickSound {
    CLICK
}
