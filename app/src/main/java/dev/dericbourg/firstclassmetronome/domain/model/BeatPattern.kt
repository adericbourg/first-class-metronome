package dev.dericbourg.firstclassmetronome.domain.model

/**
 * Pure logic for a repeating measure of [BeatOutput]s.
 *
 * A pattern is an ordered list whose size is the number of beats. The metronome loops over it,
 * so beat index N maps to `pattern[N % size]`. Kept free of Android dependencies so it can be
 * unit-tested in isolation.
 */
object BeatPattern {

    val DEFAULT: List<BeatOutput> = listOf(BeatOutput.Sound(ClickSound.CLICK))

    /** Output for a given (unbounded) beat index, wrapping over the pattern length. */
    fun outputFor(pattern: List<BeatOutput>, index: Int): BeatOutput {
        if (pattern.isEmpty()) return BeatOutput.NoSound
        return pattern[index % pattern.size]
    }

    /**
     * Whether this beat should vibrate.
     * [BeatOutput.HapticOnly] always vibrates; [BeatOutput.Sound] follows the master setting.
     */
    fun shouldVibrate(output: BeatOutput, masterHapticEnabled: Boolean): Boolean = when (output) {
        BeatOutput.NoSound -> false
        BeatOutput.HapticOnly -> true
        is BeatOutput.Sound -> masterHapticEnabled
    }

    /** Serialize to an ordered, comma-delimited string for persistence. */
    fun encode(pattern: List<BeatOutput>): String = pattern.joinToString(SEPARATOR) { token(it) }

    /** Parse a string produced by [encode], falling back to [DEFAULT] on any malformed input. */
    fun decode(encoded: String): List<BeatOutput> {
        val parsed = runCatching {
            encoded.split(SEPARATOR)
                .filter { it.isNotEmpty() }
                .map { parseToken(it) }
        }.getOrNull()
        return parsed?.takeIf { it.isNotEmpty() } ?: DEFAULT
    }

    private fun token(output: BeatOutput): String = when (output) {
        BeatOutput.NoSound -> TOKEN_NO_SOUND
        BeatOutput.HapticOnly -> TOKEN_HAPTIC_ONLY
        is BeatOutput.Sound -> "$TOKEN_SOUND_PREFIX${output.sound.name}"
    }

    private fun parseToken(token: String): BeatOutput = when {
        token == TOKEN_NO_SOUND -> BeatOutput.NoSound
        token == TOKEN_HAPTIC_ONLY -> BeatOutput.HapticOnly
        token.startsWith(TOKEN_SOUND_PREFIX) ->
            BeatOutput.Sound(ClickSound.valueOf(token.removePrefix(TOKEN_SOUND_PREFIX)))
        else -> throw IllegalArgumentException("Unknown beat token: $token")
    }

    private const val SEPARATOR = ","
    private const val TOKEN_NO_SOUND = "N"
    private const val TOKEN_HAPTIC_ONLY = "H"
    private const val TOKEN_SOUND_PREFIX = "S:"
}
