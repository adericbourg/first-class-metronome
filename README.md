# First-class Metronome

A precise, accessible metronome app for musicians.

## Features

- Accurate tempo control (BPM)
- Accessibility-first design (WCAG 2.2 Level AA)
- A worklog to help you track your practice sessions

## Requirements

- Android 10 (API 29) or higher
- No special permissions required
- Java 21

## Tech Stack

- Kotlin
- Jetpack Compose
- MVVM + Clean Architecture
- Hilt for dependency injection

## Building

```bash
# Clone the repository
git clone https://github.com/adericbourg/FirstClassMetronome.git
cd FirstClassMetronome

# Build debug APK
./gradlew assembleDebug

# Run tests
./gradlew test
```

## Project Structure

```
app/src/main/java/com/example/metronome/
├── data/           # Repositories, data sources
├── domain/         # Use cases, business logic
├── presentation/   # ViewModels, Compose UI
└── di/             # Hilt modules
```

## License

GPLv3
