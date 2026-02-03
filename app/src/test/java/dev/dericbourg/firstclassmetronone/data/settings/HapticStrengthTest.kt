package dev.dericbourg.firstclassmetronone.data.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class HapticStrengthTest {

    @Test
    fun light_hasCorrectAmplitude() {
        assertEquals(64, HapticStrength.LIGHT.amplitude)
    }

    @Test
    fun light_hasCorrectDuration() {
        assertEquals(30L, HapticStrength.LIGHT.durationMs)
    }

    @Test
    fun medium_hasCorrectAmplitude() {
        assertEquals(128, HapticStrength.MEDIUM.amplitude)
    }

    @Test
    fun medium_hasCorrectDuration() {
        assertEquals(50L, HapticStrength.MEDIUM.durationMs)
    }

    @Test
    fun strong_hasCorrectAmplitude() {
        assertEquals(192, HapticStrength.STRONG.amplitude)
    }

    @Test
    fun strong_hasCorrectDuration() {
        assertEquals(80L, HapticStrength.STRONG.durationMs)
    }

    @Test
    fun entries_hasThreeValues() {
        assertEquals(3, HapticStrength.entries.size)
    }

    @Test
    fun amplitudes_areOrdered() {
        val amplitudes = HapticStrength.entries.map { it.amplitude }

        assertEquals(listOf(64, 128, 192), amplitudes)
    }

    @Test
    fun durations_areOrdered() {
        val durations = HapticStrength.entries.map { it.durationMs }

        assertEquals(listOf(30L, 50L, 80L), durations)
    }
}
