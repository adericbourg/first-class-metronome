# Feature 0008: Theme and Appearance

## Goal

Allow users to customize the app's visual appearance with support for light mode, dark mode, 
system default theme preference, and Material You dynamic color theming (Android 12+).

## Prerequisites

- Feature 0006 (Settings) must be implemented

## Design

### Settings Screen Location

Add a new **"Appearance"** section in the Settings screen, positioned after the "Metronome" section:

```
┌─────────────────────────────────────────┐
│ ← Settings                              │
├─────────────────────────────────────────┤
│                                         │
│  Metronome                              │
│  ─────────────────────────────────────  │
│  [existing metronome settings...]       │
│                                         │
│  Appearance                             │
│  ─────────────────────────────────────  │
│                                         │
│  Theme                                  │
│  Follow system settings             [▼] │
│                                         │
│  Dynamic colors                     [•] │
│  Use Material You wallpaper colors      │
│                                         │
│                                         │
│         [ Reset to defaults ]           │
│                                         │
└─────────────────────────────────────────┘
```

### Settings

| Setting         | Type    | Default             | Values                                    | Description                          |
|-----------------|---------|---------------------|-------------------------------------------|--------------------------------------|
| Theme           | Enum    | System default      | System default, Light, Dark               | Color scheme preference              |
| Dynamic colors  | Boolean | true (if supported) | on/off                                    | Use Material You dynamic theming     |

### Setting Controls

#### Theme Selection

Use an **Exposed Dropdown Menu** (Material 3 standard for Android):

```
┌─────────────────────────────────────────┐
│  Theme                                  │
│  Follow system settings             [▼] │
└─────────────────────────────────────────┘
```

When tapped, shows dropdown with three options:
- **Follow system settings** (default)
- **Light**
- **Dark**

**Behavior:**
- Theme changes **immediately** when selected (no apply button needed)
- Current selection is always displayed in the dropdown
- Follows Material 3 `ExposedDropdownMenuBox` design

#### Dynamic Colors Toggle

**Only visible on Android 12 (API 31) and above**:

```
┌─────────────────────────────────────────┐
│  Dynamic colors                     [•] │
│  Use Material You wallpaper colors      │
└─────────────────────────────────────────┘
```

- Standard Material 3 switch
- Subtitle explains the feature briefly
- Changes take effect immediately when toggled
- Enabled by default on supported devices

### Theme Application Behavior

| Theme Setting       | System Theme | Resulting UI | Dynamic Colors Effect                    |
|---------------------|--------------|--------------|------------------------------------------|
| System default      | Light        | Light        | Uses wallpaper colors if enabled         |
| System default      | Dark         | Dark         | Uses wallpaper colors if enabled         |
| Light               | Any          | Light        | Uses wallpaper colors if enabled         |
| Dark                | Any          | Dark         | Uses wallpaper colors if enabled         |

### Visual Examples

#### Light Mode
- Primary: Material 3 default or dynamic color from wallpaper
- Background: White/light gray surface colors
- Text: Dark gray/black

#### Dark Mode
- Primary: Material 3 default or dynamic color from wallpaper  
- Background: Dark surface colors
- Text: White/light gray

#### Dynamic Colors (Android 12+)
- Extracts accent colors from system wallpaper
- Applies to primary, secondary, and tertiary color roles
- Only affects color palette, not light/dark mode decision

## Data Model Changes

### ThemeMode Enum

```kotlin
enum class ThemeMode {
    SYSTEM_DEFAULT,
    LIGHT,
    DARK;
    
    companion object {
        fun fromOrdinal(ordinal: Int): ThemeMode = 
            entries.getOrNull(ordinal) ?: SYSTEM_DEFAULT
    }
}
```

### AppSettings

Add two new properties:

```kotlin
data class AppSettings(
    val bpmIncrement: Int = DEFAULT_BPM_INCREMENT,
    val hapticFeedbackEnabled: Boolean = DEFAULT_HAPTIC_FEEDBACK_ENABLED,
    val hapticStrength: HapticStrength = DEFAULT_HAPTIC_STRENGTH,
    val themeMode: ThemeMode = DEFAULT_THEME_MODE,                    // NEW
    val dynamicColorsEnabled: Boolean = DEFAULT_DYNAMIC_COLORS_ENABLED // NEW
) {
    companion object {
        const val DEFAULT_BPM_INCREMENT = 5
        const val DEFAULT_HAPTIC_FEEDBACK_ENABLED = false
        val DEFAULT_HAPTIC_STRENGTH = HapticStrength.MEDIUM
        val DEFAULT_THEME_MODE = ThemeMode.SYSTEM_DEFAULT              // NEW
        const val DEFAULT_DYNAMIC_COLORS_ENABLED = true                // NEW
        const val MIN_BPM_INCREMENT = 1
        const val MAX_BPM_INCREMENT = 20
    }
}
```

### SettingsRepository

Add two new methods:

```kotlin
interface SettingsRepository {
    val settings: Flow<AppSettings>
    
    suspend fun setBpmIncrement(value: Int)
    suspend fun setHapticFeedbackEnabled(enabled: Boolean)
    suspend fun setHapticStrength(strength: HapticStrength)
    suspend fun setThemeMode(mode: ThemeMode)                    // NEW
    suspend fun setDynamicColorsEnabled(enabled: Boolean)        // NEW
    suspend fun resetToDefaults()
}
```

### SettingsState

Add two new properties:

```kotlin
data class SettingsState(
    val bpmIncrement: Int = AppSettings.DEFAULT_BPM_INCREMENT,
    val hapticFeedbackEnabled: Boolean = AppSettings.DEFAULT_HAPTIC_FEEDBACK_ENABLED,
    val hapticStrength: HapticStrength = AppSettings.DEFAULT_HAPTIC_STRENGTH,
    val hasAmplitudeControl: Boolean = true,
    val themeMode: ThemeMode = AppSettings.DEFAULT_THEME_MODE,                         // NEW
    val dynamicColorsEnabled: Boolean = AppSettings.DEFAULT_DYNAMIC_COLORS_ENABLED,    // NEW
    val isDynamicColorsSupported: Boolean = true,                                      // NEW
    val showResetDialog: Boolean = false
)
```

## Integration Changes

### MainActivity / App Theme Setup

Apply the theme at the app level based on settings:

```kotlin
@Composable
fun FirstClassMetronomeApp(
    settings: AppSettings
) {
    val useDarkTheme = when (settings.themeMode) {
        ThemeMode.SYSTEM_DEFAULT -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    
    val dynamicColor = settings.dynamicColorsEnabled && 
                      Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    
    FirstClassMetronomeTheme(
        darkTheme = useDarkTheme,
        dynamicColor = dynamicColor
    ) {
        // App content
    }
}
```

### Theme Composable

Update your theme file to support dynamic colors:

```kotlin
@Composable
fun FirstClassMetronomeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) 
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

### SettingsScreen

Add the new Appearance section with dropdown and toggle:

```kotlin
@Composable
fun ThemeSettingDropdown(
    currentTheme: ThemeMode,
    onThemeChanged: (ThemeMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = currentTheme.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Theme") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ThemeMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(mode.displayName) },
                    onClick = {
                        onThemeChanged(mode)
                        expanded = false
                    }
                )
            }
        }
    }
}
```

## Data Persistence

### DataStore Keys

Add to `SettingsKeys`:

```kotlin
object SettingsKeys {
    val BPM_INCREMENT = intPreferencesKey("bpm_increment")
    val HAPTIC_FEEDBACK_ENABLED = booleanPreferencesKey("haptic_feedback_enabled")
    val HAPTIC_STRENGTH = stringPreferencesKey("haptic_strength")
    val THEME_MODE = intPreferencesKey("theme_mode")                        // NEW
    val DYNAMIC_COLORS_ENABLED = booleanPreferencesKey("dynamic_colors")    // NEW
}
```

### Storage Format

- **Theme mode**: Store as `Int` (ordinal value of enum)
- **Dynamic colors**: Store as `Boolean`

## Accessibility (WCAG 2.2 Level AA)

### Content Descriptions

| Element                | contentDescription                                    |
|------------------------|-------------------------------------------------------|
| Theme dropdown         | "Theme selection, current: [System/Light/Dark]"       |
| Dynamic colors toggle  | "Dynamic colors, [on/off]"                            |

### Touch Targets

- Theme dropdown: Full width, minimum 48dp height
- Dynamic colors toggle: Minimum 48dp × 48dp

### Screen Reader Announcements

| Event                     | Announcement                                      |
|---------------------------|---------------------------------------------------|
| Theme changed             | "Theme set to [System default/Light/Dark]"        |
| Dynamic colors toggled    | "Dynamic colors [enabled/disabled]"               |

### Contrast Requirements

- Ensure all color schemes (light, dark, dynamic) meet:
  - **Text contrast**: Minimum 4.5:1 for normal text
  - **UI components**: Minimum 3:1 for interactive elements
  - **Large text**: Minimum 3:1 for text ≥18pt or ≥14pt bold

## Edge Cases

| Scenario                                  | Behavior                                        |
|-------------------------------------------|-------------------------------------------------|
| Android < 12 (no dynamic colors support)  | Hide dynamic colors toggle entirely             |
| Invalid theme mode value in storage       | Default to `SYSTEM_DEFAULT`                     |
| Settings reset                            | Return to system default theme + dynamic colors |
| Theme changes while app is running        | UI updates immediately without restart          |
| System theme changes (in System Default)  | App updates to match system immediately         |

## Implementation Notes

### Step-by-step Implementation

1. **Add `ThemeMode` enum** to settings package
2. **Update `AppSettings`** data class with new properties
3. **Update `SettingsRepository`** interface and implementation
4. **Add DataStore keys** for theme_mode and dynamic_colors
5. **Update `SettingsViewModel`** to expose theme settings
6. **Update `SettingsState`** with new properties
7. **Modify `SettingsScreen`** to add Appearance section
8. **Check dynamic color support** using `Build.VERSION.SDK_INT >= Build.VERSION_CODES.S`
9. **Update `FirstClassMetronomeTheme`** to accept dynamic color parameter
10. **Update `MainActivity`** or root composable to apply theme from settings
11. **Add unit tests** for theme selection logic
12. **Add UI tests** for settings screen theme controls

### Dynamic Color Support Detection

```kotlin
val isDynamicColorsSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

// In ViewModel initialization
_state.update { 
    it.copy(isDynamicColorsSupported = isDynamicColorsSupported)
}
```

### Theme Mode Display Names

Add extension property for user-facing labels:

```kotlin
val ThemeMode.displayName: String
    get() = when (this) {
        ThemeMode.SYSTEM_DEFAULT -> "Follow system settings"
        ThemeMode.LIGHT -> "Light"
        ThemeMode.DARK -> "Dark"
    }
```

## Testing Considerations

### Unit Tests

- Test `ThemeMode.fromOrdinal()` with valid and invalid values
- Test theme application logic for all combinations
- Test settings persistence and retrieval
- Test reset to defaults includes theme settings

### UI Tests

- Verify dropdown displays all three options
- Verify selection changes theme immediately
- Verify dynamic colors toggle (on Android 12+ emulator)
- Verify accessibility labels

### Manual Testing

- Test on Android 11 (no dynamic colors option shown)
- Test on Android 12+ (dynamic colors option shown)
- Change system theme while app is in "System default" mode
- Verify colors update when toggling dynamic colors

## Out of Scope (Future Features)

- Custom color palette selection
- Per-screen theme overrides
- Theme scheduling (auto dark mode at night)
- High contrast mode
- Color blindness accessibility modes
- AMOLED black theme variant
