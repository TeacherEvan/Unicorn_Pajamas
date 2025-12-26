# Voice Integration with ElevenLabs

## Overview

This document describes the integration of ElevenLabs Text-to-Speech (TTS) API into Unicorn Pajamas to provide a welcoming voice experience when users launch the app.

## Features

- **Dual Voice Welcome Sequence**: A female voice welcomes users, followed by a male voice delivering the tagline
- **Stoic and Gentle Male Voice**: The male voice says "Learning through games for everyone" in a calm, measured tone
- **Secure API Key Storage**: ElevenLabs API key is stored using Android's EncryptedSharedPreferences
- **Automatic Audio Chaining**: Female voice automatically triggers the male voice upon completion

## Implementation Details

### VoiceService Class

The `VoiceService` class handles all voice-related functionality:

#### Key Methods

1. **`generateSpeech(text: String, voiceId: String, apiKey: String): File?`**
   - Calls the ElevenLabs API to convert text to speech
   - Uses optimal voice settings for stoic/gentle delivery:
     - `stability`: 0.7 (more stable, less variation)
     - `similarity_boost`: 0.7 (natural sounding)
     - `style`: 0.2 (subtle delivery for stoic tone)
   - Returns an MP3 file saved in the app's cache directory

2. **`playAudio(audioFile: File, onComplete: (() -> Unit)?)`**
   - Plays an audio file using Android's MediaPlayer
   - Invokes callback when playback completes
   - Handles cleanup and error scenarios

3. **`speak(text: String, voiceId: String, apiKey: String, onComplete: (() -> Unit)?)`**
   - Convenience method that generates and plays speech in one call
   - Chains generation and playback with completion callback

4. **`playWelcomeSequence(apiKey: String, onComplete: (() -> Unit)?)`**
   - Plays the complete welcome sequence
   - Female voice: "Welcome to Unicorn Pajamas!"
   - Male voice (after female completes): "Learning through games for everyone."

### Voice Selection

#### Male Voices (Stoic and Gentle)
- **Luca** (ID: `4JVOFy4SLQs9my0OLhEw`) - Default choice
  - Calm, sober, slightly breathy
  - Perfect for mellow, gentle narration
- **AJ** (ID: `OtD5NgAuh1zlbur5wvSw`)
  - Calm, steady, measured pace
  - Very engaging and gentle
- **George** (ID: `JBFqnCBsd6RMkjVDRZzb`)
  - British accent, warm resonance
  - Captivating but gentle

#### Female Voices
- **Rachel** (ID: `21m00Tcm4TlvDq8ikWAM`) - Default choice
  - Clear, friendly
  - Natural and welcoming
- **Bella** (ID: `EXAVITQu4vr4xnSDxMaL`)
  - Soft, young
  - Warm and approachable

### SecurePreferences Updates

Added methods to store and retrieve the ElevenLabs API key:
- `saveElevenLabsApiKey(apiKey: String)`
- `getElevenLabsApiKey(): String?`

The API key is encrypted using AES256_GCM encryption, same as other credentials.

### MainActivity Updates

1. **onCreate**: Initializes `VoiceService` and calls `playWelcomeVoice()`
2. **playWelcomeVoice()**: 
   - Checks if API key is configured
   - Ensures voice only plays once per app session
   - Launches coroutine to play welcome sequence
   - Logs progress to UI
3. **showSettingsDialog()**: Updated to include ElevenLabs API key input field
4. **onDestroy()**: Cleans up VoiceService resources

## Setup Instructions

### 1. Obtain ElevenLabs API Key

1. Sign up at [ElevenLabs](https://elevenlabs.io/)
2. Navigate to your account settings
3. Generate an API key
4. Copy the API key (you'll need it in step 2)

### 2. Configure in the App

1. Launch Unicorn Pajamas
2. Tap the Settings icon (gear) in the toolbar
3. Scroll to "ElevenLabs API Key" field
4. Paste your API key
5. Tap "Save Credentials"

### 3. Test the Voice

1. Close and relaunch the app
2. You should hear:
   - Female voice: "Welcome to Unicorn Pajamas!"
   - Male voice: "Learning through games for everyone."

## API Details

### Endpoint
```
POST https://api.elevenlabs.io/v1/text-to-speech/{voice_id}
```

### Headers
```
xi-api-key: YOUR_API_KEY
Content-Type: application/json
Accept: audio/mpeg
```

### Request Body
```json
{
  "text": "Your text here",
  "model_id": "eleven_monolingual_v1",
  "voice_settings": {
    "stability": 0.7,
    "similarity_boost": 0.7,
    "style": 0.2,
    "use_speaker_boost": true
  }
}
```

### Response
Binary audio data in MP3 format

## Best Practices

### Voice Settings for Stoic/Gentle Delivery

- **Stability (0.0-1.0)**: Higher values (0.7-0.8) produce more consistent, stable delivery
- **Similarity Boost (0.0-1.0)**: Higher values (0.7-0.8) make the voice sound more natural
- **Style (0.0-1.0)**: Lower values (0.1-0.3) produce subtle, understated delivery perfect for stoic tones
- **Speaker Boost**: Enabled for clarity and presence

### Audio Chaining

The implementation uses completion callbacks to chain audio:
```kotlin
speak("First message", femaleVoiceId, apiKey) {
    speak("Second message", maleVoiceId, apiKey) {
        // Both complete
    }
}
```

This ensures sequential playback without overlap.

### Resource Management

- Audio files are cached in `context.cacheDir` and automatically managed by Android
- MediaPlayer is properly released after each playback
- Coroutines are cancelled when MainActivity is destroyed

## Security Considerations

1. **Encrypted Storage**: API key is stored using EncryptedSharedPreferences with AES256_GCM
2. **No Logging**: API key is never logged or displayed in plain text
3. **HTTPS Only**: All API calls use HTTPS for secure transmission
4. **Input Masking**: API key input field shows bullets (••••••••) for existing keys

## Troubleshooting

### Voice Doesn't Play

1. **Check API Key**: Ensure ElevenLabs API key is configured in Settings
2. **Check Logs**: Look for error messages in the app's log view
3. **Network Connection**: Ensure device has internet connectivity
4. **API Quota**: Verify you haven't exceeded ElevenLabs API quota

### Common Log Messages

- `"ElevenLabs API key not configured"` - Set API key in Settings
- `"API request failed: 401"` - Invalid or expired API key
- `"Error generating speech"` - Network or API issue
- `"Welcome voice sequence completed"` - Success!

## Future Enhancements

- [ ] Add voice preference settings (choose different voices)
- [ ] Support for custom welcome messages
- [ ] Voice feedback for sync operations
- [ ] Multiple language support
- [ ] Offline voice caching for repeated phrases
- [ ] Volume control
- [ ] Ability to disable voice greetings

## References

- [ElevenLabs API Documentation](https://elevenlabs.io/docs/api-reference/text-to-speech)
- [ElevenLabs Voice Library](https://elevenlabs.io/voice-library)
- [Android MediaPlayer Guide](https://developer.android.com/guide/topics/media/mediaplayer)
- [EncryptedSharedPreferences](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences)

## Code Structure

```
app/src/main/java/com/teacherevan/unicornpajamas/
├── VoiceService.kt           # Core voice functionality
├── SecurePreferences.kt      # Secure storage (updated)
└── MainActivity.kt           # Voice playback trigger (updated)

app/src/main/res/
├── layout/
│   └── dialog_settings.xml  # Settings dialog (updated)
├── menu/
│   └── main_menu.xml        # App menu (updated)
└── values/
    └── strings.xml          # String resources (updated)
```

## License

This integration follows the same MIT License as the Unicorn Pajamas project.

## Author

Implementation by TeacherEvan using ElevenLabs Text-to-Speech API.
