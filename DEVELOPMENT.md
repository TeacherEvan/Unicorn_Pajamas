# RepoSync Development Guide

## Quick Start for Developers

### Prerequisites

1. **Android Studio**: Hedgehog (2023.1.1) or newer
2. **JDK**: 17 or newer
3. **Android SDK**: API 34 (Android 14)
4. **Python**: 3.8+ (for Chaquopy)
5. **Git**: For version control

### First Build

```bash
# Clone the repository
git clone https://github.com/TeacherEvan/Unicorn_Pajamas.git
cd Unicorn_Pajamas

# Build the project
./gradlew build

# Run tests
./gradlew test

# Install on device/emulator
./gradlew installDebug
```

## Project Architecture

### Layers

1. **UI Layer**: Activities, Fragments, Adapters
2. **Domain Layer**: Business logic, Use cases
3. **Data Layer**: Repository, Database, API
4. **Security Layer**: Encryption, Authentication

### Key Components

#### MainActivity
Entry point for the application. Handles:
- User input for GitHub URL and HF Repo ID
- Token setup dialog
- Sync initiation
- History display

#### RepoSyncWorker
Background worker that:
- Clones GitHub repository
- Uploads to Hugging Face
- Updates sync status
- Handles errors

#### SecureTokenStorage
Manages API tokens:
- Encrypts using Android Keystore
- Provides secure storage/retrieval
- Never logs tokens

#### repo_sync.py
Python module that:
- Performs Git operations
- Interfaces with Hugging Face API
- Manages temporary files
- Handles cleanup

## Development Tips

### Debugging

#### Logcat Filtering
```bash
adb logcat -s RepoSyncWorker:D MainActivity:D repo_sync:D
```

#### Python Debugging
```kotlin
// Add to worker
val python = Python.getInstance()
python.getModule("sys").get("stderr").callAttr("write", "Debug message\n")
```

#### Database Inspection
```bash
# Pull database from device
adb pull /data/data/com.teacherevan.reposync/databases/sync_database .

# Use SQLite browser or CLI
sqlite3 sync_database
```

### Testing

#### Unit Tests
Test business logic without Android dependencies:
```bash
./gradlew test
```

#### Instrumented Tests
Test on device/emulator:
```bash
./gradlew connectedAndroidTest
```

#### Manual Testing Checklist
- [ ] Token storage and retrieval
- [ ] GitHub URL validation
- [ ] HF Repo ID validation
- [ ] Sync operation (small repo)
- [ ] Sync operation (large repo)
- [ ] Error handling (invalid URL)
- [ ] Error handling (invalid token)
- [ ] Biometric authentication
- [ ] History display
- [ ] Database persistence

### Common Issues

#### Chaquopy Build Errors
```bash
# Clear Chaquopy cache
rm -rf ~/.chaquopy

# Clean and rebuild
./gradlew clean build
```

#### Python Module Not Found
```kotlin
// Ensure Python is initialized
if (!Python.isStarted()) {
    AndroidPlatform.start(applicationContext)
}
```

#### Room Schema Errors
```bash
# Delete and reinstall
adb uninstall com.teacherevan.reposync
./gradlew installDebug
```

## Code Style

### Kotlin
```kotlin
// Use meaningful names
class SyncHistoryAdapter : ListAdapter<SyncRecord, ViewHolder>() {
    
    // Document public APIs
    /**
     * Binds sync record to view holder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
```

### Python
```python
def sync_repos(github_url: str, hf_repo_id: str, hf_token: str) -> dict:
    """
    Synchronize GitHub repository to Hugging Face.
    
    Args:
        github_url: The GitHub repository URL
        hf_repo_id: The Hugging Face repository ID
        hf_token: Hugging Face authentication token
        
    Returns:
        dict: Status information about the sync
    """
    pass
```

## Performance Optimization

### Tips
1. **Use WorkManager constraints**: Only sync when connected
2. **Clean temporary files**: Delete after each sync
3. **Limit database queries**: Use pagination
4. **Cache static data**: Reduce redundant queries
5. **Optimize layouts**: Use ConstraintLayout, avoid nesting

### Monitoring
```kotlin
// Track sync duration
val startTime = System.currentTimeMillis()
performSync()
val duration = System.currentTimeMillis() - startTime
Log.d(TAG, "Sync completed in ${duration}ms")
```

## Security Checklist

- [ ] No hardcoded credentials
- [ ] No tokens in logs
- [ ] Encrypted storage for secrets
- [ ] Input validation on all inputs
- [ ] HTTPS for all network calls
- [ ] No shell=True in subprocess
- [ ] Timeout for long operations
- [ ] Biometric authentication enabled
- [ ] ProGuard rules for release builds

## Release Process

### Version Numbering
Follow Semantic Versioning (MAJOR.MINOR.PATCH):
- MAJOR: Breaking changes
- MINOR: New features (backward compatible)
- PATCH: Bug fixes

### Creating a Release

1. **Update version**:
   ```kotlin
   // In app/build.gradle.kts
   versionCode = 2
   versionName = "1.1.0"
   ```

2. **Update changelog**:
   ```markdown
   ## [1.1.0] - 2024-01-15
   ### Added
   - GitHub token support for private repos
   ### Fixed
   - Sync failure with large repositories
   ```

3. **Build release**:
   ```bash
   ./gradlew assembleRelease
   ```

4. **Sign APK**: Configure signing in build.gradle.kts

5. **Tag release**:
   ```bash
   git tag -a v1.1.0 -m "Release version 1.1.0"
   git push origin v1.1.0
   ```

## Useful Commands

```bash
# List connected devices
adb devices

# Install APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Clear app data
adb shell pm clear com.teacherevan.reposync

# View database
adb shell "run-as com.teacherevan.reposync cat databases/sync_database" > sync_database

# Monitor logs
adb logcat | grep -E "RepoSync|reposync"

# Take screenshot
adb shell screencap -p /sdcard/screenshot.png
adb pull /sdcard/screenshot.png
```

## Resources

### Documentation
- [Android Developers](https://developer.android.com/)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Chaquopy Documentation](https://chaquo.com/chaquopy/doc/current/)
- [Hugging Face API](https://huggingface.co/docs/huggingface_hub/)

### Libraries
- [Room Persistence](https://developer.android.com/training/data-storage/room)
- [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- [Material Components](https://material.io/develop/android)
- [Biometric Authentication](https://developer.android.com/training/sign-in/biometric-auth)

### Community
- [Android Development](https://www.reddit.com/r/androiddev/)
- [Kotlin Discussions](https://discuss.kotlinlang.org/)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/android)

## Getting Help

1. Check documentation first
2. Search existing issues
3. Ask in discussions
4. Open a new issue with details

Happy coding! 🚀
