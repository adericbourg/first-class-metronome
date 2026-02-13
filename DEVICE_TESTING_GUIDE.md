# Device Testing Guide - First-class Metronome

## Prerequisites

Before you begin, ensure you have:

- [ ] Physical Android device (Android 10+)
- [ ] USB debugging enabled on device
- [ ] USB cable to connect device to computer
- [ ] ADB installed and working
- [ ] Bundletool installed (for converting AAB to APK)

---

## Step 1: Install Bundletool

### macOS (using Homebrew):
```bash
brew install bundletool
```

### Manual Installation:
```bash
# Download latest bundletool
curl -L -o bundletool.jar https://github.com/google/bundletool/releases/latest/download/bundletool-all.jar

# Create alias (add to ~/.zshrc or ~/.bash_profile)
alias bundletool='java -jar /path/to/bundletool.jar'
```

**Verify installation:**
```bash
bundletool version
```

---

## Step 2: Convert AAB to APK

The release AAB must be converted to APK format for installation:

```bash
# Navigate to project root
cd /Users/alban.dericbourg/workspace/perso/FirstClassMetronome

# Generate universal APK from AAB
bundletool build-apks \
  --bundle=app/build/outputs/bundle/release/app-release.aab \
  --output=app/build/outputs/apks/app-release.apks \
  --mode=universal

# Extract the APK
unzip -p app/build/outputs/apks/app-release.apks universal.apk > app-release.apk
```

**Expected result:** `app-release.apk` file created in project root

---

## Step 3: Connect Device and Install

### 3.1 Enable USB Debugging on Android Device

**Settings → About phone → Tap "Build number" 7 times**

Then:

**Settings → Developer options → Enable "USB debugging"**

### 3.2 Connect Device

```bash
# Connect device via USB, then verify connection
adb devices
```

**Expected output:**
```
List of devices attached
ABC123XYZ    device
```

**If device shows "unauthorized":**
- Check device screen for authorization prompt
- Tap "Allow" and check "Always allow from this computer"
- Run `adb devices` again

### 3.3 Install APK

```bash
# Uninstall previous version if exists
adb uninstall dev.dericbourg.firstclassmetronome

# Install release APK
adb install -r app-release.apk
```

**Expected output:** `Success`

---

## Step 4: Basic Functionality Testing

Launch the app on your device and verify:

### 4.1 Core Metronome Features
- [ ] App launches without crash
- [ ] Main screen displays correctly with BPM value
- [ ] Tap **START** button
- [ ] Audio click sounds play at correct tempo
- [ ] Metronome keeps accurate tempo (use external metronome to verify)
- [ ] Visual indicator flashes in sync with audio
- [ ] Tap **STOP** button - audio stops immediately

### 4.2 BPM Selection
- [ ] Navigate to BPM selection screen
- [ ] Tap different BPM values (try: 60, 120, 180, 240)
- [ ] Selected BPM shows as active/highlighted
- [ ] Return to main screen - new BPM is displayed
- [ ] Start metronome - plays at new BPM

### 4.3 Settings
- [ ] Navigate to Settings screen
- [ ] Toggle **Haptic feedback** ON
- [ ] Start metronome - device vibrates on each beat
- [ ] Toggle **Haptic feedback** OFF
- [ ] Start metronome - no vibration
- [ ] Change **Click sound** setting (if available)
- [ ] Verify new sound plays

### 4.4 Worklog / Practice Sessions
- [ ] Navigate to Worklog screen
- [ ] Start metronome and let it run for 30+ seconds
- [ ] Stop metronome
- [ ] Check Worklog - new practice session should appear
- [ ] Verify session shows:
  - Start time (e.g., "14:30")
  - Duration (e.g., "0m 35s")

### 4.5 Statistics
- [ ] Still in Worklog, scroll to Statistics section
- [ ] Verify **Practice** stats show:
  - 7d, 30d, total columns
  - Correct values (should match your practice session)
- [ ] Verify **Average** stats display
- [ ] Numbers should be properly formatted (hours/minutes)

---

## Step 5: Accessibility Testing with TalkBack

### 5.1 Enable TalkBack

**Method 1 - Settings:**
1. **Settings → Accessibility → TalkBack**
2. Toggle **Use TalkBack** ON
3. Confirm activation

**Method 2 - Volume Key Shortcut:**
1. Hold **Volume Up + Volume Down** for 3 seconds
2. TalkBack should activate

**First-time setup:**
- TalkBack tutorial may appear
- Complete tutorial or skip to proceed

### 5.2 TalkBack Navigation Basics

- **Swipe right:** Move to next element
- **Swipe left:** Move to previous element
- **Double-tap:** Activate/click focused element
- **Swipe down then right:** Read from current position
- **Two-finger swipe up/down:** Scroll

### 5.3 Test Main Screen Accessibility

Navigate using TalkBack and verify announcements:

- [ ] **START button** announces: "Start" or "Start button"
- [ ] **BPM value** announces: "120 BPM" or similar
- [ ] **Navigation icons** announce their purpose
- [ ] All interactive elements are focusable
- [ ] Double-tap on START activates metronome
- [ ] While playing, can still navigate and stop metronome

### 5.4 Test SessionList Accessibility (CRITICAL)

This is a key change from Step 4 of the implementation plan.

1. Navigate to **Worklog** screen
2. Swipe to a practice session card
3. **Expected announcement:**
   ```
   "Practice session at [TIME], duration [DURATION]"
   ```
   Example: "Practice session at 14:30, duration 15 minutes 23 seconds"

- [ ] SessionCard announces complete information
- [ ] Announcement includes both time AND duration
- [ ] Format matches: "Practice session at [TIME], duration [DURATION]"

**If announcement is wrong or incomplete, this is a CRITICAL ISSUE.**

### 5.5 Test StatsCard Accessibility (CRITICAL)

This is a key change from Step 5 of the implementation plan.

Still in Worklog, scroll to Statistics section:

1. **Section headings:**
   - [ ] "Practice" announces as a heading
   - [ ] "Average" announces as a heading
   - [ ] TalkBack heading navigation (swipe up then right) works

2. **Period labels (7d, 30d, total):**
   - [ ] "7d" announces as "7 days"
   - [ ] "30d" announces as "30 days"
   - [ ] "total" announces as "All time"

3. **Stat values:**
   - [ ] Each value announces with context
   - [ ] Example: "Practice for 7 days: 2 hours 30 minutes"
   - [ ] Format: "[CATEGORY] for [PERIOD]: [VALUE]"

**If stats don't announce with full context, this is a CRITICAL ISSUE.**

### 5.6 Test BPM Selection Accessibility

1. Navigate to BPM selection screen
2. Swipe through BPM options
3. Each BPM should announce:
   - [ ] Unselected: "120 BPM" or "120 BPM, button"
   - [ ] Selected: "120 BPM, selected" or similar indication
   - [ ] Double-tap activates BPM selection

### 5.7 Test Settings Accessibility

1. Navigate to Settings screen
2. Verify each setting announces clearly:
   - [ ] Toggle states (ON/OFF) are announced
   - [ ] Double-tap toggles the setting
   - [ ] Setting labels are descriptive

### 5.8 Disable TalkBack

After testing:
- **Volume Up + Volume Down** for 3 seconds, or
- **Settings → Accessibility → TalkBack → Toggle OFF**

---

## Step 6: Edge Cases & Stress Testing

### 6.1 Device Rotation
- [ ] Enable auto-rotate (if app supports landscape)
- [ ] Start metronome
- [ ] Rotate device
- [ ] Metronome continues playing without interruption
- [ ] UI adjusts correctly (or stays portrait-only per manifest)

**Note:** App is configured for portrait-only, so rotation should be locked.

### 6.2 Background Behavior
- [ ] Start metronome
- [ ] Press **Home button** - app goes to background
- [ ] **Expected:** Metronome stops (per lifecycle handler)
- [ ] Return to app - can restart metronome

### 6.3 App Switching
- [ ] Start metronome
- [ ] Open Recent Apps (square button or swipe up)
- [ ] Switch to another app
- [ ] **Expected:** Metronome stops
- [ ] Switch back to metronome app

### 6.4 Device Sleep
- [ ] Start metronome
- [ ] Press **Power button** - device screen turns off
- [ ] **Expected:** Metronome stops
- [ ] Wake device - app resumes correctly

### 6.5 Extreme BPM Values
- [ ] Set BPM to **20** (very slow)
  - Clicks should be very far apart (~3 seconds)
  - No timing drift over 30 seconds
- [ ] Set BPM to **300** (very fast)
  - Clicks should be very rapid (~5 per second)
  - No audio crackling or dropouts
  - UI remains responsive

### 6.6 Long Practice Sessions
- [ ] Start metronome
- [ ] Let it run for 5+ minutes
- [ ] Verify:
  - No audio quality degradation
  - No memory leaks (app doesn't slow down)
  - Tempo stays accurate throughout
- [ ] Stop and check Worklog - session logged correctly

---

## Step 7: Performance Verification

### 7.1 Audio Quality
- [ ] No crackling or popping sounds
- [ ] Consistent volume across beats
- [ ] Clean, sharp click sound
- [ ] No latency when starting/stopping

### 7.2 UI Responsiveness
- [ ] No lag when pressing buttons
- [ ] Smooth navigation between screens
- [ ] No frame drops or stuttering
- [ ] BPM changes apply immediately

### 7.3 Statistics Loading
- [ ] Worklog screen loads quickly (<1 second)
- [ ] Statistics calculate and display quickly
- [ ] No freezing when scrolling session list

### 7.4 Battery Usage (Optional)
- [ ] Note battery level before testing
- [ ] Use metronome for 30 minutes
- [ ] Check battery drain - should be minimal

---

## Step 8: Check LogCat for Errors

While testing, monitor device logs for any errors:

```bash
# In a separate terminal window
adb logcat -s FirstClassMetronome:* AndroidRuntime:E *:F
```

**Watch for:**
- Crashes (FATAL EXCEPTION)
- ANR (Application Not Responding)
- Memory warnings
- Audio errors

**Expected:** No errors during normal operation

---

## Testing Checklist Summary

### ✅ PASS Criteria
- [ ] All basic functionality works
- [ ] TalkBack announces SessionList correctly: "Practice session at [TIME], duration [DURATION]"
- [ ] TalkBack announces StatsCard with full context: "[CATEGORY] for [PERIOD]: [VALUE]"
- [ ] All headings navigable with TalkBack
- [ ] No crashes or ANRs
- [ ] Audio quality is good
- [ ] UI is responsive
- [ ] Extreme BPM values work correctly
- [ ] Background/sleep behavior is correct

### ❌ CRITICAL ISSUES (Must Fix Before Release)
- App crashes on launch
- Metronome doesn't play audio
- SessionList accessibility missing or incorrect
- StatsCard accessibility missing or incorrect
- ANR during normal use
- Audio crackling/dropouts

### ⚠️ MINOR ISSUES (Fix If Possible, Not Blocking)
- Slight UI glitches
- Non-critical TalkBack announcements
- Performance warnings in LogCat

---

## Troubleshooting

### Issue: APK won't install
```bash
# Check for signing issues
adb install -r -t app-release.apk

# Or uninstall first
adb uninstall dev.dericbourg.firstclassmetronome
adb install app-release.apk
```

### Issue: "Device unauthorized"
- Check device screen for USB debugging prompt
- Revoke authorizations: Settings → Developer options → Revoke USB debugging authorizations
- Reconnect and re-authorize

### Issue: TalkBack is confusing
- Complete TalkBack tutorial first
- Practice navigation in Settings app before testing
- Remember: double-tap to activate, not single tap

### Issue: Can't hear audio
- Check device volume (media volume, not ringtone)
- Ensure device isn't in silent/vibrate mode
- Try plugging in headphones

### Issue: Audio sounds distorted with R8
This could indicate ProGuard rules are too aggressive:
```bash
# Check logcat for ClassNotFoundException or resource loading errors
adb logcat -s FirstClassMetronome:*

# If issues found, may need to add to proguard-rules.pro:
# -keep class **.R$raw { *; }
```

---

## After Testing: Report Results

Once testing is complete, document:

1. **Device Info:**
   - Model: _______________________
   - Android version: _____________
   - Screen size: _________________

2. **Test Results:**
   - PASS / FAIL for each section
   - Any CRITICAL issues found
   - Any MINOR issues found

3. **Accessibility Results:**
   - SessionList announcement: CORRECT / INCORRECT
   - StatsCard announcements: CORRECT / INCORRECT
   - Heading navigation: WORKS / DOESN'T WORK

4. **Performance:**
   - Audio quality: GOOD / POOR
   - UI responsiveness: GOOD / POOR
   - Battery drain: ACCEPTABLE / EXCESSIVE

5. **LogCat Errors:**
   - List any errors/warnings found

---

## Next Steps After Successful Testing

If all tests pass:

1. ✅ Mark Step 8 complete in plan
2. ✅ Proceed to commit changes
3. ✅ Tag version for release
4. ✅ Push to trigger automated Play Store upload

If critical issues found:

1. ❌ Document issues
2. ❌ Fix issues in code
3. ❌ Re-run `./gradlew bundleRelease`
4. ❌ Re-test on device
5. ❌ Repeat until all tests pass

---

**Good luck with testing!** 🚀
