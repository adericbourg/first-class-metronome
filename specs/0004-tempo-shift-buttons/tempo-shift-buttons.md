# Feature 0004: Tempo Shift Buttons

## Goal

Allow users to fine-tune the tempo by shifting the BPM value up or down by 5, enabling precise tempo
adjustments beyond the fixed grid values.

## Design

### Button Location

The `-5` and `+5` buttons are located in the button bar, on the left side:

```
────────────────────────────
                   [ 62 BPM ]
[ -5 ][ +5 ]       [ 👆 ][ ▶ ]
```

### Behavior

| Button | Action         | Result                            |
|--------|----------------|-----------------------------------|
| -5     | Decrease tempo | `currentBpm - 5`, minimum 20 BPM  |
| +5     | Increase tempo | `currentBpm + 5`, maximum 300 BPM |

### BPM Range

| Limit   | Value   | Rationale                                 |
|---------|---------|-------------------------------------------|
| Minimum | 20 BPM  | Practical lower bound for metronome use   |
| Maximum | 300 BPM | Common upper limit for digital metronomes |

### Button States

| Condition      | -5 Button                    | +5 Button                     |
|----------------|------------------------------|-------------------------------|
| BPM = 20       | Disabled                     | Enabled                       |
| BPM = 300      | Enabled                      | Disabled                      |
| 20 < BPM < 300 | Enabled                      | Enabled                       |
| BPM ≤ 24       | Disabled (would go below 20) | Enabled                       |
| BPM ≥ 296      | Enabled                      | Disabled (would go above 300) |

### Grid Selection Sync

When the shifted BPM matches a value in the beat selection grid:

- **Select** that grid button (highlight it)

When the shifted BPM does NOT match a grid value:

- **Deselect** all grid buttons (none highlighted)
- Display an off-grid indicator in the tempo display

### Playback Behavior

| Metronome State | On Shift                                   |
|-----------------|--------------------------------------------|
| Playing         | Update tempo immediately (no interruption) |
| Stopped         | Update tempo, remain stopped               |

## Off-Grid Indicator

When the current BPM is not in the grid's predefined values (e.g., 62 BPM from tap tempo, or 25 BPM
from shifting):

### Visual Treatment

- Display the BPM value with a subtle visual indicator
- Use a different text style (e.g., italic) or add a small icon
- Maintain the same font size and position

### Example States

| BPM | Grid Match | Display                   |
|-----|------------|---------------------------|
| 60  | Yes        | `60 BPM` (normal)         |
| 62  | No         | `62 BPM` (with indicator) |
| 25  | No         | `25 BPM` (with indicator) |
| 120 | Yes        | `120 BPM` (normal)        |

## Accessibility (WCAG 2.2 Level AA)

### Content Descriptions

| Element   | State    | contentDescription                     |
|-----------|----------|----------------------------------------|
| -5 button | Enabled  | "Decrease tempo by 5"                  |
| -5 button | Disabled | "Decrease tempo by 5, minimum reached" |
| +5 button | Enabled  | "Increase tempo by 5"                  |
| +5 button | Disabled | "Increase tempo by 5, maximum reached" |

### Screen Reader Announcements

| Event            | Announcement                     |
|------------------|----------------------------------|
| Tempo decreased  | "[N] BPM"                        |
| Tempo increased  | "[N] BPM"                        |
| At minimum (20)  | "Minimum tempo reached, 20 BPM"  |
| At maximum (300) | "Maximum tempo reached, 300 BPM" |

### Touch Targets

- Minimum touch target size: **48dp × 48dp** (already implemented in button bar)

### Disabled State

- Disabled buttons must have sufficient contrast (3:1 minimum)
- Use `enabled = false` parameter for proper semantics

## State Model

### Constants to Add

```kotlin
companion object {
    const val MIN_BPM = 20
    const val MAX_BPM = 300
    const val BPM_SHIFT_AMOUNT = 5
}
```

### Computed Properties

```kotlin
val isOnGrid: Boolean
get() = selectedBpm in availableBpmValues

val canDecreaseBpm: Boolean
get() = selectedBpm - BPM_SHIFT_AMOUNT >= MIN_BPM

val canIncreaseBpm: Boolean
get() = selectedBpm + BPM_SHIFT_AMOUNT <= MAX_BPM
```

## ViewModel Methods

```kotlin
fun decreaseBpm() {
    val newBpm = (state.value.selectedBpm - BPM_SHIFT_AMOUNT)
        .coerceAtLeast(MIN_BPM)
    updateBpm(newBpm)
}

fun increaseBpm() {
    val newBpm = (state.value.selectedBpm + BPM_SHIFT_AMOUNT)
        .coerceAtMost(MAX_BPM)
    updateBpm(newBpm)
}

private fun updateBpm(bpm: Int) {
    _state.update { it.copy(selectedBpm = bpm) }
    if (_state.value.isPlaying) {
        metronomePlayer.updateBpm(bpm)
    }
}
```

## Edge Cases

| Scenario                                    | Behavior                               |
|---------------------------------------------|----------------------------------------|
| Rapid button presses                        | Each press shifts by 5, no debouncing  |
| BPM at 23, press -5                         | BPM becomes 20 (clamped), -5 disabled  |
| BPM at 298, press +5                        | BPM becomes 300 (clamped), +5 disabled |
| Off-grid BPM (e.g., 62), press -5           | BPM becomes 57 (off-grid)              |
| Off-grid BPM (e.g., 62), select grid button | BPM snaps to grid value                |

## Out of Scope (Future Features)

- Long-press for continuous adjustment
- Customizable shift amount (e.g., ±1, ±10)
- Haptic feedback on press

---

## Implementation Notes

1. Add `MIN_BPM`, `MAX_BPM`, and `BPM_SHIFT_AMOUNT` constants to `BeatSelectionState`
2. Add `isOnGrid`, `canDecreaseBpm`, `canIncreaseBpm` computed properties
3. Add `decreaseBpm()` and `increaseBpm()` methods to `BeatSelectionViewModel`
4. Update `ButtonBar` to accept callbacks and pass enabled states
5. Add off-grid visual indicator to tempo display
6. Update content descriptions for disabled states
7. Add unit tests for boundary conditions and grid sync
