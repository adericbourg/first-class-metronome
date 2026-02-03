# Feature 0002: Beat Play

## Goal

Play a continuous metronome beat at the exact BPM selected in the beat pad.

## Design

### Play/Stop Button

The play button in the button bar toggles metronome playback.

| State | Icon | Material Icon Name | Content Description |
|-------|------|-------------------|---------------------|
| Stopped | ▶ | `Icons.Default.PlayArrow` | "Start metronome" |
| Playing | ◼ | `Icons.Default.Stop` | "Stop metronome" |

**Behavior:**
- Tapping while stopped starts playback immediately
- Tapping while playing stops playback immediately
- Button appearance updates instantly on state change

### Audio

#### Sound Source

- Use a bundled WAV file located at `res/raw/click.wav`
- Source: Open-source metronome click sound (to be included)

#### Timing Precision

- Beat interval calculation: `intervalMs = 60_000 / bpm`
- Timing must be precise with minimal drift over extended playback
- Use `AudioTrack` with a dedicated audio thread for low-latency playback
- Do NOT use `Handler.postDelayed()` or coroutine delays for timing (insufficient precision)

#### Implementation Approach

```
┌─────────────────────────────────────────────────────┐
│                   Audio Thread                       │
│  ┌─────────────────────────────────────────────┐    │
│  │  while (isPlaying) {                        │    │
│  │      playClick()                            │    │
│  │      preciseWait(intervalMs)                │    │
│  │  }                                          │    │
│  └─────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────┘
```

- Pre-load the WAV file into memory on app start
- Use `System.nanoTime()` for precise interval measurement
- Account for playback duration in interval calculation

### BPM Changes During Playback

- User **can** change BPM while metronome is playing
- New BPM takes effect **after the current beat completes**
- No audible glitch or timing discontinuity

### Lifecycle Behavior

| Event | Behavior |
|-------|----------|
| App goes to background | Stop playback |
| App returns to foreground | Remain stopped (user must restart) |
| Screen off | Stop playback |
| Activity destroyed | Stop playback, release audio resources |

**Implementation:** Stop playback in `onPause()` or when lifecycle state drops below `RESUMED`.

### Error Handling

| Error | Behavior |
|-------|----------|
| Audio file missing | Log error, disable play button, show toast |
| Audio playback failure | Stop playback, log error |
| Audio focus lost | Stop playback |

## Accessibility (WCAG 2.2 Level AA)

### Content Descriptions

Already defined in the button state table above.

### Screen Reader Announcements

| Action | Announcement |
|--------|--------------|
| Start playback | "Metronome started at [N] BPM" |
| Stop playback | "Metronome stopped" |
| BPM change while playing | "[N] BPM" (announce new tempo) |

## State Model

```kotlin
data class MetronomePlaybackState(
    val isPlaying: Boolean = false,
    val currentBpm: Int = 60
)
```

**Note:** This extends the existing `BeatSelectionState` or can be combined with it.

## Out of Scope (Future Features)

- Beat accent (first beat of measure)
- Time signature / measure grouping
- Visual beat indicator
- Background playback (foreground service)
- Haptic feedback
- Multiple click sounds / customization
- Tap tempo functionality

---

## Implementation Notes

1. Create `MetronomePlayer` class to handle audio playback
2. Use `AudioTrack` in streaming mode for precise timing
3. Run audio loop on dedicated `HandlerThread` or use `Executors.newSingleThreadExecutor()`
4. Integrate with `BeatSelectionViewModel` or create separate `MetronomeViewModel`
5. Add lifecycle observer to stop playback appropriately
6. Source and bundle the click WAV file

### Recommended Audio Parameters

```kotlin
val sampleRate = 44100
val channelConfig = AudioFormat.CHANNEL_OUT_MONO
val audioFormat = AudioFormat.ENCODING_PCM_16BIT
```

### Click Sound Requirements

- Duration: 10-50ms (short, percussive)
- Format: WAV, PCM 16-bit, 44.1kHz, mono
- License: Open source (CC0, MIT, or similar)
