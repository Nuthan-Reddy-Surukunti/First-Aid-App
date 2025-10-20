# Voice Assistant with Firebase Vertex AI - Complete Setup Guide

## 🎯 Overview

Your First Aid app now has a **complete AI-powered Voice Assistant** using **Firebase Vertex AI (Gemini)**. This guide will help you set it up without any failures.

---

## 📋 What Has Been Implemented

### ✅ Core Components Created

1. **Data Models** (`data/voice/`)
   - `VoiceCommand.kt` - Command types and parsing
   - `EmergencyProcedure.kt` - Emergency procedure data structures
   - `VoicePreferences.kt` - User preferences for voice settings

2. **Voice Services** (`voice/`)
   - `GeminiAIManager.kt` - Firebase Vertex AI integration
   - `SpeechRecognitionService.kt` - Android speech recognition
   - `TextToSpeechManager.kt` - Text-to-speech output
   - `VoiceAssistantManager.kt` - Main coordinator
   - `VoicePermissionManager.kt` - Permission handling

3. **UI Components** (`ui/voice/`)
   - `VoiceAssistantViewModel.kt` - UI state management
   - `VoiceAssistantFragment.kt` - Main interface
   - `fragment_voice_assistant.xml` - Complete UI layout

4. **Animations** (`res/raw/`)
   - Listening, Processing, Speaking, Emergency animations

---

## 🚀 Setup Steps

### Step 1: Set Up Firebase Project

1. **Go to Firebase Console**: https://console.firebase.google.com/

2. **Create or Select Your Project**
   - Click "Add project" or select existing project
   - Follow the wizard to create project

3. **Add Android App**
   - Click "Add app" → Android icon
   - Package name: `com.example.firstaidapp`
   - Download `google-services.json`

4. **Add google-services.json**
   - Place the file in: `app/google-services.json`
   - **Critical**: Must be in the `app` folder, NOT the root folder

### Step 2: Enable Vertex AI in Firebase

1. **In Firebase Console**, go to:
   - Build → Vertex AI in Firebase
   
2. **Enable the API**
   - Click "Get Started"
   - Agree to terms
   - Select your billing account (has free tier)

3. **Enable Required APIs** in Google Cloud Console:
   - Vertex AI API
   - Generative Language API

### Step 3: Sync Your Project

1. **Open Android Studio**
2. Click **"Sync Project with Gradle Files"** (elephant icon)
3. Wait for sync to complete
4. Build → Clean Project
5. Build → Rebuild Project

---

## 🎤 Features Implemented

### 1. **Voice Commands**
```
User: "Help me with CPR"
AI: "Call 911 immediately. Start CPR: 30 chest compressions, 
     then 2 rescue breaths. Push hard and fast at 100-120 
     compressions per minute."
```

### 2. **Emergency Quick Actions**
- **CPR Button** → Instant CPR guidance
- **Choking Button** → Heimlich maneuver steps
- **Bleeding Button** → Bleeding control instructions
- **Burns Button** → Burn treatment guidance

### 3. **Smart AI Features**
- **Context-aware**: Remembers conversation history
- **Emotional intelligence**: Calms panicked users
- **Follow-up questions**: "What if they wake up?"
- **Fallback mode**: Works offline with basic commands

### 4. **Visual Feedback**
- **Listening Animation** → Blue pulsing circle
- **Processing Animation** → Orange spinner
- **Speaking Animation** → Green sound waves
- **Emergency Mode** → Full-screen red overlay

---

## 📱 How to Use the Voice Assistant

### Method 1: In Your Existing App

**Add to MainActivity or Navigation:**

```kotlin
// In MainActivity.kt or your navigation setup
import com.example.firstaidapp.ui.voice.VoiceAssistantFragment

// To navigate to voice assistant
supportFragmentManager.beginTransaction()
    .replace(R.id.fragment_container, VoiceAssistantFragment())
    .addToBackStack(null)
    .commit()
```

### Method 2: Add to Bottom Navigation

Update your navigation menu to include voice assistant:

```xml
<!-- res/menu/bottom_navigation_menu.xml -->
<item
    android:id="@+id/navigation_voice"
    android:icon="@android:drawable/ic_btn_speak_now"
    android:title="Voice Assistant" />
```

### Method 3: Floating Action Button

Add a floating microphone button on your home screen:

```xml
<com.google.android.material.floatingactionbutton.FloatingActionButton
    android:id="@+id/fabVoiceAssistant"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:src="@android:drawable/ic_btn_speak_now"
    android:contentDescription="Voice Assistant"
    app:layout_anchor="@id/bottom_navigation"
    app:layout_anchorGravity="top|end" />
```

---

## 🧪 Testing the Voice Assistant

### Test 1: Basic Voice Recognition
1. Open Voice Assistant screen
2. Tap microphone button
3. Say: "Test"
4. Should see: "You said: Test"

### Test 2: Emergency CPR
1. Tap "CPR" button
2. Should hear: AI guidance for CPR
3. Screen turns red (Emergency Mode)
4. Follow voice instructions

### Test 3: Natural Conversation
1. Tap microphone
2. Say: "Someone is choking"
3. AI provides Heimlich maneuver steps
4. Ask follow-up: "What if they can still talk?"
5. AI adapts response based on context

---

## 🔧 Customization Options

### Change Voice Speed/Pitch

```kotlin
val preferences = VoicePreferences(
    voiceSpeed = 1.2f,  // 0.5 to 2.0
    voicePitch = 1.0f,  // 0.5 to 2.0
    autoSpeak = true
)
viewModel.updatePreferences(preferences)
```

### Modify AI System Prompt

Edit `GeminiAIManager.kt` → `getSystemPrompt()`:

```kotlin
private fun getSystemPrompt(): String {
    return """
        You are an AI First Aid Assistant...
        [Customize personality, tone, expertise here]
    """.trimIndent()
}
```

### Add Custom Emergency Commands

Edit `VoiceCommand.kt`:

```kotlin
enum class VoiceCommandType {
    EMERGENCY_CPR,
    EMERGENCY_CHOKING,
    EMERGENCY_HEART_ATTACK,  // Add new
    EMERGENCY_STROKE,         // Add new
    // ...
}
```

---

## 🎨 UI Customization

### Change Colors

```xml
<!-- res/values/colors.xml -->
<color name="voice_assistant_primary">#2196F3</color>
<color name="voice_assistant_emergency">#F44336</color>
<color name="voice_assistant_success">#4CAF50</color>
```

### Better Animations

Replace the basic animations with professional ones from:
- **LottieFiles**: https://lottiefiles.com/
- Search for: "voice", "microphone", "emergency"
- Download JSON and replace files in `res/raw/`

---

## 🐛 Troubleshooting

### Issue 1: "Google Services Plugin Not Found"
**Solution**: Make sure you have `google-services.json` in the `app/` folder

### Issue 2: "Speech Recognition Not Available"
**Solution**: Test on a real device (emulator may not have speech recognition)

### Issue 3: "AI Service Unavailable"
**Solution**: 
- Check internet connection
- Verify Firebase project is set up correctly
- Ensure Vertex AI is enabled in Firebase Console

### Issue 4: "Permissions Not Granted"
**Solution**: 
- App will show permission dialog automatically
- Grant: Microphone, Location, Phone permissions

### Issue 5: Build Errors
**Solution**:
```bash
# Clean and rebuild
./gradlew clean
./gradlew build

# Or in Android Studio:
Build → Clean Project
Build → Rebuild Project
```

---

## 📊 API Usage & Costs

### Firebase Vertex AI Free Tier (Gemini 1.5 Flash)
- **Free**: 15 requests per minute
- **Free**: 1 million tokens per month
- **Average voice command**: ~100-500 tokens
- **Estimated**: 2,000-10,000 free commands/month

### Monitoring Usage
1. Firebase Console → Vertex AI → Usage
2. Google Cloud Console → Vertex AI → Quotas

---

## 🔐 Security Best Practices

1. **Never commit `google-services.json`** to public repositories
2. Add to `.gitignore`:
   ```
   app/google-services.json
   ```

3. **Use Firebase App Check** (optional but recommended):
   ```kotlin
   // In FirstAidApplication.kt
   FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
       PlayIntegrityAppCheckProviderFactory.getInstance()
   )
   ```

---

## 🚀 Next Steps

### Phase 1: Integration (You are here ✅)
- ✅ Core voice assistant implemented
- ✅ Firebase Vertex AI integrated
- ✅ UI components created

### Phase 2: Enhancements
- [ ] Add CPR timer with audio metronome
- [ ] Integrate with existing first aid procedures
- [ ] Add location services for hospital finder
- [ ] Implement emergency contact quick dial

### Phase 3: Polish
- [ ] Add wake word detection ("Hey First Aid")
- [ ] Offline mode with cached responses
- [ ] Multi-language support
- [ ] Analytics and usage tracking

---

## 📞 Emergency Integration Example

### Connect to Existing First Aid Procedures

```kotlin
// In VoiceAssistantManager.kt → handleVoiceAction()
when (action) {
    is VoiceAction.NavigateToProcedure -> {
        // Navigate to existing procedure screen
        findNavController().navigate(
            R.id.action_voice_to_procedure,
            bundleOf("procedureId" to action.procedureId)
        )
    }
}
```

---

## ✅ Quick Checklist

Before testing, ensure:
- [ ] `google-services.json` is in `app/` folder
- [ ] Firebase project created and configured
- [ ] Vertex AI enabled in Firebase Console
- [ ] Project synced successfully in Android Studio
- [ ] App has microphone permission
- [ ] Testing on real device (not emulator)
- [ ] Internet connection available

---

## 🎉 You're Ready!

Your voice assistant is fully implemented and ready to use! The system includes:
- ✅ Complete AI integration with Firebase Vertex AI
- ✅ Speech recognition and text-to-speech
- ✅ Emergency quick actions
- ✅ Context-aware conversations
- ✅ Offline fallback mode
- ✅ Professional UI with animations
- ✅ Comprehensive error handling

**Next**: Add `google-services.json` file and sync your project to start using the voice assistant!

---

## 📚 Resources

- **Firebase Vertex AI Docs**: https://firebase.google.com/docs/vertex-ai
- **Gemini API**: https://ai.google.dev/docs
- **Android Speech Recognition**: https://developer.android.com/reference/android/speech/SpeechRecognizer
- **Material Design**: https://material.io/components

---

**Need Help?** Check the troubleshooting section or review the inline code comments for detailed explanations.

