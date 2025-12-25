# Unicorn Pajamas

**Pull to Push** - An Android application that automatically syncs Git repositories from GitHub to Hugging Face.

## Features

- 🔄 Clone repositories from GitHub
- 🚀 Push repositories to Hugging Face
- 🔐 Secure credential storage using Android EncryptedSharedPreferences
- 📱 Deep link support for receiving repository URLs
- ⚡ Background sync using WorkManager
- 📊 Real-time progress tracking and logging
- 🎨 Material Design UI

## How It Works

1. **Configure Credentials**: Set up your GitHub Personal Access Token (PAT) and Hugging Face token in the Settings menu
2. **Enter Repository URLs**: Input the GitHub repository URL you want to clone and the Hugging Face repository URL where you want to push
3. **Sync**: Tap the "Sync Repository" button to start the process
4. **Monitor Progress**: Watch real-time logs as the app clones from GitHub and pushes to Hugging Face

## Setup

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK API 26 (Android 8.0) or higher
- GitHub Personal Access Token with repo permissions
- Hugging Face Access Token with write permissions

### Building the App

1. Clone this repository:
   ```bash
   git clone https://github.com/TeacherEvan/Unicorn_Pajamas.git
   cd Unicorn_Pajamas
   ```

2. Open the project in Android Studio

3. Sync Gradle files and build the project

4. Run on an emulator or physical device

### Configuration

1. Launch the app
2. Tap the Settings icon (gear) in the toolbar
3. Enter your GitHub Personal Access Token
4. Enter your Hugging Face Access Token
5. Tap "Save Credentials"

## Usage

### Basic Sync

1. Enter the GitHub repository URL (e.g., `https://github.com/username/repo.git`)
2. Enter the Hugging Face repository URL (e.g., `https://huggingface.co/username/repo`)
3. Tap "Sync Repository"
4. Monitor progress in the log view

### Deep Link Support

You can also trigger the app with a repository URL via deep links:

```
adb shell am start -a android.intent.action.VIEW -d "https://github.com/username/repo.git" com.teacherevan.unicornpajamas
```

## Architecture

- **MainActivity**: Main UI and user interaction
- **GitService**: Git operations using JGit library
- **SyncWorker**: Background sync using WorkManager
- **SecurePreferences**: Encrypted credential storage

## Dependencies

- AndroidX Core, AppCompat, Material Design Components
- Kotlin Coroutines for asynchronous operations
- JGit for Git operations
- WorkManager for background tasks
- Retrofit/OkHttp for networking
- EncryptedSharedPreferences for secure storage

## Security

- Access tokens are stored using Android's EncryptedSharedPreferences
- AES256_GCM encryption for secure credential storage
- Tokens are never logged or exposed in the UI
- Network operations require authentication

## API Integration

### Receiving Links

The app supports Android deep links and can receive repository URLs from other applications:

```xml
<intent-filter android:autoVerify="true">
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="http" android:host="*" />
    <data android:scheme="https" android:host="*" />
</intent-filter>
```

### Sending Links

To send a repository URL to this app from another application:

```kotlin
val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/username/repo.git"))
startActivity(intent)
```

## Troubleshooting

### Common Issues

1. **Clone/Push Failures**: Verify your access tokens have the correct permissions
2. **Network Errors**: Ensure you have an active internet connection
3. **Authentication Errors**: Re-enter your credentials in Settings

### Logs

The app provides detailed logs for debugging:
- Clone progress and status
- Push operations
- Error messages with stack traces

## License

MIT License - See LICENSE file for details

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Author

TeacherEvan

## Acknowledgments

- JGit library for Git operations
- Android Jetpack components
- Material Design guidelines
