# Project Structure

## Root Level Organization
```
FirstAidApp/
├── app/                    # Main application module
├── gradle/                 # Gradle wrapper and version catalog
├── .gradle/               # Gradle cache (auto-generated)
├── .idea/                 # Android Studio project files
├── .kiro/                 # Kiro AI assistant configuration
├── build.gradle.kts       # Root build configuration
├── settings.gradle.kts    # Project settings
├── gradle.properties      # Gradle properties
├── local.properties       # Local SDK paths
└── PRD                    # Product Requirements Document
```

## App Module Structure (Planned MVVM Architecture)
```
app/src/main/
├── java/com/example/firstaidapp/
│   ├── data/
│   │   ├── database/
│   │   │   ├── AppDatabase.kt
│   │   │   ├── GuideDao.kt
│   │   │   └── entities/
│   │   ├── repository/
│   │   └── models/
│   ├── ui/
│   │   ├── home/           # Home screen with categories
│   │   ├── guide/          # Step-by-step guide details
│   │   ├── search/         # Search functionality
│   │   ├── navigator/      # Symptom navigator
│   │   ├── favorites/      # Bookmarked guides
│   │   └── emergency/      # Emergency contacts
│   ├── utils/
│   │   ├── VoiceAssistant.kt
│   │   ├── CPRMetronome.kt
│   │   └── Constants.kt
│   └── MainActivity.kt
├── res/
│   ├── layout/            # XML layout files
│   ├── drawable/          # Vector drawables and images
│   ├── values/
│   │   ├── strings.xml    # All text strings
│   │   ├── colors.xml     # Color definitions
│   │   └── themes.xml     # Material Design themes
│   └── navigation/        # Navigation graph
└── assets/
    ├── guides/
    │   └── first_aid_data.json
    └── images/            # Guide illustrations
```

## Key Architectural Patterns

### MVVM Layer Separation
- **View Layer**: Activities, Fragments, XML layouts
- **ViewModel Layer**: Business logic, LiveData, UI state management
- **Model Layer**: Repository pattern, Room database, JSON data

### Package Organization
- **data/**: All data-related classes (database, models, repositories)
- **ui/**: Feature-based UI organization (each screen gets its own package)
- **utils/**: Shared utilities and helper classes

### Resource Organization
- **Offline-First**: All content stored in `assets/` folder
- **Material Design**: Consistent theming in `values/themes.xml`
- **Localization Ready**: All strings externalized to `strings.xml`

### Navigation Structure
- Bottom navigation with 5 tabs: Home, Search, Emergency, Favorites, Settings
- Fragment-based navigation using Navigation Component
- Deep linking support for direct guide access

## File Naming Conventions
- **Activities**: `MainActivity.kt`, `SettingsActivity.kt`
- **Fragments**: `HomeFragment.kt`, `GuideDetailFragment.kt`
- **ViewModels**: `HomeViewModel.kt`, `GuideViewModel.kt`
- **Layouts**: `activity_main.xml`, `fragment_home.xml`, `item_guide_card.xml`
- **Resources**: `ic_heart.xml`, `bg_emergency.xml`

## Data Storage Strategy
- **Static Content**: JSON files in `assets/guides/`
- **User Data**: Room database (favorites, contacts, settings)
- **Images**: Bundled in `drawable/` and `assets/images/`
- **No External Dependencies**: Fully offline-capable