# FirstClassMetronome - Development Guidelines

## Project Overview

Android metronome application built with modern Android development practices.

## Specifications

- Stored in `/specs` folder.
- One folder per specification.
- Pattern `<ordering number with 4 digits>-<title>`.
- Ask me for every feature decision you have to make.
- Always consider adding settings for a new feature.

## Technology Stack

- **Language**: Kotlin
- **Min SDK**: 29 (Android 10)
- **Target SDK**: 34 (Android 14)
- **Build System**: Gradle with Kotlin DSL
- **Architecture**: MVVM with Clean Architecture principles
- **UI**: Jetpack Compose
- **DI**: Hilt

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/metronome/
│   │   │   ├── data/           # Repositories, data sources
│   │   │   ├── domain/         # Use cases, business logic
│   │   │   ├── presentation/   # ViewModels, Compose UI
│   │   │   └── di/             # Hilt modules
│   │   └── res/
│   ├── test/                   # Unit tests
│   └── androidTest/            # Instrumented tests
```

## Coding Standards

### Kotlin Style

- Use `val` over `var` when possible
- Prefer immutable collections (`listOf`, `setOf`, `mapOf`)
- Use data classes for DTOs and state objects
- Prefix `Optional`-like nullable variables with `maybe` (e.g., `maybeCurrentBeat`)
- Use sealed classes for UI state and events

### Compose Guidelines

- Keep composables small and focused
- Extract reusable components to separate files
- Use `remember` and `derivedStateOf` appropriately
- Follow unidirectional data flow (state down, events up)

### Audio Handling

- Use `AudioTrack` for precise timing control
- Run audio generation on a dedicated thread (not main/UI thread)
- Use `Handler` with `Looper` or coroutines for scheduling
- Consider `MediaPlayer` for simple sound playback, `AudioTrack` for low-latency

### Database

- (important) Consider all changes as a migration: there must be a way to migrate all data
  incrementally from any version to any more recent version

## Testing

### Unit Tests

- Use JUnit 5 with `kotlin.test`
- Mock dependencies with MockK
- Test naming: `methodName_whenCondition_expectedResult`
- Structure: Given / When / Then (no comments, use blank lines)

### UI Tests

- Use Compose testing APIs
- Test accessibility with `assertIsDisplayed()` and semantic matchers
- Use `createComposeRule()` for isolated component tests

### Example Test

```kotlin
@Test
fun setBpm_whenValueInRange_updatesBpm() {
    val viewModel = MetronomeViewModel()

    viewModel.setBpm(120)

    assertEquals(120, viewModel.state.value.bpm)
}
```

## Accessibility (WCAG 2.2 Level AA)

- Provide content descriptions for all interactive elements
- Support TalkBack navigation
- Minimum touch target size: 48dp
- Sufficient color contrast (4.5:1 for text, 3:1 for UI components)
- Support dynamic text sizing
- Avoid conveying information by color alone

### Compose Accessibility

```kotlin
Button(
    onClick = { /* action */ },
    modifier = Modifier.semantics {
        contentDescription = "Start metronome"
    }
)
```

## Audio-Specific Considerations

### Timing Accuracy

- Calculate beat intervals: `intervalMs = 60_000 / bpm`
- Account for audio latency in scheduling
- Use `System.nanoTime()` for precise measurements
- Consider audio buffer size impact on latency

### Sound Generation

- Pre-generate click sounds to avoid runtime allocation
- Use appropriate sample rate (44100 Hz standard)

## Dependencies

```kotlin
// build.gradle.kts (app)
dependencies {
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.9")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
```

## Git Workflow

- Branch naming: `feature/`, `fix/`, `refactor/`
- Commit messages: imperative mood, concise
- Run tests before committing

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Check lint
./gradlew lint
```
