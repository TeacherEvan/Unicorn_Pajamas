# Quick Start Guide

Get Unicorn Pajamas up and running in 5 minutes!

## Prerequisites

- [ ] Android Studio installed
- [ ] Android device or emulator (Android 8.0+)
- [ ] GitHub account with Personal Access Token
- [ ] Hugging Face account with Access Token

## Step 1: Get Your Tokens

### GitHub Personal Access Token

1. Go to https://github.com/settings/tokens
2. Click "Generate new token (classic)"
3. Give it a name: "Unicorn Pajamas"
4. Select scopes:
   - ✅ `repo` (for private repos) OR
   - ✅ `public_repo` (for public repos only)
5. Click "Generate token"
6. **Copy and save the token** (you won't see it again!)

Example: `ghp_1234567890abcdefghijklmnopqrstuvwxyz`

### Hugging Face Access Token

1. Go to https://huggingface.co/settings/tokens
2. Click "New token"
3. Name: "Unicorn Pajamas"
4. Role: **Write**
5. Click "Generate"
6. **Copy and save the token**

Example: `hf_1234567890abcdefghijklmnopqrstuvwxyz`

## Step 2: Build the App

### Option A: Using Android Studio (Recommended)

```bash
# Clone the repository
git clone https://github.com/TeacherEvan/Unicorn_Pajamas.git
cd Unicorn_Pajamas

# Open in Android Studio
# File → Open → Select Unicorn_Pajamas folder

# Wait for Gradle sync to complete

# Click Run button (green triangle) or press Shift+F10
```

### Option B: Using Command Line

```bash
# Clone the repository
git clone https://github.com/TeacherEvan/Unicorn_Pajamas.git
cd Unicorn_Pajamas

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Or install manually
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Step 3: Configure the App

1. **Launch Unicorn Pajamas** on your device
2. **Tap the Settings icon** (⚙️) in the top-right corner
3. **Enter your GitHub token** in the first field
4. **Enter your Hugging Face token** in the second field
5. **Tap "Save Credentials"**
6. You should see "Credentials saved securely" message

## Step 4: Sync Your First Repository

### Example: Sync a Small Test Repository

1. **GitHub URL**: Enter `https://github.com/huggingface/transformers.git`
2. **Hugging Face URL**: Enter `https://huggingface.co/YOUR_USERNAME/transformers`
   - Replace `YOUR_USERNAME` with your actual Hugging Face username
3. **Tap "Sync Repository"**
4. **Watch the progress** in the log view at the bottom
5. **Wait for completion** (may take a few minutes depending on repository size)

### Success!

When you see "✓ Sync completed successfully!" in the logs:
- Go to `https://huggingface.co/YOUR_USERNAME/transformers`
- Your repository should now be there!

## Troubleshooting

### "Please configure your access tokens in Settings"
- Make sure you've entered both tokens in Settings
- Tap the Settings icon and re-enter your tokens

### "Auth failed" or "Authentication failed"
- Check that your tokens are correct
- Verify tokens haven't expired
- Ensure tokens have the right permissions

### "Invalid URL"
- Make sure URLs are in the correct format
- GitHub: `https://github.com/username/repo.git`
- Hugging Face: `https://huggingface.co/username/repo`

### "Network error"
- Check your internet connection
- Try using WiFi instead of cellular
- Ensure you're not behind a restrictive firewall

### Sync takes too long
- Large repositories can take several minutes
- Check the logs for progress updates
- Ensure stable network connection

## What's Next?

### Sync Your Own Repository

1. Create a repository on Hugging Face
2. Enter your GitHub repo URL
3. Enter your new Hugging Face repo URL
4. Tap Sync!

### Automate with Deep Links

Send repository URLs to the app from other apps:

```kotlin
val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/user/repo.git"))
startActivity(intent)
```

### Learn More

- **Full Documentation**: See [README.md](README.md)
- **API Integration**: See [API_INTEGRATION.md](API_INTEGRATION.md)
- **Build Guide**: See [BUILD.md](BUILD.md)
- **Security Info**: See [SECURITY.md](SECURITY.md)
- **Examples**: See [EXAMPLES.md](EXAMPLES.md)

## Quick Reference

### Supported URL Formats

**GitHub:**
```
https://github.com/username/repo.git  ✅ Recommended
https://github.com/username/repo      ✅ Works
git@github.com:username/repo.git      ✅ Works (if keys configured)
```

**Hugging Face:**
```
https://huggingface.co/username/repo                    ✅ Models
https://huggingface.co/datasets/username/dataset        ✅ Datasets
https://huggingface.co/spaces/username/space            ✅ Spaces
```

### Token Permissions

**GitHub Token Scopes:**
- `public_repo` - For public repositories
- `repo` - For private repositories (full access)

**Hugging Face Token:**
- `write` - Required for pushing repositories

### App Features

✓ One-tap sync  
✓ Real-time progress  
✓ Secure token storage  
✓ Deep link support  
✓ Background processing  
✓ Detailed logging  
✓ Material Design UI  

## Need Help?

1. Check the [Troubleshooting](#troubleshooting) section above
2. Review [EXAMPLES.md](EXAMPLES.md) for detailed examples
3. Read [README.md](README.md) for comprehensive documentation
4. Open an issue on GitHub

## Security Reminder

🔒 **Your tokens are stored securely**
- Encrypted with AES256_GCM
- Never logged or exposed
- Excluded from backups
- Only accessible by the app

⚠️ **Never share your tokens**
- Treat them like passwords
- Don't commit them to repositories
- Rotate them regularly (every 90 days recommended)

---

**Congratulations!** You're now ready to sync repositories between GitHub and Hugging Face! 🎉

For more advanced usage, check out the other documentation files in this repository.
