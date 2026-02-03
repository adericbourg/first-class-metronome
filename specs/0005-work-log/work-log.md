# Feature 0005: Work Log

## Goal

Keep track of practice over time to help users monitor their progress and build consistent practice
habits.

## Design

### Navigation

- Add a menu icon (☰) at the top-right of the main screen
- Tapping the menu opens a dropdown with a single item: "Work log" (more items will be added later)
- The Work log screen has a back arrow (←) at the top-left to return to the main screen

### Session Tracking

| Concept          | Definition                                                                   |
|------------------|------------------------------------------------------------------------------|
| Playing time     | Time the metronome has been playing                                          |
| Short pause      | Pause < 10 min → **added** to practice duration (user was "mentally active") |
| Long pause       | Pause ≥ 10 min but < 1 hour → **not added**, but session continues           |
| Session boundary | Gap ≥ 1 hour (from stop to next start) → creates a **new session**           |

**Practice duration** = Playing time + Short pause time

### Statistics

Display the following stats in a card at the bottom of the screen:

| Metric                    | 7 days | 30 days | All time |
|---------------------------|--------|---------|----------|
| Total practice duration   | ✓      | ✓       | ✓        |
| Number of days practicing | ✓      | ✓       | ✓        |
| Average duration/session  | ✓      | ✓       | ✓        |
| Average duration/day      | ✓      | ✓       | ✓        |

**Calculation rules:**

- **Days practicing**: Count days with at least one session
- **Average per day**: Only count days with practice (exclude days without practice)

### Work Log Screen Layout

```
┌─────────────────────────────────────────┐
│ ← Work log                              │
├─────────────────────────────────────────┤
│                                         │
│   2026-01-02 05:23   00:09:00           │
│              04:00   00:12:00           │
│   2026-01-01 02:00   00:21:00           │
│                                         │
│            (scrollable list)            │
│                                         │
├─────────────────────────────────────────┤
│  ┌───────────────────────────────────┐  │
│  │ Practice                          │  │
│  │     7d       30d      total       │  │
│  │  00:42:00  01:25:00  05:30:00     │  │
│  │  (2 days)  (5 days)  (30 days)    │  │
│  │                                   │  │
│  │ Average per session               │  │
│  │     7d       30d      total       │  │
│  │  00:14:00  00:11:25  00:05:30     │  │
│  │                                   │  │
│  │ Average per day                   │  │
│  │     7d       30d      total       │  │
│  │  00:21:00  00:17:00  00:11:00     │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

**Layout behavior:**

- Stats card is **fixed at the bottom** (does not scroll)
- Session list scrolls independently above the stats card
- Most recent sessions appear at the top

### Session Entry Display

Each session entry shows:

- **Date**: Only shown for the first session of each day (subsequent sessions on same day show blank
  date)
- **Start time**: HH:mm format (24-hour)
- **Duration**: HH:mm:ss format

### Empty State

When no practice data exists:

```
┌─────────────────────────────────────────┐
│ ← Work log                              │
├─────────────────────────────────────────┤
│                                         │
│         🎵                              │
│                                         │
│    No practice sessions yet             │
│                                         │
│    Start the metronome to begin         │
│    tracking your practice time.         │
│                                         │
└─────────────────────────────────────────┘
```

### Clear Work Log

Users can delete all practice data. This action requires **double confirmation** to prevent
accidental data loss.

#### Access

- Add a "Clear all data" button at the bottom of the Work log screen (below the stats card)
- Button uses **destructive styling** (red text or outline)
- Button is **hidden** when there is no data (empty state)

#### Confirmation Flow

**Step 1: First confirmation dialog**

```
┌─────────────────────────────────────────┐
│                                         │
│   Clear all practice data?              │
│                                         │
│   This will permanently delete all      │
│   your practice history. This action    │
│   cannot be undone.                     │
│                                         │
│        [ Cancel ]    [ Clear ]          │
│                                         │
└─────────────────────────────────────────┘
```

**Step 2: Second confirmation dialog** (only shown if user taps "Clear")

```
┌─────────────────────────────────────────┐
│                                         │
│   Are you sure?                         │
│                                         │
│   You are about to delete:              │
│   • 45 practice sessions                │
│   • 12h 34m of tracked practice         │
│                                         │
│   This cannot be undone.                │
│                                         │
│     [ Cancel ]    [ Delete forever ]    │
│                                         │
└─────────────────────────────────────────┘
```

#### Button States

| Dialog   | Cancel button                  | Confirm button                              |
|----------|--------------------------------|---------------------------------------------|
| First    | Default style, closes dialog   | Destructive style (red), shows second dialog|
| Second   | Default style, closes dialog   | Destructive style (red), deletes all data   |

#### After Deletion

- Close both dialogs
- Show empty state on Work log screen
- Show brief toast/snackbar: "Practice data cleared"

## Examples

### Pauses

Assume the following usage:

1. 2026-01-01, 02:00, metronome starts
2. 2026-01-01, 02:04, metronome stops
3. 2026-01-01, 02:10, metronome starts
4. 2026-01-01, 02:12, metronome stops
5. 2026-01-01, 02:23, metronome starts
6. 2026-01-01, 02:32, metronome stops

**Breakdown:**

| Interval      | Type        | Duration | Counts? |
|---------------|-------------|----------|---------|
| 02:00 → 02:04 | Playing     | 4 min    | ✓       |
| 02:04 → 02:10 | Short pause | 6 min    | ✓       |
| 02:10 → 02:12 | Playing     | 2 min    | ✓       |
| 02:12 → 02:23 | Long pause  | 11 min   | ✗       |
| 02:23 → 02:32 | Playing     | 9 min    | ✓       |

**Result**: One session starting at 02:00 with **21 minutes** of practice (4 + 6 + 2 + 9).

### Sessions

Assume the following usage:

1. 2026-01-02, 04:00, metronome starts
2. 2026-01-02, 04:04, metronome stops
3. 2026-01-02, 04:10, metronome starts
4. 2026-01-02, 04:12, metronome stops
5. 2026-01-02, 05:23, metronome starts
6. 2026-01-02, 05:32, metronome stops

**Breakdown:**

| Interval      | Type        | Duration | Session |
|---------------|-------------|----------|---------|
| 04:00 → 04:04 | Playing     | 4 min    | 1       |
| 04:04 → 04:10 | Short pause | 6 min    | 1       |
| 04:10 → 04:12 | Playing     | 2 min    | 1       |
| 04:12 → 05:23 | Session gap | 71 min   | —       |
| 05:23 → 05:32 | Playing     | 9 min    | 2       |

**Result**: Two sessions (gap ≥ 1 hour creates new session):

- **Session 1** (04:00): 12 minutes (4 + 6 + 2)
- **Session 2** (05:23): 9 minutes

## Data Persistence

### Storage

Use **Room database** with the following schema:

```kotlin
@Entity(tableName = "practice_events")
data class PracticeEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,      // Unix timestamp in milliseconds
    val eventType: EventType  // START or STOP
)

enum class EventType {
    START, STOP
}

@Entity(tableName = "practice_sessions")
data class PracticeSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTimestamp: Long,     // Unix timestamp in milliseconds
    val durationMs: Long,         // Duration in milliseconds
    val isCompacted: Boolean      // True if created from compaction (events deleted)
)
```

**Dual storage strategy:**

- **Recent data (≤ 30 days)**: Sessions are computed from events on-the-fly
- **Old data (> 30 days)**: Sessions are stored in `practice_sessions` table after compaction

### Event Compaction

To limit database growth, events older than 30 days are compacted into sessions.

#### Compaction Rules

| Condition                        | Action                                              |
|----------------------------------|-----------------------------------------------------|
| Event older than 30 days         | Eligible for compaction                             |
| Session spans the 30-day boundary| Keep events until entire session is > 30 days old   |
| Compaction frequency             | Run on app launch (background, non-blocking)        |

#### Compaction Algorithm

```kotlin
private const val COMPACTION_THRESHOLD_MS = 30L * 24 * 60 * 60 * 1000  // 30 days

fun compactOldEvents() {
    val cutoffTime = System.currentTimeMillis() - COMPACTION_THRESHOLD_MS

    // 1. Find all events older than 30 days
    val oldEvents = eventDao.getEventsBefore(cutoffTime)
    if (oldEvents.isEmpty()) return

    // 2. Compute sessions from old events
    val sessionsToCompact = computeSessions(oldEvents)
        .filter { session ->
            // Only compact sessions that ended before cutoff
            // (no risk of splitting an ongoing session)
            val sessionEnd = session.startTimestamp + session.durationMs
            sessionEnd < cutoffTime
        }

    // 3. Insert compacted sessions
    sessionsToCompact.forEach { session ->
        sessionDao.insert(
            PracticeSessionEntity(
                startTimestamp = session.startTimestamp,
                durationMs = session.durationMs,
                isCompacted = true
            )
        )
    }

    // 4. Delete compacted events
    val lastCompactedSessionEnd = sessionsToCompact.maxOfOrNull {
        it.startTimestamp + it.durationMs
    } ?: return

    eventDao.deleteEventsBefore(lastCompactedSessionEnd)
}
```

#### Querying Sessions

When loading sessions, merge both sources:

```kotlin
fun getAllSessions(): List<PracticeSession> {
    val compactedSessions = sessionDao.getAll()
    val recentEvents = eventDao.getAll()
    val recentSessions = computeSessions(recentEvents)

    return (compactedSessions.map { it.toPracticeSession() } + recentSessions)
        .sortedByDescending { it.startTime }
}
```

### App Lifecycle Handling

| Scenario                           | Behavior                                    |
|------------------------------------|---------------------------------------------|
| App killed while metronome playing | Insert STOP event on next app launch        |
| App backgrounded while playing     | Continue tracking (metronome keeps playing) |
| Phone rebooted while playing       | Insert STOP event on next app launch        |

**Detecting abnormal termination**: On app launch, check if last event is START with no
corresponding STOP. If so, insert a STOP event with the START timestamp + reasonable estimate (or
just the START timestamp).

## Accessibility (WCAG 2.2 Level AA)

### Content Descriptions

| Element              | contentDescription                                          |
|----------------------|-------------------------------------------------------------|
| Menu button          | "Open menu"                                                 |
| Work log menu item   | "Work log"                                                  |
| Back button          | "Go back"                                                   |
| Session entry        | "Practice session on [date] at [time], duration [duration]" |
| Stats card           | "Practice statistics"                                       |
| Clear all data button| "Clear all practice data"                                   |
| First dialog Cancel  | "Cancel"                                                    |
| First dialog Clear   | "Clear all data"                                            |
| Second dialog Cancel | "Cancel"                                                    |
| Second dialog Delete | "Delete forever"                                            |

### Screen Reader Announcements

| Event                    | Announcement                                          |
|--------------------------|-------------------------------------------------------|
| Work log screen opens    | "Work log, [N] practice sessions"                     |
| Empty state              | "No practice sessions yet"                            |
| First confirm dialog     | "Alert: Clear all practice data? This cannot be undone" |
| Second confirm dialog    | "Alert: Are you sure? [N] sessions will be deleted"   |
| Data cleared             | "Practice data cleared"                               |

### Touch Targets

- Menu button: Minimum **48dp × 48dp**
- Back button: Minimum **48dp × 48dp**
- Session entries: Minimum **48dp** height

### Color Contrast

- All text must have **4.5:1** contrast ratio against background
- Stats card background must have **3:1** contrast against screen background

## State Model

```kotlin
data class WorkLogState(
    val sessions: List<PracticeSession> = emptyList(),
    val stats: PracticeStats = PracticeStats(),
    val clearDataDialog: ClearDataDialogState = ClearDataDialogState.Hidden
)

data class PracticeSession(
    val id: Long,
    val startTime: Instant,
    val durationSeconds: Long
)

data class PracticeStats(
    val last7Days: PeriodStats = PeriodStats(),
    val last30Days: PeriodStats = PeriodStats(),
    val allTime: PeriodStats = PeriodStats()
)

data class PeriodStats(
    val totalDurationSeconds: Long = 0,
    val daysWithPractice: Int = 0,
    val sessionCount: Int = 0
) {
    val averagePerSession: Long
        get() = if (sessionCount > 0) totalDurationSeconds / sessionCount else 0

    val averagePerDay: Long
        get() = if (daysWithPractice > 0) totalDurationSeconds / daysWithPractice else 0
}

sealed class ClearDataDialogState {
    object Hidden : ClearDataDialogState()
    object FirstConfirmation : ClearDataDialogState()
    data class SecondConfirmation(
        val sessionCount: Int,
        val totalDurationSeconds: Long
    ) : ClearDataDialogState()
}
```

## Edge Cases

| Scenario                                 | Behavior                                                |
|------------------------------------------|---------------------------------------------------------|
| Metronome starts and stops in < 1 second | Record the session (even if 0 seconds)                  |
| Multiple rapid start/stops               | Each creates an event; short pauses merge sessions      |
| Timezone change during session           | Use UTC internally; display in local timezone           |
| DST transition during session            | Use UTC internally; no impact on duration               |
| Clock set backwards during session       | Use monotonic time for duration, wall clock for display |
| Very long session (> 24 hours)           | Display as HH:mm:ss (e.g., "25:30:00")                  |
| Session spans 30-day compaction boundary | Keep events until entire session is older than 30 days  |
| Compaction runs during active session    | Never compact events from ongoing session               |
| Clear data while metronome is playing    | Stop metronome first, then clear (or block clear)       |
| Clear data interrupted (app killed)      | Transaction ensures atomicity; no partial delete        |
| User cancels at second confirmation      | Return to Work log screen (not first dialog)            |

## Out of Scope (Future Features)

- Editing or deleting sessions manually
- Exporting practice data
- Practice goals/reminders
- Charts/graphs of practice over time
- Syncing across devices
- Detailed breakdown by tempo/time signature

---

## Implementation Notes

1. Create `PracticeEvent` entity and `PracticeEventDao` for Room
2. Create `PracticeSessionEntity` entity and `PracticeSessionDao` for compacted sessions
3. Create `PracticeRepository` to handle event storage, session computation, and compaction
4. Create `WorkLogViewModel` with `WorkLogState`
5. Create `WorkLogScreen` composable with session list, stats card, and clear button
6. Create `ClearDataDialog` composable with two-step confirmation
7. Add menu dropdown to `BeatSelectionScreen`
8. Use `Navigation` component for screen transitions
9. Register `ProcessLifecycleOwner` observer to detect app termination
10. Use `java.time.Instant` for timestamps (requires desugaring for API < 26)
11. Run compaction in `WorkScope` coroutine on app launch (non-blocking)
12. Use Room `@Transaction` for clear operation to ensure atomicity

### Session Computation Algorithm

```kotlin
private const val SHORT_PAUSE_THRESHOLD_MS = 10 * 60 * 1000L  // 10 minutes
private const val SESSION_GAP_THRESHOLD_MS = 60 * 60 * 1000L  // 1 hour

fun computeSessions(events: List<PracticeEvent>): List<PracticeSession> {
    val sortedEvents = events.sortedBy { it.timestamp }
    val sessions = mutableListOf<PracticeSession>()

    var sessionStart: Long? = null
    var lastStartTime: Long? = null
    var lastStopTime: Long? = null
    var totalDuration: Long = 0

    for (event in sortedEvents) {
        when (event.eventType) {
            START -> {
                val gapFromLastStop = lastStopTime?.let { event.timestamp - it }

                if (gapFromLastStop != null && gapFromLastStop >= SESSION_GAP_THRESHOLD_MS) {
                    // Gap ≥ 1 hour: finalize previous session, start new one
                    if (sessionStart != null) {
                        sessions.add(PracticeSession(sessionStart, totalDuration))
                    }
                    sessionStart = event.timestamp
                    totalDuration = 0
                } else if (gapFromLastStop != null && gapFromLastStop < SHORT_PAUSE_THRESHOLD_MS) {
                    // Short pause (< 10 min): add pause time to duration
                    totalDuration += gapFromLastStop
                }
                // Long pause (10 min to 1 hour): don't add to duration, but continue session

                if (sessionStart == null) {
                    sessionStart = event.timestamp
                }
                lastStartTime = event.timestamp
            }
            STOP -> {
                // Add playing time
                lastStartTime?.let { start ->
                    totalDuration += event.timestamp - start
                }
                lastStopTime = event.timestamp
                lastStartTime = null
            }
        }
    }

    // Finalize last session if exists
    if (sessionStart != null && totalDuration > 0) {
        sessions.add(PracticeSession(sessionStart, totalDuration))
    }
    return sessions
}
```

### Clear All Data Algorithm

```kotlin
@Transaction
suspend fun clearAllData() {
    // Delete in a single transaction for atomicity
    eventDao.deleteAll()
    sessionDao.deleteAll()
}

// ViewModel handling
fun onClearDataClicked() {
    _state.update { it.copy(clearDataDialog = ClearDataDialogState.FirstConfirmation) }
}

fun onFirstConfirmationConfirmed() {
    val stats = _state.value.stats.allTime
    _state.update {
        it.copy(
            clearDataDialog = ClearDataDialogState.SecondConfirmation(
                sessionCount = stats.sessionCount,
                totalDurationSeconds = stats.totalDurationSeconds
            )
        )
    }
}

fun onSecondConfirmationConfirmed() {
    viewModelScope.launch {
        repository.clearAllData()
        _state.update {
            WorkLogState() // Reset to empty state
        }
    }
}

fun onClearDialogDismissed() {
    _state.update { it.copy(clearDataDialog = ClearDataDialogState.Hidden) }
}
```