package dev.dericbourg.firstclassmetronome.audio

import org.junit.Assert.assertEquals
import org.junit.Test

class MetronomePlayerBpmTest {

    @Test
    fun clampBpm_ofValueWithinRange_returnsSameValue() {
        val result = MetronomePlayer.clampBpm(120)

        assertEquals(120, result)
    }

    @Test
    fun clampBpm_ofZero_returnsMinBpm() {
        val result = MetronomePlayer.clampBpm(0)

        assertEquals(MetronomePlayer.MIN_BPM, result)
    }

    @Test
    fun clampBpm_ofNegativeValue_returnsMinBpm() {
        val result = MetronomePlayer.clampBpm(-10)

        assertEquals(MetronomePlayer.MIN_BPM, result)
    }

    @Test
    fun clampBpm_ofValueBelowMin_returnsMinBpm() {
        val result = MetronomePlayer.clampBpm(MetronomePlayer.MIN_BPM - 1)

        assertEquals(MetronomePlayer.MIN_BPM, result)
    }

    @Test
    fun clampBpm_ofValueAboveMax_returnsMaxBpm() {
        val result = MetronomePlayer.clampBpm(MetronomePlayer.MAX_BPM + 1)

        assertEquals(MetronomePlayer.MAX_BPM, result)
    }
}
