# Design Document

## Overview

The Emergency Guide System is designed as an offline-first Android application following MVVM architecture principles. The system prioritizes simplicity, accessibility, and reliability during high-stress emergency situations. The design emphasizes large touch targets, clear visual hierarchy, and minimal cognitive load to ensure usability when users are under pressure.

## Architecture

### High-Level Architecture

```
┌─────────────────────────────────────┐
│            Presentation Layer        │
│  (Activities, Fragments, ViewModels) │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│           Domain Layer               │
│     (Use Cases, Repositories)        │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│            Data Layer                │
│  (Room Database, JSON Assets, DAOs)  │
└─────────────────────────────────────┘
```

### MVVM Implementation

- **View Layer**: XML layouts with Material Design 3 components, Fragments for each screen
- **ViewModel Layer**: Manages UI state, handles user interactions, coordinates with repositories
- **Model Layer**: Repository pattern for data access, Room database for user data, JSON assets for static content

## Components and Interfaces

### Core Components

#### 1. Navigation System
- **Bottom Navigation**: 5 tabs (Home, Search, Emergency, Favorites, Settings)
- **Fragment Navigation**: Using Navigation Component with single Activity architecture
- **Deep Linking**: Support for direct guide access via custom URLs

#### 2. Guide Display System
- **ViewPager2**: For swipeable step navigation
- **Progress Indicator**: Custom component showing "Step X of Y"
- **Content Renderer**: Handles text, images, and warning displays
- **Action Bar**: Always-visible emergency call button

#### 3. Voice Assistant Module
- **TextToSpeech Integration**: Android TTS API for voice guidance
- **Speech Recognition**: Optional voice commands ("next", "repeat")
- **Foreground Service**: Keeps voice active when screen is locked
- **Audio Focus Management**: Handles interruptions from calls/notifications

#### 4. CPR Metronome Component
- **Custom Canvas View**: Animated pulsing circle
- **Audio Engine**: MediaPlayer for beat sounds and breath prompts
- **Timing Controller**: Precise BPM control using Coroutines
- **Haptic Feedback**: VibrationEffect API integration

#### 5. Search Engine
- **Full-Text Search**: Room FTS for fast content searching
- **Query Processor**: Handles typos and partial matches
- **History Manager**: Stores and retrieves recent searches
- **Result Ranking**: Relevance-based result ordering

### Key Interfaces

```kotlin
interface GuideRepository {
    suspend fun getAllGuides(): List<FirstAidGuide>
    suspend fun getGuideById(id: String): FirstAidGuide?
    suspend fun searchGuides(query: String): List<FirstAidGuide>
    suspend fun getFavoriteGuides(): List<FirstAidGuide>
}

interface VoiceAssistant {
    fun startVoiceGuidance(guide: FirstAidGuide)
    fun speakStep(step: GuideStep)
    fun pauseGuidance()
    fun resumeGuidance()
    fun stopGuidance()
}

interface CPRMetronome {
    fun startMetronome(bpm: Int)
    fun stopMetronome()
    fun setHapticEnabled(enabled: Boolean)
    fun setSoundEnabled(enabled: Boolean)
}
```

## Data Models

### Core Data Structures

```kotlin
@Entity(tableName = "guides")
data class FirstAidGuide(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val description: String,
    val iconResource: String,
    val searchKeywords: List<String>,
    val estimatedTime: String,
    val difficulty: String,
    val isFavorite: Boolean = false
)

@Entity(tableName = "guide_steps")
data class GuideStep(
    @PrimaryKey val id: String,
    val guideId: String,
    val stepNumber: Int,
    val instruction: String,
    val imageResource: String?,
    val voiceScript: String,
    val warnings: List<String>,
    val isEmergencyStep: Boolean = false
)

@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phoneNumber: String,
    val relationship: String,
    val isPredefined: Boolean = false,
    val displayOrder: Int
)

data class NavigatorNode(
    val id: String,
    val question: String,
    val questionType: QuestionType,
    val yesNextId: String?,
    val noNextId: String?,
    val resultGuideId: String?,
    val isEndNode: Boolean,
    val iconResource: String?
)

enum class QuestionType {
    CONSCIOUSNESS, BREATHING, BLEEDING, PAIN, MOVEMENT
}
```

### Database Schema

```kotlin
@Database(
    entities = [FirstAidGuide::class, GuideStep::class, EmergencyContact::class, SearchHistory::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun guideDao(): GuideDao
    abstract fun contactDao(): ContactDao
    abstract fun searchDao(): SearchDao
}
```

## Error Handling

### Error Categories

1. **Data Loading Errors**
   - JSON parsing failures
   - Missing asset files
   - Database corruption

2. **Voice System Errors**
   - TTS initialization failure
   - Audio focus conflicts
   - Speech recognition unavailable

3. **Hardware Errors**
   - Vibration not supported
   - Audio output unavailable
   - Low memory conditions

### Error Recovery Strategies

```kotlin
sealed class AppError {
    object DataLoadingError : AppError()
    object VoiceUnavailableError : AppError()
    object AudioUnavailableError : AppError()
    data class NetworkError(val message: String) : AppError()
}

class ErrorHandler {
    fun handleError(error: AppError): ErrorAction {
        return when (error) {
            is AppError.DataLoadingError -> ErrorAction.ShowOfflineMessage
            is AppError.VoiceUnavailableError -> ErrorAction.DisableVoiceFeatures
            is AppError.AudioUnavailableError -> ErrorAction.ShowVisualOnlyMode
            is AppError.NetworkError -> ErrorAction.ContinueOffline
        }
    }
}
```

## Testing Strategy

### Unit Testing
- **ViewModels**: Test business logic and state management
- **Repositories**: Test data access and caching
- **Use Cases**: Test domain logic and error handling
- **Utilities**: Test voice assistant and metronome functionality

### Integration Testing
- **Database Operations**: Test Room database queries and migrations
- **Navigation Flow**: Test fragment transitions and deep linking
- **Voice Integration**: Test TTS and speech recognition
- **Search Functionality**: Test full-text search and ranking

### UI Testing
- **Critical Paths**: Test emergency guide access and navigation
- **Voice Mode**: Test hands-free operation
- **CPR Metronome**: Test timing accuracy and visual feedback
- **Accessibility**: Test with TalkBack and large text

### Performance Testing
- **Launch Time**: Measure cold start performance (target < 2 seconds)
- **Search Speed**: Measure query response time (target < 1 second)
- **Memory Usage**: Monitor RAM consumption (target < 200MB)
- **Battery Impact**: Test background service efficiency

## User Experience Design

### Design Principles
1. **Emergency-First**: Large buttons, high contrast, minimal steps
2. **Accessibility**: Support for screen readers, large text, color blind users
3. **Offline Reliability**: No network dependencies for core functionality
4. **Stress-Friendly**: Clear visual hierarchy, simple language, obvious actions

### Visual Design System

#### Color Palette
```kotlin
object AppColors {
    val Emergency = Color(0xFFD32F2F)      // Red - Emergency actions
    val Medical = Color(0xFF1976D2)        // Blue - Medical information
    val Success = Color(0xFF388E3C)        // Green - Positive actions
    val Warning = Color(0xFFFF9800)        // Orange - Warnings
    val Background = Color(0xFFFFFFFF)     // White - Background
    val Surface = Color(0xFFF5F5F5)        // Light gray - Cards
}
```

#### Typography Scale
```kotlin
object AppTypography {
    val EmergencyTitle = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold)
    val StepTitle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium)
    val StepContent = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal)
    val ButtonText = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
}
```

### Screen Layouts

#### Home Screen Layout
- **Header**: App title with emergency call button
- **Category Grid**: 2x4 grid of emergency categories
- **Quick Actions**: Voice mode toggle, symptom navigator
- **Bottom Navigation**: 5-tab navigation bar

#### Guide Detail Layout
- **Progress Bar**: Step indicator at top
- **Content Area**: Image, instruction text, warnings
- **Navigation**: Previous/Next buttons, voice mode toggle
- **Emergency Bar**: Always-visible call button

#### CPR Metronome Layout
- **Central Animation**: Large pulsing circle
- **Counter Display**: Compression count and cycle number
- **Controls**: Start/Stop, sound toggle, vibration toggle
- **Instructions**: Brief text guidance

## Performance Considerations

### Optimization Strategies

1. **Image Loading**
   - Use WebP format for smaller file sizes
   - Implement lazy loading for guide images
   - Cache frequently accessed images

2. **Database Performance**
   - Index search keywords for fast queries
   - Use Room's built-in FTS for text search
   - Implement pagination for large result sets

3. **Memory Management**
   - Release TTS resources when not needed
   - Use weak references for large objects
   - Implement proper lifecycle management

4. **Battery Optimization**
   - Use JobScheduler for background tasks
   - Minimize wake locks usage
   - Optimize audio playback efficiency

### Monitoring and Analytics

```kotlin
interface PerformanceMonitor {
    fun trackLaunchTime(duration: Long)
    fun trackSearchTime(query: String, duration: Long)
    fun trackVoiceUsage(guideId: String, duration: Long)
    fun trackCPRSession(duration: Long, cycles: Int)
}
```

## Security and Privacy

### Data Protection
- **Local Storage Only**: No cloud sync for sensitive data
- **Encryption**: Encrypt personal emergency contacts
- **Permissions**: Minimal required permissions (CALL_PHONE, VIBRATE)
- **No Analytics**: No user tracking or data collection

### Content Safety
- **Medical Accuracy**: All content reviewed by medical professionals
- **Age Appropriate**: No graphic medical imagery
- **Disclaimers**: Clear medical disclaimer on first launch
- **Source Attribution**: Credit medical organizations

## Accessibility Features

### Screen Reader Support
- **Content Descriptions**: All images have meaningful descriptions
- **Navigation Hints**: Clear focus indicators and navigation cues
- **Semantic Markup**: Proper heading hierarchy and landmarks

### Motor Accessibility
- **Large Touch Targets**: Minimum 48dp touch targets
- **Voice Control**: Complete voice navigation support
- **Switch Navigation**: Support for external switch devices

### Visual Accessibility
- **High Contrast**: WCAG AA compliant color ratios
- **Large Text**: Support for system text scaling
- **Color Independence**: No color-only information conveyance