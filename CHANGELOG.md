# Changelog

All notable changes to Unicorn Pajamas will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-12-25

### Added - Initial Release

#### Core Features
- Repository synchronization from GitHub to Hugging Face
- Background sync using Android WorkManager
- Real-time progress tracking and logging
- Material Design 3 user interface
- Deep link support for repository URLs
- Secure credential storage with AES256_GCM encryption

#### Security
- EncryptedSharedPreferences for token storage
- HTTPS-only network communication
- Restricted deep link hosts (github.com, huggingface.co)
- Android Keystore integration
- Secure backup exclusion rules
- No sensitive data logging

#### User Interface
- Repository URL input fields
- Settings dialog for token configuration
- Progress bar with status text
- Scrollable log view
- Menu with settings and clear logs options
- Toast notifications for quick feedback

#### Technical Implementation
- Kotlin as primary language
- JGit 6.8.0 for Git operations
- WorkManager 2.9.0 for background tasks
- Security-Crypto 1.0.0 for encryption
- Retrofit/OkHttp for networking
- Material Components for UI

#### Documentation
- Comprehensive README with setup guide
- API_INTEGRATION.md for integration patterns
- BUILD.md with detailed build instructions
- SECURITY.md covering security best practices
- EXAMPLES.md with configuration examples
- ARCHITECTURE.md with system design diagrams
- PROJECT_SUMMARY.md with implementation overview

#### Testing
- Unit test infrastructure
- Example tests for GitService
- Android instrumented test support
- Dependency vulnerability scanning (all passed)

#### Build System
- Gradle 8.2 build configuration
- Gradle wrapper for consistent builds
- ProGuard rules for code optimization
- Debug and release build variants

### Security Fixes
- Updated security-crypto to stable 1.0.0 (from alpha)
- Restricted deep link hosts to prevent exploitation
- Fixed unsafe cast in MainActivity scroll handling
- Included gradle-wrapper.properties for build consistency

### Performance
- Efficient coroutine-based async operations
- Cache directory usage for temporary repositories
- Automatic cleanup of temporary files
- Battery-optimized background processing

### Known Limitations
- All branches synced by default (no single-branch selection)
- Force push enabled (may overwrite remote changes)
- No Git LFS support yet
- No conflict resolution UI
- Manual token entry required (no OAuth flow)

### Minimum Requirements
- Android 8.0 (API 26) or higher
- 50MB storage + repository sizes
- Internet connection
- GitHub Personal Access Token
- Hugging Face Access Token

### Dependencies
- androidx.core:core-ktx:1.12.0
- androidx.appcompat:appcompat:1.6.1
- com.google.android.material:material:1.11.0
- androidx.constraintlayout:constraintlayout:2.1.4
- org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3
- androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0
- androidx.lifecycle:lifecycle-livedata-ktx:2.7.0
- androidx.work:work-runtime-ktx:2.9.0
- com.squareup.retrofit2:retrofit:2.9.0
- com.squareup.retrofit2:converter-gson:2.9.0
- com.squareup.okhttp3:okhttp:4.12.0
- com.squareup.okhttp3:logging-interceptor:4.12.0
- org.eclipse.jgit:org.eclipse.jgit:6.8.0.202311291450-r
- androidx.security:security-crypto:1.0.0
- junit:junit:4.13.2
- androidx.test.ext:junit:1.1.5
- androidx.test.espresso:espresso-core:3.5.1

### Verified Security
- ✅ No vulnerabilities in dependencies
- ✅ Code review completed
- ✅ Secure credential storage
- ✅ HTTPS-only communication
- ✅ Minimal permissions
- ✅ No data leakage

---

## Future Roadmap

### [1.1.0] - Planned
#### Added
- Branch selection for sync
- Repository sync history
- Quick sync widget
- Sync scheduling options

#### Improved
- Better error messages
- Network retry configuration
- UI/UX refinements

### [1.2.0] - Planned
#### Added
- Git LFS support
- Conflict resolution UI
- Batch synchronization
- Firebase Cloud Messaging integration

#### Security
- Certificate pinning
- Biometric authentication option

### [2.0.0] - Future
#### Added
- REST API server
- Webhook receiver
- OAuth authentication flow
- GitHub Actions integration
- Custom remote names
- Multi-account support

#### Breaking Changes
- New API structure
- Updated minimum Android version

---

## Migration Guide

### For New Users
1. Install app from release APK or build from source
2. Generate GitHub Personal Access Token
3. Generate Hugging Face Access Token
4. Configure in app Settings
5. Enter repository URLs and sync

### For Developers
1. Clone repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Run on device or emulator

---

## Deprecation Notices

None for initial release.

---

## Breaking Changes

None for initial release.

---

## Contributors

- TeacherEvan - Initial development and design
- GitHub Copilot - Code assistance and optimization

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## Acknowledgments

- JGit team for excellent Git library
- Android Jetpack team for modern components
- Material Design team for UI guidelines
- GitHub and Hugging Face for API documentation

---

**Note**: This is the initial release (1.0.0). Future updates will be documented here with appropriate version numbers and change descriptions.
