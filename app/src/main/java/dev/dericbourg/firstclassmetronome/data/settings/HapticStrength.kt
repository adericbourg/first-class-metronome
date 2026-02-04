package dev.dericbourg.firstclassmetronome.data.settings

enum class HapticStrength(val amplitude: Int, val durationMs: Long) {
    LIGHT(64, 30L),
    MEDIUM(128, 50L),
    STRONG(192, 80L)
}
