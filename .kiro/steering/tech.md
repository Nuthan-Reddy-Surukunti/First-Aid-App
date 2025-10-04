# Technology Stack

## Core Technologies
- **Language**: Kotlin
- **UI Framework**: XML Layouts with Material Design 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Build System**: Gradle with Kotlin DSL
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 14)
- **Java Version**: 11

## Key Dependencies
- **Core Android**: androidx.core:core-ktx, androidx.appcompat
- **UI Components**: Material Design 3, ConstraintLayout
- **Testing**: JUnit, Espresso

## Planned Architecture Components
- **Database**: Room (for local data storage)
- **Navigation**: Navigation Component
- **Lifecycle**: ViewModel & LiveData
- **Image Loading**: Glide or Coil
- **JSON Parsing**: Gson
- **Async Operations**: Kotlin Coroutines

## Common Commands

### Build & Run
```bash
# Clean and build
gradlew clean build

# Install debug APK
gradlew installDebug

# Run tests
gradlew test
gradlew connectedAndroidTest

# Generate signed APK
gradlew assembleRelease
```

### Development
```bash
# Check for dependency updates
gradlew dependencyUpdates

# Lint check
gradlew lint

# Generate documentation
gradlew dokkaHtml
```

## Performance Requirements
- **Launch Time**: < 2 seconds
- **Memory Usage**: < 200MB RAM
- **App Size**: < 50MB
- **Search Response**: < 1 second
- **Voice Response**: < 500ms
- **CPR Metronome Accuracy**: ±2 BPM

## Offline-First Design
- All guides stored locally in JSON files
- Images bundled in app resources
- Room database for user data (favorites, contacts)
- No network dependency for core features
- Optional features may use network (maps, AI enhancement)