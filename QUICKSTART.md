# Quick Start Guide

Get RepoSync up and running in 5 minutes!

## 📱 For Users

### Step 1: Install the App

**Option A: Download from GitHub Releases**
1. Go to [Releases](https://github.com/TeacherEvan/Unicorn_Pajamas/releases)
2. Download the latest `.apk` file
3. Install on your Android device (enable "Install from Unknown Sources")

**Option B: Build from Source**
```bash
git clone https://github.com/TeacherEvan/Unicorn_Pajamas.git
cd Unicorn_Pajamas
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Step 2: Get Your Hugging Face Token

1. Visit [https://huggingface.co/settings/tokens](https://huggingface.co/settings/tokens)
2. Click "New token"
3. Name it "RepoSync"
4. Select "Write" permission
5. Copy the token (keep it safe!)

### Step 3: Configure the App

1. Launch RepoSync
2. Tap "Setup Tokens"
3. Paste your Hugging Face token
4. Tap "Save"

### Step 4: Sync Your First Repository

1. **Enter GitHub URL**:
   ```
   https://github.com/username/repository
   ```

2. **Enter Hugging Face Repo ID**:
   ```
   your-username/your-repo-name
   ```

3. **Select Repository Type**:
   - Model (for ML models)
   - Dataset (for datasets)
   - Space (for HF Spaces)

4. **Tap "Sync Repository"**

5. **Authenticate** (if biometric is enabled)

6. **Wait for completion** - check sync history

### Step 5: Monitor Progress

- View sync history at the bottom
- Green = Completed
- Red = Failed
- Blue = In Progress
- Gray = Pending

## 💻 For Developers

### Quick Build

```bash
# Clone
git clone https://github.com/TeacherEvan/Unicorn_Pajamas.git
cd Unicorn_Pajamas

# Build
./gradlew build

# Run tests
./gradlew test

# Install on device
./gradlew installDebug
```

### Quick Dev Setup

1. **Open in Android Studio**
   - File → Open → Select project directory
   - Wait for Gradle sync

2. **Configure Device**
   - Connect Android device via USB
   - Enable USB Debugging
   - Or use Android Emulator (API 26+)

3. **Run**
   - Click ▶️ Run button
   - Select device
   - Wait for installation

4. **Test**
   - Enter test data
   - Check logcat for errors

### Project Structure Overview

```
app/src/main/
├── java/com/teacherevan/reposync/
│   ├── ui/                 # User Interface
│   │   ├── MainActivity.kt
│   │   └── SyncHistoryAdapter.kt
│   ├── data/              # Database & Models
│   │   ├── SyncRecord.kt
│   │   ├── SyncDao.kt
│   │   └── SyncDatabase.kt
│   ├── security/          # Security
│   │   ├── SecureTokenStorage.kt
│   │   └── BiometricAuthHelper.kt
│   └── workers/           # Background Tasks
│       └── RepoSyncWorker.kt
├── python/
│   └── repo_sync.py       # Core Sync Logic
└── res/                   # Resources
    ├── layout/
    ├── values/
    └── mipmap/
```

## 🔧 Configuration

### Supported Repository Types

| Type | Description | Example Use Case |
|------|-------------|------------------|
| **model** | ML models | PyTorch, TensorFlow models |
| **dataset** | Datasets | Training data, CSVs |
| **space** | HF Spaces | Gradio/Streamlit apps |

### System Requirements

- **Android**: 8.0 (API 26) or higher
- **Storage**: 500MB+ free space
- **Network**: WiFi or mobile data
- **Biometric**: Optional (fingerprint/face)

## 📊 Example Workflows

### Workflow 1: Sync ML Model

```
GitHub Repo: https://github.com/user/my-pytorch-model
HF Repo ID: myusername/pytorch-model
Type: model

Result: https://huggingface.co/myusername/pytorch-model
```

### Workflow 2: Sync Dataset

```
GitHub Repo: https://github.com/user/training-data
HF Repo ID: myusername/my-dataset
Type: dataset

Result: https://huggingface.co/datasets/myusername/my-dataset
```

### Workflow 3: Sync HF Space

```
GitHub Repo: https://github.com/user/gradio-app
HF Repo ID: myusername/demo-app
Type: space

Result: https://huggingface.co/spaces/myusername/demo-app
```

## 🐛 Troubleshooting

### Problem: "Missing HF Token"
**Solution**: Tap "Setup Tokens" and enter your token

### Problem: "Invalid GitHub URL"
**Solution**: Use full URL: `https://github.com/user/repo`

### Problem: "Sync Failed"
**Solutions**:
- Check internet connection
- Verify token has write permission
- Check HF repo ID format: `username/repo-name`
- Ensure sufficient storage space

### Problem: "Authentication Failed"
**Solution**: 
- Re-enter token in Settings
- Verify token is valid on HuggingFace.co

### Problem: App Crashes
**Solutions**:
- Clear app data
- Reinstall app
- Check Android version (8.0+)
- Report bug with logs

## 💡 Tips

1. **Use Small Repos First**: Test with small repositories
2. **Check Network**: Use WiFi for large repos
3. **Monitor Storage**: Ensure adequate free space
4. **Secure Tokens**: Use biometric lock
5. **Update Regularly**: Keep app up to date

## 📚 Next Steps

- Read [README.md](README.md) for full documentation
- Check [ARCHITECTURE.md](ARCHITECTURE.md) for technical details
- Review [SECURITY.md](SECURITY.md) for security info
- See [CONTRIBUTING.md](CONTRIBUTING.md) to contribute

## 🆘 Need Help?

- 📖 Check the [FAQ](README.md#-faq)
- 🐛 [Report a bug](https://github.com/TeacherEvan/Unicorn_Pajamas/issues)
- 💬 [Ask a question](https://github.com/TeacherEvan/Unicorn_Pajamas/discussions)
- 📧 Contact: [Create an issue]

---

**You're ready to sync! 🚀**

*Happy syncing from GitHub to Hugging Face!*
