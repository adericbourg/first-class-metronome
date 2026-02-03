# Feature 0003: Tap Tempo

## Goal

Allow the user to set the tempo by tapping a rhythm, useful when matching a song's tempo or setting
a tempo by feel.

## Design

### Opening the Overlay

- Tapping the tap-tempo button (👆) in the button bar opens a full-screen overlay
- If the metronome is playing, it **pauses** when the overlay opens

### Overlay Layout

```
┌─────────────────────────────────────────┐
│                                         │
│            [Shaded finger icon]         │
│              (background)               │
│                                         │
│  ┌───────────────────────────────────┐  │
│  │                                   │  │
│  │         TAP ZONE                  │  │
│  │                                   │  │
│  │         "Tap here"                │  │
│  │                                   │  │
│  └───────────────────────────────────┘  │
│                                         │
│              120 BPM                    │
│          (calculated tempo)             │
│                                         │
│      [ Cancel ]        [ Apply ]        │
│                                         │
└─────────────────────────────────────────┘
```

### Tap Zone

- Large dedicated button/zone for tapping (minimum 200dp × 150dp)
- Displays "Tap here" as hint text
- Shows **ripple effect** on each tap (standard Material ripple)
- Touch target meets accessibility requirements (48dp minimum, but zone is much larger)

### BPM Calculation

#### Algorithm

1. Record timestamp of each tap
2. Calculate intervals between consecutive taps
3. Average the **last 5 intervals** to compute BPM
4. Formula: `BPM = 60,000 / averageIntervalMs`

#### Display Rules

| Condition                  | Display                                                                              |
|----------------------------|--------------------------------------------------------------------------------------|
| Less than 2 taps           | "Tap here" (no BPM shown)                                                            |
| 2+ taps                    | Show BPM but Apply enabled                                                           |
| After 5 seconds of no taps | Keep current value; reset all taps and show "Tap here" when the user resumes tapping |

#### BPM Range

- **No clamping**: Accept any calculated BPM (even outside grid's 30-150 range)
- Display as integer (rounded)
- When applied, if BPM matches a grid value, select it; otherwise no grid selection

### Buttons

| Button | Behavior                               | Enabled               |
|--------|----------------------------------------|-----------------------|
| Cancel | Close overlay, discard tapped BPM      | Always                |
| Apply  | Set BPM to tapped value, close overlay | When 3+ taps recorded |

### Dismissing the Overlay

The overlay can be dismissed by:

- Tapping **Cancel** button
- Tapping **outside** the overlay content (same as Cancel)
- Pressing the **back button** (same as Cancel)

### Metronome State

| Action         | Metronome Behavior                        |
|----------------|-------------------------------------------|
| Open overlay   | Pause if playing                          |
| Cancel/Dismiss | Resume if was playing before              |
| Apply          | Resume if was playing before (at new BPM) |

## Accessibility (WCAG 2.2 Level AA)

### Content Descriptions

| Element       | contentDescription                             |
|---------------|------------------------------------------------|
| Tap zone      | "Tap tempo zone. Tap repeatedly to set tempo"  |
| BPM display   | "[N] beats per minute"                         |
| Cancel button | "Cancel tap tempo"                             |
| Apply button  | "Apply [N] BPM" (or "Apply tempo" if disabled) |

### Screen Reader Announcements

| Event          | Announcement                                                |
|----------------|-------------------------------------------------------------|
| Overlay opens  | "Tap tempo. Tap the zone repeatedly to set your tempo"      |
| BPM calculated | "[N] BPM" (after each tap that changes the displayed value) |
| Apply pressed  | "Tempo set to [N] BPM"                                      |

### Touch Targets

- Tap zone: Minimum 200dp × 150dp
- Cancel/Apply buttons: Minimum 48dp × 48dp

## State Model

```kotlin
data class TapTempoState(
    val isVisible: Boolean = false,
    val tapTimestamps: List<Long> = emptyList(),
    val calculatedBpm: Int? = null,
    val wasPlayingBeforeOpen: Boolean = false
)
```

## Out of Scope (Future Features)

- Tap tempo without overlay (tap directly on main screen)
- Metronome sync (start metronome in sync with taps)
- Visual tap counter
- Haptic feedback on tap

---

## Implementation Notes

1. Create `TapTempoOverlay` composable as a modal dialog/overlay
2. Create `TapTempoViewModel` or add state to `BeatSelectionViewModel`
3. Use `System.currentTimeMillis()` for tap timestamps
4. Implement 5-second timeout using `LaunchedEffect` with delay
5. Handle back press with `BackHandler` composable
6. Use `ModalBottomSheet` or custom `Dialog` for overlay

### BPM Calculation Example

```kotlin
fun calculateBpm(timestamps: List<Long>): Int? {
    if (timestamps.size < 2) return null

    val intervals = timestamps.zipWithNext { a, b -> b - a }
    val recentIntervals = intervals.takeLast(5)
    val averageMs = recentIntervals.average()

    return (60_000 / averageMs).roundToInt()
}
```
