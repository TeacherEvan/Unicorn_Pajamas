# RepoSync - GitHub to Hugging Face Synchronization App

An Android application that automates the synchronization of Git repositories from GitHub to Hugging Face. This app allows users to easily clone GitHub repositories and push them to Hugging Face as models, datasets, or spaces.

> **Pull to Push** - Bridging GitHub and Hugging Face

## 🎯 Features

- **Automated Synchronization**: Download GitHub repos and push to Hugging Face with a single tap
- **Background Processing**: Uses WorkManager for reliable background sync operations
- **Secure Token Storage**: API tokens encrypted using Android Keystore System
- **Biometric Authentication**: Optional biometric/PIN authentication for enhanced security
- **Sync History**: Track all synchronization operations with status and timestamps
- **Multiple Repository Types**: Support for Hugging Face models, datasets, and spaces
- **Python Integration**: Leverages official `huggingface_hub` library via Chaquopy

## 📋 Requirements

- **Android Device**: Minimum SDK 26 (Android 8.0 Oreo)
- **Internet Connection**: Required for cloning and uploading repositories
- **Hugging Face Account**: Get your token from [https://huggingface.co/settings/tokens](https://huggingface.co/settings/tokens)
- **Storage Space**: Sufficient space for temporary repository storage during sync

## 🏗️ Architecture

### System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         Android Application                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────────┐        ┌──────────────┐      ┌─────────────┐ │
│  │              │        │              │      │             │ │
│  │  UI Layer    │◄──────►│  ViewModel   │◄────►│  WorkManager│ │
│  │  (MainActivity)       │              │      │             │ │
│  │              │        │              │      │  (Background)│ │
│  └──────┬───────┘        └──────┬───────┘      └──────┬──────┘ │
│         │                       │                     │        │
│         │                       │                     │        │
│  ┌──────▼───────────────────────▼─────────────────────▼──────┐ │
│  │                    Data Layer                              │ │
│  │  ┌──────────────┐    ┌──────────────┐   ┌──────────────┐ │ │
│  │  │ Room Database│    │SecureStorage │   │ SyncRecord   │ │ │
│  │  │  (History)   │    │ (Tokens)     │   │   Model      │ │ │
│  │  └──────────────┘    └──────────────┘   └──────────────┘ │ │
│  └────────────────────────────────────────────────────────────┘ │
│                                                                   │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                    Python Layer (Chaquopy)                  │ │
│  │  ┌──────────────┐         ┌──────────────┐                │ │
│  │  │ repo_sync.py │  uses   │huggingface_hub│               │ │
│  │  │              │────────►│   GitPython   │               │ │
│  │  └──────────────┘         └──────────────┘                │ │
│  └────────────────────────────────────────────────────────────┘ │
│                                                                   │
└───────────────────────────┬───────────────────┬──────────────────┘
                            │                   │
                            ▼                   ▼
                    ┌───────────────┐   ┌──────────────┐
                    │    GitHub     │   │ Hugging Face │
                    │   Repository  │   │  Repository  │
                    └───────────────┘   └──────────────┘
```

### Component Breakdown

#### 1. **UI Layer**
- **MainActivity**: Main user interface for input and control
- **SyncHistoryAdapter**: RecyclerView adapter for displaying sync history
- **Layouts**: Material Design-based XML layouts

#### 2. **Data Layer**
- **SyncRecord**: Room entity for storing sync history
- **SyncDao**: Data access object for database operations
- **SyncDatabase**: Room database singleton
- **SecureTokenStorage**: Encrypted SharedPreferences for API tokens

#### 3. **Security Layer**
- **Android Keystore**: Hardware-backed encryption for sensitive data
- **BiometricAuthHelper**: Biometric/PIN authentication support
- **EncryptedSharedPreferences**: Secure storage implementation

#### 4. **Background Processing**
- **WorkManager**: Reliable background task execution
- **RepoSyncWorker**: Worker class for sync operations
- **Network Constraints**: Ensures sync only when connected

#### 5. **Python Integration (Chaquopy)**
- **repo_sync.py**: Core synchronization logic
- **huggingface_hub**: Official HF Python library
- **GitPython**: Git operations support

## 🚀 Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/TeacherEvan/Unicorn_Pajamas.git
cd Unicorn_Pajamas
```

### 2. Open in Android Studio

- Open Android Studio
- Select "Open an Existing Project"
- Navigate to the cloned repository
- Wait for Gradle sync to complete

### 3. Build the Project

```bash
./gradlew build
```

### 4. Install on Device/Emulator

```bash
./gradlew installDebug
```

Or use Android Studio's Run button (▶️)

## 📱 Usage Guide

### First-Time Setup

1. **Launch the App**: Open RepoSync on your Android device
2. **Setup Hugging Face Token**:
   - Tap "Setup Tokens" button
   - Enter your Hugging Face token
   - Token is securely encrypted and stored

### Synchronizing a Repository

1. **Enter GitHub URL**: 
   - Example: `https://github.com/username/repository`
   - Both HTTPS and SSH URLs are supported

2. **Enter Hugging Face Repo ID**:
   - Format: `username/repo-name`
   - Example: `teacherevan/my-model`

3. **Select Repository Type**:
   - **Model**: For machine learning models
   - **Dataset**: For datasets
   - **Space**: For Hugging Face Spaces

4. **Tap "Sync Repository"**:
   - Biometric authentication may be requested
   - Sync runs in background
   - Progress tracked in sync history

### Viewing Sync History

- Scroll down to see all sync operations
- Each entry shows:
  - GitHub URL
  - Hugging Face destination
  - Status (PENDING, IN_PROGRESS, COMPLETED, FAILED)
  - Timestamp

## 🔐 Security Best Practices

### Token Security
- **Never log tokens**: Tokens are never written to Logcat
- **Encrypted storage**: All tokens stored using Android Keystore
- **Biometric protection**: Optional biometric auth for sync operations
- **No plaintext storage**: Tokens never stored in plaintext

### API Token Management
1. Use tokens with minimal required permissions
2. Rotate tokens regularly
3. Revoke tokens if device is lost/stolen
4. Never share tokens in logs or screenshots

### Getting Your Tokens

#### Hugging Face Token
1. Go to [https://huggingface.co/settings/tokens](https://huggingface.co/settings/tokens)
2. Click "New token"
3. Select "Write" permission
4. Copy the token and paste in the app

## 🛠️ Development

### Technology Stack

- **Language**: Kotlin
- **UI Framework**: Material Design Components
- **Database**: Room (SQLite)
- **Background Tasks**: WorkManager
- **Security**: Android Keystore, Biometric API
- **Python Bridge**: Chaquopy
- **Python Libraries**: huggingface_hub, GitPython

### Project Structure

```
app/
├── src/main/
│   ├── java/com/teacherevan/reposync/
│   │   ├── data/              # Data models and database
│   │   ├── security/          # Security and authentication
│   │   ├── ui/                # UI components
│   │   ├── workers/           # Background workers
│   │   └── utils/             # Utility classes
│   ├── python/
│   │   └── repo_sync.py       # Core sync logic
│   └── res/                   # Android resources
│       ├── layout/            # XML layouts
│       └── values/            # Strings, colors, themes
└── build.gradle.kts           # App-level Gradle config
```

### Building from Source

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing config)
./gradlew assembleRelease

# Run tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## 🧪 Testing

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest
```

### Test Coverage

The project includes tests for:
- Database operations
- Token storage security
- Sync worker functionality
- UI components

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 Implementation Phases

### Phase I: Discovery ✅
- [x] Define API limits for GitHub and Hugging Face
- [x] Research OAuth2 implementation
- [x] Design system architecture

### Phase II: Core Logic ✅
- [x] Integrate Chaquopy for Python support
- [x] Implement Python sync script (clone + upload)
- [x] Add WorkManager for background tasks

### Phase III: Security ✅
- [x] Implement Android Keystore integration
- [x] Add biometric authentication
- [x] Secure token storage implementation

### Phase IV: UI/UX ✅
- [x] Create Material Design interface
- [x] Implement input forms for URLs and tokens
- [x] Add sync history view
- [x] Token setup dialog

### Phase V: Documentation ✅
- [x] System architecture diagram
- [x] Comprehensive README
- [x] Setup instructions
- [x] Security best practices

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Hugging Face](https://huggingface.co/) for their excellent API and documentation
- [Chaquopy](https://chaquo.com/chaquopy/) for enabling Python in Android
- Material Design team for UI components
- Android Jetpack team for modern Android development tools

## 📞 Support

For issues, questions, or contributions, please:
- Open an issue on GitHub
- Check existing documentation
- Review the FAQ section below

## ❓ FAQ

### Q: What happens if the sync fails?
A: Failed syncs are logged in the history with error details. You can retry by creating a new sync operation.

### Q: How much storage space is needed?
A: The app temporarily stores the cloned repository. Ensure you have at least 2-3x the repository size available.

### Q: Can I sync private repositories?
A: Yes, if you have appropriate access tokens for both GitHub and Hugging Face.

### Q: Does this work offline?
A: No, an active internet connection is required for both cloning and uploading.

### Q: Is my token safe?
A: Yes, tokens are encrypted using Android Keystore System, which provides hardware-backed security on supported devices.

## 🗺️ Roadmap

- [ ] Add GitHub token support for private repos
- [ ] Implement scheduled syncs
- [ ] Add support for custom commit messages
- [ ] Sync progress notifications
- [ ] Multi-repository batch sync
- [ ] Export/Import sync configurations
- [ ] Dark mode improvements

---

**Built with ❤️ by TeacherEvan**

*Pull to Push - Bridging GitHub and Hugging Face*
