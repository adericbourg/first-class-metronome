# Feature 0001: Beat Selection

## Goal

Display beat (tempo) selection on the main screen, allowing users to select a BPM value.

## Design

### Layout Structure

The UI consists of three vertically stacked sections:
1. **Title** - App name
2. **Beat Selection Grid** - Matrix of BPM buttons
3. **Current tempo display** - The current selected tempo 
4. **Button Bar** - Action buttons

### Visual Example

```
First Class Metronome

   [ 30 ][ 35 ][ 40 ][ 45 ]
   [ 50 ][ 55 ][ 60 ][ 65 ]
   [ 70 ][ 75 ][ 80 ][ 85 ]
   [ 90 ][ 95 ][100 ][110 ]
   [120 ][130 ][140 ][150 ]

────────────────────────────
                  [    CT   ]
[ -5 ][ +5 ]      [ 👆 ][ ▶ ]
```

## Beat Selection Grid

### BPM Values

The grid displays the following **hardcoded** BPM values in a 5×4 matrix (row-major order):

| Row | Values |
|-----|--------|
| 1 | 30, 35, 40, 45 |
| 2 | 50, 55, 60, 65 |
| 3 | 70, 75, 80, 85 |
| 4 | 90, 95, 100, 110 |
| 5 | 120, 130, 140, 150 |

### Layout Rules

- Buttons arranged in a uniform grid (4 columns × 5 rows)
- All buttons have identical dimensions
- Text is horizontally and vertically centered within each button
- Uniform gap between all buttons (horizontal and vertical)

### Selection Behavior

- **Single selection**: Only one BPM can be selected at a time
- **Default selection**: 60 BPM on app launch
- **Visual indicator**: Selected button uses:
  - Primary color background (vs. secondary/surface for unselected)
  - Emphasized border (thicker and/or primary color)
  - This dual indicator ensures accessibility compliance (not relying on color alone)
- Tapping a button selects that BPM value

## Button Bar

### Layout

Two button groups separated by flexible space:

| Group | Position | Buttons |
|-------|----------|---------|
| Adjustment | Left | "-5", "+5" |
| Actions | Right | Tap tempo (👆), Play/Pause (▶) |

Above the right button group, recall the currently selected tempo.

### Behavior (Phase 1 - This Feature)

All button bar buttons are **non-functional** in this phase:
- `-5` / `+5`: No action (future: adjust selected BPM)
- Tap tempo: No action (future: detect BPM from taps)
- Play/Pause: No action (future: start/stop metronome)

## Accessibility (WCAG 2.2 Level AA)

### Touch Targets

- Minimum touch target size: **48dp × 48dp** for all interactive elements

### Content Descriptions

| Element | contentDescription |
|---------|-------------------|
| BPM button (unselected) | "Select [N] BPM" |
| BPM button (selected) | "[N] BPM, selected" |
| -5 button | "Decrease tempo by 5" |
| +5 button | "Increase tempo by 5" |
| Tap tempo button | "Tap tempo" |
| Play button | "Start metronome" |

### Screen Reader Behavior

- When a BPM is selected, announce: "[N] BPM selected"
- Grid should be navigable with TalkBack gestures

### Visual Requirements

- Text contrast ratio: minimum 4.5:1 against background
- UI component contrast: minimum 3:1 for button borders/backgrounds
- Selected state must be distinguishable by more than color alone (e.g., border weight or icon)

## Responsive Behavior

### Portrait (Primary)

- Grid fills available width with equal button sizing
- Horizontal padding: 16dp on each side

### Landscape

- Same layout, buttons may be wider
- Ensure button bar remains visible (no scrolling required for core UI)

### Small Screens

- If grid cannot fit at minimum touch target size, allow vertical scrolling for the grid section only
- Button bar remains fixed at bottom

## Icons

Use Material Icons from `androidx.compose.material.icons`:

| Function | Icon | Material Name |
|----------|------|---------------|
| Tap tempo | 👆 | `Icons.Default.TouchApp` |
| Play | ▶ | `Icons.Default.PlayArrow` |

## State Model

```kotlin
data class BeatSelectionState(
    val selectedBpm: Int = 60,
    val availableBpmValues: List<Int> = listOf(
        30, 35, 40, 45,
        50, 55, 60, 65,
        70, 75, 80, 85,
        90, 95, 100, 110,
        120, 130, 140, 150
    )
)
```

## Out of Scope (Future Features)

- Metronome audio playback
- Tap tempo detection
- BPM adjustment with +5/-5 buttons
- Custom BPM input
- Persistence of selected BPM

---

## Implementation Notes

When approved, implementation will involve:
1. Create `BeatSelectionState` data class
2. Create `BeatSelectionViewModel` with state management
3. Build `BeatSelectionScreen` composable
4. Build `BpmGrid` composable for the button matrix
5. Build `ButtonBar` composable for action buttons
6. Add unit tests for ViewModel
7. Add UI tests for accessibility compliance
