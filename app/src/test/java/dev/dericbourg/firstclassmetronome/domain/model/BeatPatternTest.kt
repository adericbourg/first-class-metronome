package dev.dericbourg.firstclassmetronome.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BeatPatternTest {

    private val click = BeatOutput.Sound(ClickSound.CLICK)

    @Test
    fun default_isSingleClickBeat() {
        assertEquals(listOf(click), BeatPattern.DEFAULT)
    }

    @Test
    fun outputFor_withinBounds_returnsBeatAtIndex() {
        val pattern = listOf(BeatOutput.NoSound, click, BeatOutput.HapticOnly)

        assertEquals(BeatOutput.NoSound, BeatPattern.outputFor(pattern, 0))
        assertEquals(click, BeatPattern.outputFor(pattern, 1))
        assertEquals(BeatOutput.HapticOnly, BeatPattern.outputFor(pattern, 2))
    }

    @Test
    fun outputFor_whenIndexBeyondLength_wrapsAround() {
        val pattern = listOf(click, BeatOutput.NoSound)

        assertEquals(click, BeatPattern.outputFor(pattern, 2))
        assertEquals(BeatOutput.NoSound, BeatPattern.outputFor(pattern, 3))
        assertEquals(click, BeatPattern.outputFor(pattern, 100))
    }

    @Test
    fun outputFor_whenPatternEmpty_returnsNoSound() {
        assertEquals(BeatOutput.NoSound, BeatPattern.outputFor(emptyList(), 0))
    }

    @Test
    fun shouldVibrate_whenNoSound_isFalseRegardlessOfMaster() {
        assertFalse(BeatPattern.shouldVibrate(BeatOutput.NoSound, masterHapticEnabled = true))
        assertFalse(BeatPattern.shouldVibrate(BeatOutput.NoSound, masterHapticEnabled = false))
    }

    @Test
    fun shouldVibrate_whenHapticOnly_isTrueRegardlessOfMaster() {
        assertTrue(BeatPattern.shouldVibrate(BeatOutput.HapticOnly, masterHapticEnabled = true))
        assertTrue(BeatPattern.shouldVibrate(BeatOutput.HapticOnly, masterHapticEnabled = false))
    }

    @Test
    fun shouldVibrate_whenSound_followsMasterSetting() {
        assertTrue(BeatPattern.shouldVibrate(click, masterHapticEnabled = true))
        assertFalse(BeatPattern.shouldVibrate(click, masterHapticEnabled = false))
    }

    @Test
    fun encodeThenDecode_roundTripsEveryVariant() {
        val pattern = listOf(BeatOutput.NoSound, BeatOutput.HapticOnly, click, click)

        val roundTripped = BeatPattern.decode(BeatPattern.encode(pattern))

        assertEquals(pattern, roundTripped)
    }

    @Test
    fun decode_whenEmptyString_returnsDefault() {
        assertEquals(BeatPattern.DEFAULT, BeatPattern.decode(""))
    }

    @Test
    fun decode_whenMalformedToken_returnsDefault() {
        assertEquals(BeatPattern.DEFAULT, BeatPattern.decode("N,???,H"))
    }

    @Test
    fun decode_whenUnknownSound_returnsDefault() {
        assertEquals(BeatPattern.DEFAULT, BeatPattern.decode("S:NONEXISTENT"))
    }
}
