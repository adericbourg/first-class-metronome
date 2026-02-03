# Feature 0007: Haptic Feedback Enhancements

## Goal

Enhance the existing haptic feedback feature with configurable vibration strength
and provide visual feedback when haptic mode is active during playback.

## Prerequisites

- Feature 0006 (Settings) must be implemented

## Design

### Vibration Strength Setting

Add a strength slider below the haptic feedback toggle in Settings:

```
┌─────────────────────────────────────────┐
│  Haptic feedback                    [•] │
│  Vibrate on each beat                   │
│                                         │
│  Vibration strength                     │
│  ○───────●─────────○                    │
│  Light   Medium    Strong               │
└─────────────────────────────────────────┘
```

- **Only visible when haptic feedback is enabled**
- Three discrete levels for simplicity:
    - Light (amplitude ~64)
    - Medium (amplitude ~128) - DEFAULT
    - Strong (amplitude ~192)
- Setting is remembered independently of the toggle

### Visual Indicator on Main Screen

When haptic feedback is enabled, show a vibration indicator next to the play button:

```
┌─────────────────────────────────────────┐
│                                         │
│                        60 BPM           │
│                    [📳] [▶]             │
│                                         │
└─────────────────────────────────────────┘
```

- Icon: Material `Icons.Default.Vibration` or `Icons.Outlined.Vibration`
- Visibility: Always visible when haptic feedback is enabled (regardless of play state)
- Color: Secondary/accent color when playing, muted when paused
- Accessibility: "Haptic feedback enabled"

## Settings

| Setting         | Type | Default | Range/Values          | Description                  |
|-----------------|------|---------|-----------------------|------------------------------|
| Haptic strength | Enum | Medium  | Light, Medium, Strong | Vibration intensity per beat |

### Strength Level Mapping

| Level  | Amplitude Value | Duration | User Perception |
|--------|-----------------|----------|-----------------|
| Light  | 64              | 30ms     | Subtle tap      |
| Medium | 128             | 50ms     | Clear pulse     |
| Strong | 192             | 80ms     | Firm vibration  |

## Data Model Changes

### AppSettings

```kotlin
data class AppSettings(
    val bpmIncrement: Int = DEFAULT_BPM_INCREMENT,
    val hapticFeedbackEnabled: Boolean = DEFAULT_HAPTIC_FEEDBACK_ENABLED,
    val hapticStrength: HapticStrength = DEFAULT_HAPTIC_STRENGTH
) {
    companion object {
        const val DEFAULT_BPM_INCREMENT = 5
        const val DEFAULT_HAPTIC_FEEDBACK_ENABLED = false
        val DEFAULT_HAPTIC_STRENGTH = HapticStrength.MEDIUM
    }
}

enum class HapticStrength(val amplitude: Int, val durationMs: Long) {
    LIGHT(64, 30L),
    MEDIUM(128, 50L),
    STRONG(192, 80L)
}
```

### SettingsRepository

Add method:

```kotlin
suspend fun setHapticStrength(strength: HapticStrength)
```

### SettingsState

Add property:

```kotlin
val hapticStrength: HapticStrength = HapticStrength.MEDIUM
val hasAmplitudeControl: Boolean = true
```

### BeatSelectionState

Add property:

```kotlin
val isHapticEnabled: Boolean = false
```

## Integration Changes

### MetronomePlayer

Replace fixed values with settings:

```kotlin
// Before
vibrator.vibrate(
    VibrationEffect.createOneShot(
        HAPTIC_DURATION_MS,
        VibrationEffect.DEFAULT_AMPLITUDE
    )
)

// After
val strength = currentHapticStrength.get()
vibrator.vibrate(VibrationEffect.createOneShot(strength.durationMs, strength.amplitude))
```

### ButtonBar

Add haptic indicator icon parameter:

```kotlin
@Composable
fun ButtonBar(
    // existing params...
    isHapticEnabled: Boolean,  // NEW
    // ...
)
```

## Accessibility (WCAG 2.2 Level AA)

### Content Descriptions

| Element               | contentDescription                                   |
|-----------------------|------------------------------------------------------|
| Strength slider       | "Vibration strength, current: [Light/Medium/Strong]" |
| Haptic indicator icon | "Haptic feedback enabled"                            |

### Touch Targets

- Slider track: Full width, minimum 48dp height for easy dragging
- Slider thumb: Minimum 48dp touch target

### Screen Reader Announcements

| Event                  | Announcement                                      |
|------------------------|---------------------------------------------------|
| Strength level changed | "Vibration strength set to [Light/Medium/Strong]" |

## Edge Cases

| Scenario                              | Behavior                               |
|---------------------------------------|----------------------------------------|
| Haptic disabled → strength hidden     | Slider collapses with animation        |
| Haptic enabled → strength shown       | Slider expands with animation          |
| Device doesn't support amplitude ctrl | Show toggle only, hide strength slider |
| Invalid strength value in storage     | Default to MEDIUM                      |

## Implementation Notes

1. Check `Vibrator.hasAmplitudeControl()` to determine if strength slider should show
2. Store strength as string enum name in DataStore
3. Animate slider visibility when toggle changes
4. Pass haptic state to BeatSelectionViewModel via SettingsRepository flow
