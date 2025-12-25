# Build Instructions

## Prerequisites

### Required Software

1. **Android Studio**: Arctic Fox (2020.3.1) or later
   - Download from: https://developer.android.com/studio

2. **Java Development Kit (JDK)**: 11 or later
   - Android Studio includes OpenJDK
   - Or download from: https://www.oracle.com/java/technologies/downloads/

3. **Android SDK**: API Level 26 (Android 8.0) minimum
   - Target SDK: API Level 34
   - Will be downloaded automatically via Android Studio

### Required Tokens

Before using the app, you need:

1. **GitHub Personal Access Token (PAT)**
   - Create at: https://github.com/settings/tokens
   - Required scopes: `repo` (full control of private repositories)
   - For public repos only: `public_repo` scope

2. **Hugging Face Access Token**
   - Create at: https://huggingface.co/settings/tokens
   - Required permission: `write`

## Building from Source

### Using Android Studio (Recommended)

1. **Clone the repository**:
   ```bash
   git clone https://github.com/TeacherEvan/Unicorn_Pajamas.git
   cd Unicorn_Pajamas
   ```

2. **Open in Android Studio**:
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory
   - Click "OK"

3. **Sync Gradle**:
   - Android Studio will automatically prompt to sync Gradle
   - If not, click "File" → "Sync Project with Gradle Files"
   - Wait for dependencies to download

4. **Build the project**:
   - Click "Build" → "Make Project"
   - Or press `Ctrl+F9` (Windows/Linux) / `Cmd+F9` (Mac)

5. **Run the app**:
   - Connect an Android device or start an emulator
   - Click the "Run" button or press `Shift+F10`
   - Select your device and click "OK"

### Using Command Line

1. **Clone the repository**:
   ```bash
   git clone https://github.com/TeacherEvan/Unicorn_Pajamas.git
   cd Unicorn_Pajamas
   ```

2. **Build debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```
   
   On Windows:
   ```cmd
   gradlew.bat assembleDebug
   ```

3. **Build release APK** (requires signing):
   ```bash
   ./gradlew assembleRelease
   ```

4. **Install on device**:
   ```bash
   ./gradlew installDebug
   ```

5. **Run tests**:
   ```bash
   ./gradlew test
   ```

### Build Outputs

Built APKs will be located at:
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

## Configuration

### Gradle Properties

Create `local.properties` in the root directory (if not exists):
```properties
sdk.dir=/path/to/Android/sdk
```

### Signing Configuration (For Release Builds)

1. **Create a keystore**:
   ```bash
   keytool -genkey -v -keystore unicorn-pajamas.keystore \
     -alias unicorn -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Add to `gradle.properties`** (don't commit these):
   ```properties
   KEYSTORE_FILE=/path/to/unicorn-pajamas.keystore
   KEYSTORE_PASSWORD=your_keystore_password
   KEY_ALIAS=unicorn
   KEY_PASSWORD=your_key_password
   ```

3. **Update `app/build.gradle`** (if needed):
   ```gradle
   android {
       signingConfigs {
           release {
               storeFile file(KEYSTORE_FILE)
               storePassword KEYSTORE_PASSWORD
               keyAlias KEY_ALIAS
               keyPassword KEY_PASSWORD
           }
       }
       buildTypes {
           release {
               signingConfig signingConfigs.release
               minifyEnabled false
               proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
           }
       }
   }
   ```

## Troubleshooting

### Common Build Errors

#### "SDK location not found"
**Solution**: Create `local.properties` with your SDK path
```properties
sdk.dir=/Users/username/Library/Android/sdk  # Mac
sdk.dir=C\:\\Users\\username\\AppData\\Local\\Android\\sdk  # Windows
sdk.dir=/home/username/Android/Sdk  # Linux
```

#### "Gradle sync failed"
**Solution**: 
1. Check internet connection
2. Click "File" → "Invalidate Caches / Restart"
3. Try "Build" → "Clean Project" then "Build" → "Rebuild Project"

#### "Minimum supported Gradle version"
**Solution**: Update Gradle wrapper
```bash
./gradlew wrapper --gradle-version 8.2
```

#### "Failed to resolve: org.eclipse.jgit"
**Solution**: 
1. Check internet connection
2. Add JitPack repository if needed
3. Sync Gradle again

### Dependency Issues

If you encounter dependency resolution issues:

1. **Clear Gradle cache**:
   ```bash
   ./gradlew clean
   rm -rf ~/.gradle/caches/
   ```

2. **Re-download dependencies**:
   ```bash
   ./gradlew build --refresh-dependencies
   ```

## Development Setup

### Code Style

The project follows Kotlin coding conventions:
- Install "Kotlin" plugin in Android Studio
- Use "Code" → "Reformat Code" (`Ctrl+Alt+L`)

### Git Hooks (Optional)

Set up pre-commit hooks for code quality:

```bash
# Create .git/hooks/pre-commit
#!/bin/sh
./gradlew ktlintCheck
```

Make it executable:
```bash
chmod +x .git/hooks/pre-commit
```

## Testing

### Run Unit Tests

```bash
./gradlew test
```

### Run Instrumented Tests

Requires connected device or emulator:
```bash
./gradlew connectedAndroidTest
```

### Generate Test Coverage Report

```bash
./gradlew jacocoTestReport
```

## Continuous Integration

### GitHub Actions

Create `.github/workflows/android.yml`:

```yaml
name: Android CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
    - name: Build with Gradle
      run: ./gradlew build
    - name: Run tests
      run: ./gradlew test
```

## Deployment

### Google Play Store

1. Build release APK with proper signing
2. Create a Google Play Developer account
3. Follow the Play Console upload process

### Direct Distribution

1. Build release APK
2. Enable "Unknown Sources" on Android device
3. Transfer APK to device
4. Install via file manager

## Minimum System Requirements

### Development Machine

- **OS**: Windows 10/11, macOS 10.14+, or Linux (64-bit)
- **RAM**: 8 GB minimum (16 GB recommended)
- **Disk Space**: 4 GB for Android Studio + 2 GB for SDK
- **Screen Resolution**: 1280x800 minimum

### Target Device

- **Android Version**: 8.0 (API 26) or higher
- **RAM**: 2 GB minimum
- **Storage**: 50 MB for app + space for cloned repositories
- **Network**: WiFi or cellular data connection

## Additional Resources

- [Android Developer Guide](https://developer.android.com/guide)
- [Kotlin Documentation](https://kotlinx.coroutines/)
- [JGit Documentation](https://www.eclipse.org/jgit/)
- [Material Design Guidelines](https://material.io/design)

## Support

For build issues, please:
1. Check this documentation
2. Search existing GitHub issues
3. Create a new issue with build logs
