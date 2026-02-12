package dev.dericbourg.firstclassmetronome.data.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeModeTest {

    @Test
    fun fromOrdinal_whenSystemDefault_returnsSystemDefault() {
        val result = ThemeMode.fromOrdinal(0)

        assertEquals(ThemeMode.SYSTEM_DEFAULT, result)
    }

    @Test
    fun fromOrdinal_whenLight_returnsLight() {
        val result = ThemeMode.fromOrdinal(1)

        assertEquals(ThemeMode.LIGHT, result)
    }

    @Test
    fun fromOrdinal_whenDark_returnsDark() {
        val result = ThemeMode.fromOrdinal(2)

        assertEquals(ThemeMode.DARK, result)
    }

    @Test
    fun fromOrdinal_whenInvalidNegative_returnsSystemDefault() {
        val result = ThemeMode.fromOrdinal(-1)

        assertEquals(ThemeMode.SYSTEM_DEFAULT, result)
    }

    @Test
    fun fromOrdinal_whenInvalidLarge_returnsSystemDefault() {
        val result = ThemeMode.fromOrdinal(999)

        assertEquals(ThemeMode.SYSTEM_DEFAULT, result)
    }

    @Test
    fun displayName_ofSystemDefault_returnsCorrectLabel() {
        assertEquals("Follow system settings", ThemeMode.SYSTEM_DEFAULT.displayName)
    }

    @Test
    fun displayName_ofLight_returnsCorrectLabel() {
        assertEquals("Light", ThemeMode.LIGHT.displayName)
    }

    @Test
    fun displayName_ofDark_returnsCorrectLabel() {
        assertEquals("Dark", ThemeMode.DARK.displayName)
    }

    @Test
    fun entries_hasThreeValues() {
        assertEquals(3, ThemeMode.entries.size)
    }

    @Test
    fun entries_areInCorrectOrder() {
        val entries = ThemeMode.entries

        assertEquals(ThemeMode.SYSTEM_DEFAULT, entries[0])
        assertEquals(ThemeMode.LIGHT, entries[1])
        assertEquals(ThemeMode.DARK, entries[2])
    }
}
