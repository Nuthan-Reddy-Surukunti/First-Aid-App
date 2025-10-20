# Gemini API Key Setup Guide

## ⚠️ IMPORTANT: API Key Required

The app has been switched from Firebase Vertex AI to Google Generative AI SDK to resolve dependency issues. You now need a Gemini API key.

## How to Get Your Gemini API Key

1. **Visit Google AI Studio**
   - Go to: https://makersuite.google.com/app/apikey
   - Or: https://aistudio.google.com/app/apikey

2. **Sign in with your Google Account**

3. **Create API Key**
   - Click "Create API Key"
   - Select "Create API key in new project" (or use existing project)
   - Copy the generated API key

4. **Add the API Key to Your App**

### Option 1: Direct in Code (Quick Testing)
Open `GeminiAIManager.kt` and replace:
```kotlin
private val apiKey = "YOUR_GEMINI_API_KEY_HERE"
```
with:
```kotlin
private val apiKey = "YOUR_ACTUAL_API_KEY"
```

### Option 2: Secure Storage (Recommended for Production)

1. Add to `local.properties` (this file is gitignored):
   ```properties
   GEMINI_API_KEY=your_actual_api_key_here
   ```

2. Update `app/build.gradle.kts` in the `android` block:
   ```kotlin
   android {
       // ...existing config...
       
       defaultConfig {
           // ...existing config...
           
           // Read API key from local.properties
           val properties = Properties()
           properties.load(project.rootProject.file("local.properties").inputStream())
           buildConfigField("String", "GEMINI_API_KEY", "\"${properties.getProperty("GEMINI_API_KEY", "")}\"")
       }
   }
   ```

3. Update `GeminiAIManager.kt`:
   ```kotlin
   private val apiKey = BuildConfig.GEMINI_API_KEY
   ```

## Why the Change?

**Before:** Firebase Vertex AI SDK
- Required complex Ktor HTTP client dependencies
- ClassNotFoundException for `io.ktor.client.plugins.HttpTimeout`
- More dependencies = more potential conflicts

**After:** Google Generative AI SDK
- Simpler, direct SDK
- Fewer dependencies
- No Ktor required
- Same Gemini models and capabilities

## API Key Limits

- **Free Tier**: 60 requests per minute
- Perfect for testing and personal use
- For production apps with many users, consider upgrading

## Testing

After adding your API key:
1. Sync Gradle
2. Rebuild the app
3. Click "AI Assistant" button
4. The app should no longer crash!

## Security Notes

⚠️ **Never commit API keys to version control!**
- Use `local.properties` (already in `.gitignore`)
- Or use BuildConfig with environment variables
- For production, consider server-side API calls

## Troubleshooting

If you still see errors:
1. Verify API key is correct (no extra spaces)
2. Check API key is enabled at https://console.cloud.google.com/apis/
3. Enable "Generative Language API" if needed
4. Clean and rebuild project: `./gradlew clean build`

