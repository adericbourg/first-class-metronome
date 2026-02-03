# Feature 0006: Settings

## Goal

Allow users to customize the application behavior to match their preferences.

## Design

### Navigation

- Add a "Settings" entry in the existing top-right menu (below "Work log")
- The Settings screen has a back arrow (←) at the top-left to return to the main screen
- Settings are **auto-saved** when changed (no explicit Save button needed)

### Settings Screen Layout

```
┌─────────────────────────────────────────┐
│ ← Settings                              │
├─────────────────────────────────────────┤
│                                         │
│  Metronome                              │
│  ─────────────────────────────────────  │
│                                         │
│  BPM increment                      [5] │
│  Amount added/subtracted by the ±       │
│  buttons                                │
│                                         │
│  Haptic feedback                    [•] │
│  Vibrate on each beat                   │
│                                         │
│                                         │
│         [ Reset to defaults ]           │
│                                         │
└─────────────────────────────────────────┘
```

### Settings

| Setting          | Type    | Default | Range/Values | Description                                  |
|------------------|---------|---------|--------------|----------------------------------------------|
| BPM increment    | Integer | 5       | 1 – 20       | Amount for the ± tempo shift buttons         |
| Haptic feedback  | Boolean | false   | on/off       | Vibrate on each metronome beat               |

### Setting Controls

| Setting          | Control Type        | Behavior                                           |
|------------------|--------------------|----------------------------------------------------|
| BPM increment    | Number picker/stepper | Tap to open picker, or use +/- to adjust        |
| Haptic feedback  | Toggle switch       | Immediately enables/disables vibration            |
| Reset to defaults| Text button         | Opens confirmation dialog before resetting        |

### BPM Increment Input

The BPM increment can be adjusted using a stepper control:

```
┌─────────────────────────────────────────┐
│  BPM increment            [ - ] 5 [ + ] │
└─────────────────────────────────────────┘
```

- Minimum value: **1**
- Maximum value: **20**
- The `-` button is disabled when value is 1
- The `+` button is disabled when value is 20

### Haptic Feedback

- **Only shown if the device supports haptic feedback**
- If the device doesn't support vibration, this setting is hidden entirely
- When enabled, the device vibrates briefly on each metronome beat
- Vibration pattern: single short pulse (~50ms)

### Reset to Defaults

Confirmation dialog when tapping "Reset to defaults":

```
┌─────────────────────────────────────────┐
│                                         │
│   Reset to defaults?                    │
│                                         │
│   This will restore all settings to     │
│   their original values.                │
│                                         │
│        [ Cancel ]    [ Reset ]          │
│                                         │
└─────────────────────────────────────────┘
```

After reset:
- All settings return to default values
- Show brief toast/snackbar: "Settings reset"

## Data Persistence

### Storage

Use **DataStore Preferences** for settings storage:

```kotlin
object SettingsKeys {
    val BPM_INCREMENT = intPreferencesKey("bpm_increment")
    val HAPTIC_FEEDBACK_ENABLED = booleanPreferencesKey("haptic_feedback_enabled")
}

data class AppSettings(
    val bpmIncrement: Int = DEFAULT_BPM_INCREMENT,
    val hapticFeedbackEnabled: Boolean = DEFAULT_HAPTIC_FEEDBACK
) {
    companion object {
        const val DEFAULT_BPM_INCREMENT = 5
        const val DEFAULT_HAPTIC_FEEDBACK = false
    }
}
```

### Settings Repository

```kotlin
interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun setBpmIncrement(value: Int)
    suspend fun setHapticFeedbackEnabled(enabled: Boolean)
    suspend fun resetToDefaults()
}
```

## Integration with Existing Features

### BPM Shift Buttons

The `BeatSelectionViewModel` should read the BPM increment from settings:

```kotlin
// Before (hardcoded)
val newBpm = selectedBpm - BeatSelectionState.BPM_SHIFT_AMOUNT

// After (from settings)
val newBpm = selectedBpm - settings.bpmIncrement
```

### Metronome Player

When haptic feedback is enabled, the `MetronomePlayer` should trigger vibration on each beat:

```kotlin
// In the playback loop
if (settings.hapticFeedbackEnabled) {
    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
}
```

## State Model

```kotlin
data class SettingsState(
    val bpmIncrement: Int = AppSettings.DEFAULT_BPM_INCREMENT,
    val hapticFeedbackEnabled: Boolean = AppSettings.DEFAULT_HAPTIC_FEEDBACK,
    val isHapticSupported: Boolean = true,
    val showResetDialog: Boolean = false
)
```

## Accessibility (WCAG 2.2 Level AA)

### Content Descriptions

| Element                | contentDescription                              |
|------------------------|------------------------------------------------|
| Back button            | "Go back"                                       |
| Settings menu item     | "Settings"                                      |
| BPM increment field    | "BPM increment, current value [N]"             |
| BPM decrement button   | "Decrease BPM increment"                        |
| BPM increment button   | "Increase BPM increment"                        |
| Haptic feedback toggle | "Haptic feedback, [on/off]"                    |
| Reset button           | "Reset to defaults"                             |

### Touch Targets

- All interactive elements: Minimum **48dp × 48dp**
- Toggle switch: Standard Material 3 size (meets requirement)
- Stepper buttons: Minimum **48dp × 48dp**

### Screen Reader Announcements

| Event                    | Announcement                                    |
|--------------------------|------------------------------------------------|
| Settings screen opens    | "Settings"                                      |
| BPM increment changed    | "BPM increment set to [N]"                      |
| Haptic feedback toggled  | "Haptic feedback [enabled/disabled]"           |
| Reset dialog opens       | "Alert: Reset to defaults?"                     |
| Settings reset           | "Settings reset to defaults"                    |

## Edge Cases

| Scenario                              | Behavior                                         |
|---------------------------------------|--------------------------------------------------|
| Device doesn't support vibration      | Hide haptic feedback setting entirely            |
| App killed while on Settings screen   | Settings already auto-saved, no data loss        |
| Invalid BPM increment in storage      | Clamp to valid range (1–20) on load              |
| BPM increment causes out-of-range BPM | Clamp resulting BPM to MIN_BPM/MAX_BPM           |
| Settings migration (future)           | DataStore handles missing keys with defaults     |

## Implementation Notes

1. Create `SettingsRepository` interface and `SettingsRepositoryImpl` with DataStore
2. Create `SettingsViewModel` with `SettingsState`
3. Create `SettingsScreen` composable
4. Add "Settings" to menu dropdown in `BeatSelectionScreen`
5. Add `AppScreen.Settings` to navigation
6. Update `BeatSelectionViewModel` to inject `SettingsRepository` and use dynamic BPM increment
7. Update `MetronomePlayer` to support haptic feedback (inject `Vibrator`)
8. Check haptic support using `Vibrator.hasVibrator()` or `VibrationEffect` capabilities
9. Add Hilt module for `Vibrator` system service injection

### Dependency Injection

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {
    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository = SettingsRepositoryImpl(context)

    @Provides
    fun provideVibrator(
        @ApplicationContext context: Context
    ): Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
}
```

### Required Permissions

Add to `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.VIBRATE" />
```

## Out of Scope (Future Features)

- Sound customization (click sound selection)
- Theme settings (dark/light mode)
- Accent beat settings
- Time signature configuration
- Backup/restore settings
- Cloud sync of settings
