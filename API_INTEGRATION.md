# API Integration Guide

## Overview

Unicorn Pajamas provides multiple ways to integrate with external systems for sending and receiving repository URLs.

## Receiving Repository URLs

### Deep Links

The app registers deep link handlers for HTTP/HTTPS URLs. Any application can send a repository URL to Unicorn Pajamas.

#### Android Intent Example

```kotlin
// From another Android app
val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/username/repo.git"))
intent.setPackage("com.teacherevan.unicornpajamas") // Optional: target specific app
startActivity(intent)
```

#### ADB Command Example

```bash
# Send a GitHub URL to the app
adb shell am start -a android.intent.action.VIEW \
  -d "https://github.com/username/repo.git" \
  com.teacherevan.unicornpajamas
```

### Intent Filters

The app's manifest declares the following intent filters:

```xml
<intent-filter android:autoVerify="true">
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="http" android:host="*" />
    <data android:scheme="https" android:host="*" />
</intent-filter>
```

## Sending Repository URLs

### From Web Browsers

Users can click on repository links in web browsers, and Android will offer Unicorn Pajamas as an option to handle the URL.

### Custom URL Scheme (Future Enhancement)

For more specific integration, you could define a custom URL scheme like:

```
unicornpajamas://sync?github=https://github.com/user/repo&hf=https://huggingface.co/user/repo
```

## WorkManager API

### Background Sync

The app uses Android WorkManager for background synchronization tasks. This ensures:

- **Reliability**: Tasks are guaranteed to execute even after app restarts
- **Battery Optimization**: Respects Android's battery optimization
- **Network Awareness**: Only executes when network is available

### Programmatic Access

While the app doesn't expose a public API, you can integrate with it by:

1. **Intent-based**: Send URLs via intents (recommended)
2. **Shared WorkManager**: If both apps are in the same process, share WorkManager instance
3. **Broadcast Receivers**: Add broadcast receivers for sync completion events (future enhancement)

## Security Considerations

### Token Storage

- Tokens are stored using `EncryptedSharedPreferences`
- Uses AES256_GCM encryption
- Master key is generated using Android Keystore
- Tokens are never exposed in logs or UI (shown as masked)

### Network Security

- App uses HTTPS for all network communication
- `usesCleartextTraffic` is set to `false` in manifest
- Network security config can be added for certificate pinning

### Permissions

Required permissions:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

Optional (for older Android versions):
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="28" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="28" />
```

## Example Integrations

### 1. Browser Extension

Create a browser extension that sends repository URLs to the app:

```javascript
// Chrome/Firefox extension
function sendToUnicornPajamas(url) {
  // On mobile, this would trigger the intent
  window.location = url;
}

// When user clicks on a GitHub repo
sendToUnicornPajamas('https://github.com/username/repo.git');
```

### 2. GitHub Action

Create a GitHub Action that triggers the sync:

```yaml
name: Sync to Hugging Face
on: [push]
jobs:
  notify-android:
    runs-on: ubuntu-latest
    steps:
      - name: Send notification
        run: |
          # Send notification to a server that forwards to Android device
          curl -X POST https://your-server.com/notify \
            -d "repo=${{ github.repository }}"
```

### 3. Server-Side Integration

Build a server that receives webhooks and forwards to Android devices:

```python
from flask import Flask, request
import requests

app = Flask(__name__)

@app.route('/webhook', methods=['POST'])
def github_webhook():
    repo_url = request.json['repository']['clone_url']
    # Forward to Android device via Firebase Cloud Messaging
    send_fcm_message(device_token, {
        'action': 'sync',
        'github_url': repo_url
    })
    return 'OK'
```

### 4. Tasker Integration (Android)

Use Tasker to automate repository syncing:

```
Profile: GitHub Notification
Event: Notification [ Owner Application:GitHub Cat:* ]

Task: Sync Repo
A1: Send Intent [
    Action: android.intent.action.VIEW
    Data: %NOTIFY_TEXT
    Package: com.teacherevan.unicornpajamas
]
```

## Future API Enhancements

Potential additions for better integration:

1. **REST API Server**: Embedded HTTP server for receiving sync requests
2. **Broadcast Receivers**: Broadcast sync completion events
3. **Content Provider**: Expose sync history and status
4. **Service Binding**: Allow other apps to bind to a sync service
5. **Firebase Cloud Messaging**: Remote sync triggering
6. **Webhook Support**: Direct webhook receiver from GitHub/Hugging Face

## Error Handling

The app provides detailed error messages through:

- **Log View**: Real-time logging in the UI
- **Toast Notifications**: Brief error/success messages
- **Status Text**: Current operation status
- **WorkManager Status**: Accessible via WorkManager API

## Testing the Integration

### Test Deep Link

```bash
# Test with ADB
adb shell am start -a android.intent.action.VIEW \
  -d "https://github.com/torvalds/linux.git" \
  com.teacherevan.unicornpajamas

# Check logs
adb logcat | grep UnicornPajamas
```

### Test from Another App

```kotlin
// In your Android app
fun openInUnicornPajamas(repoUrl: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repoUrl))
        intent.setPackage("com.teacherevan.unicornpajamas")
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // App not installed
        Toast.makeText(this, "Unicorn Pajamas not installed", Toast.LENGTH_SHORT).show()
    }
}
```

## Best Practices

1. **Validate URLs**: Always validate repository URLs before sending
2. **Handle Errors**: Check if the app is installed before sending intents
3. **User Consent**: Ask user permission before triggering automated syncs
4. **Rate Limiting**: Implement rate limiting to avoid overwhelming the service
5. **Token Security**: Never transmit tokens via intents or insecure channels

## Support

For integration support or feature requests, please open an issue on GitHub.
