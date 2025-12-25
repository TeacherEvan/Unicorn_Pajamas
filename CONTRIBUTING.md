# Contributing to RepoSync

Thank you for your interest in contributing to RepoSync! This document provides guidelines and instructions for contributing.

## Code of Conduct

Be respectful, inclusive, and considerate in all interactions. We're building this together!

## How to Contribute

### Reporting Bugs

1. Check if the bug has already been reported
2. Use the GitHub issue tracker
3. Provide detailed information:
   - Android version
   - Device model
   - Steps to reproduce
   - Expected vs actual behavior
   - Screenshots if applicable

### Suggesting Features

1. Check existing feature requests
2. Open a new issue with the "enhancement" label
3. Describe:
   - The problem you're trying to solve
   - Your proposed solution
   - Alternative solutions you've considered

### Code Contributions

#### Getting Started

1. Fork the repository
2. Clone your fork:
   ```bash
   git clone https://github.com/YOUR_USERNAME/Unicorn_Pajamas.git
   ```
3. Create a feature branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```

#### Development Setup

1. Open the project in Android Studio
2. Wait for Gradle sync
3. Install required SDKs if prompted
4. Build the project: `./gradlew build`

#### Coding Standards

**Kotlin**
- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable/function names
- Add KDoc comments for public APIs
- Keep functions small and focused

**Python**
- Follow [PEP 8](https://pep8.org/)
- Use type hints
- Add docstrings for functions
- Handle errors gracefully

**XML**
- Use Material Design components
- Follow Android naming conventions
- Keep layouts simple and maintainable

#### Security Guidelines

- **Never** commit API tokens or credentials
- **Never** log sensitive information
- Use EncryptedSharedPreferences for secrets
- Validate all user inputs
- Follow secure coding practices (see SECURITY.md)

#### Testing

Before submitting a PR:

1. **Run unit tests**:
   ```bash
   ./gradlew test
   ```

2. **Run instrumented tests**:
   ```bash
   ./gradlew connectedAndroidTest
   ```

3. **Test manually**:
   - Build and install on a real device/emulator
   - Test your specific changes
   - Test related functionality

4. **Check for lint issues**:
   ```bash
   ./gradlew lint
   ```

#### Commit Messages

Use clear, descriptive commit messages:

```
Add biometric authentication for sync operations

- Implement BiometricAuthHelper class
- Add biometric prompt before sync
- Handle authentication errors gracefully
- Update documentation

Fixes #123
```

Format:
- First line: Brief summary (50 chars or less)
- Blank line
- Detailed description with bullet points
- Reference related issues

#### Pull Request Process

1. **Update your branch**:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```

3. **Create Pull Request**:
   - Use a clear title
   - Describe what you changed and why
   - Reference related issues
   - Add screenshots for UI changes
   - Check all boxes in the PR template

4. **Code Review**:
   - Respond to review comments
   - Make requested changes
   - Push updates to your branch
   - Request re-review when ready

5. **Merge**:
   - Maintainer will merge once approved
   - Your branch will be deleted after merge

## Areas Needing Contribution

### High Priority
- [ ] GitHub token support for private repos
- [ ] Scheduled sync functionality
- [ ] Progress notifications
- [ ] Better error messages

### Medium Priority
- [ ] Custom commit messages
- [ ] Batch sync operations
- [ ] Export/Import configurations
- [ ] Dark mode improvements

### Low Priority
- [ ] Statistics dashboard
- [ ] Sync analytics
- [ ] Multiple account support
- [ ] Cloud backup integration

## Project Structure

```
RepoSync/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/teacherevan/reposync/
│   │   │   │   ├── data/        # Database & models
│   │   │   │   ├── security/    # Auth & encryption
│   │   │   │   ├── ui/          # Activities & adapters
│   │   │   │   ├── workers/     # Background tasks
│   │   │   │   └── utils/       # Helper classes
│   │   │   ├── python/
│   │   │   │   └── repo_sync.py # Sync logic
│   │   │   └── res/             # Resources
│   │   └── androidTest/         # Instrumented tests
│   └── build.gradle.kts
├── ARCHITECTURE.md              # Architecture docs
├── SECURITY.md                  # Security docs
└── README.md
```

## Development Workflow

### For Small Changes

1. Fork and clone
2. Create branch
3. Make changes
4. Test locally
5. Submit PR

### For Large Features

1. Open an issue first to discuss
2. Get feedback from maintainers
3. Create design document if needed
4. Implement in small PRs
5. Get each PR reviewed before next

## Questions?

- Open an issue with the "question" label
- Join discussions in existing issues
- Check documentation first

## Recognition

Contributors will be:
- Listed in README acknowledgments
- Mentioned in release notes
- Appreciated by the community! 🎉

Thank you for contributing to RepoSync!
