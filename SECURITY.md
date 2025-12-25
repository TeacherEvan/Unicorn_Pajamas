# Security Policy

## Overview

Unicorn Pajamas takes security seriously. This document outlines the security measures implemented in the application and provides guidelines for secure usage.

## Security Features

### 1. Credential Storage

#### Encrypted SharedPreferences
- All access tokens are stored using Android's `EncryptedSharedPreferences`
- Encryption: AES256_GCM for values, AES256_SIV for keys
- Master key stored in Android Keystore System
- Tokens never stored in plain text

#### Best Practices
- Tokens are never logged
- Tokens are masked in UI (shown as `••••••••`)
- Tokens excluded from backups (see `backup_rules.xml`)
- Tokens excluded from device transfer

### 2. Network Security

#### HTTPS Only
```xml
android:usesCleartextTraffic="false"
```
- All network communication uses HTTPS
- HTTP connections are blocked
- TLS 1.2+ required

#### Token Transmission
- Tokens transmitted only via HTTPS
- Used in Authorization headers for Git operations
- Never included in URLs or logs

### 3. Data Protection

#### Backup Exclusion
Sensitive data excluded from cloud backup:
```xml
<exclude domain="sharedpref" path="encrypted_prefs.xml"/>
```

#### App Sandboxing
- All repositories cloned to app's private cache directory
- No external storage access required (API 26+)
- Temporary files deleted after sync

### 4. Permissions

#### Minimal Permissions
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

#### No Dangerous Permissions
- No camera access
- No location access
- No contacts access
- No SMS access

## Token Security

### GitHub Personal Access Token (PAT)

#### Minimum Required Scopes
For public repositories:
- `public_repo` - Access public repositories

For private repositories:
- `repo` - Full control of private repositories

#### Creation Guidelines
1. Use fine-grained tokens when possible
2. Set expiration date (recommended: 90 days)
3. Limit scope to minimum required
4. Use separate tokens for different apps
5. Regularly rotate tokens

#### Revocation
If token is compromised:
1. Immediately revoke at https://github.com/settings/tokens
2. Clear app data or reinstall app
3. Generate new token
4. Update credentials in app

### Hugging Face Token

#### Minimum Required Permissions
- `write` - Write access to repositories

#### Creation Guidelines
1. Create token at https://huggingface.co/settings/tokens
2. Set descriptive name (e.g., "Unicorn Pajamas Android")
3. Select only required permissions
4. Consider token expiration

#### Revocation
If token is compromised:
1. Revoke at https://huggingface.co/settings/tokens
2. Clear app credentials
3. Generate new token

## Vulnerability Reporting

### Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |

### Reporting a Vulnerability

If you discover a security vulnerability:

1. **DO NOT** open a public GitHub issue
2. Email the maintainer directly (check GitHub profile)
3. Include:
   - Description of vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if any)

Expected response time: 48 hours

### Responsible Disclosure

We follow responsible disclosure principles:
- Acknowledge receipt within 48 hours
- Provide regular updates on fix progress
- Credit researcher (if desired) in release notes
- Coordinate public disclosure timing

## Secure Usage Guidelines

### For Users

1. **Token Management**
   - Never share your tokens
   - Don't commit tokens to repositories
   - Use different tokens for different purposes
   - Enable token expiration
   - Regularly audit token access

2. **App Security**
   - Download app only from official sources
   - Keep app updated
   - Use device lock screen
   - Enable encryption on device
   - Review app permissions

3. **Network Security**
   - Use trusted networks
   - Avoid public WiFi for sensitive operations
   - Consider VPN for additional security
   - Verify repository URLs before syncing

4. **Data Privacy**
   - Review repository contents before syncing
   - Be aware of what you're pushing to public repositories
   - Check for sensitive data in commits
   - Use `.gitignore` to exclude sensitive files

### For Developers

1. **Code Security**
   - Never hardcode credentials
   - Use ProGuard/R8 for code obfuscation
   - Keep dependencies updated
   - Follow Android security best practices

2. **Token Handling**
   ```kotlin
   // GOOD: Using EncryptedSharedPreferences
   securePreferences.saveGitHubToken(token)
   
   // BAD: Plain SharedPreferences
   // sharedPreferences.edit().putString("token", token).apply()
   
   // BAD: Logging tokens
   // Log.d("Token", token)
   ```

3. **Network Requests**
   ```kotlin
   // GOOD: HTTPS only
   val url = "https://github.com/user/repo.git"
   
   // BAD: HTTP
   // val url = "http://github.com/user/repo.git"
   ```

## Threat Model

### Identified Threats

1. **Token Theft**
   - Mitigation: Encrypted storage, no logging
   - Residual risk: Low (requires root access)

2. **Man-in-the-Middle (MITM)**
   - Mitigation: HTTPS only, certificate validation
   - Residual risk: Low (requires CA compromise)

3. **Malicious Repository Content**
   - Mitigation: User responsibility to verify sources
   - Residual risk: Medium (inherent to Git operations)

4. **Unauthorized Access**
   - Mitigation: Device lock screen, app sandboxing
   - Residual risk: Low (requires physical device access)

5. **Data Leakage via Logs**
   - Mitigation: No sensitive data in logs
   - Residual risk: Very Low

### Out of Scope

- Physical device theft (user responsibility)
- Malware on device (OS/antivirus responsibility)
- Compromised GitHub/Hugging Face servers (provider responsibility)
- Social engineering attacks (user awareness)

## Security Checklist

### Pre-Release
- [ ] No hardcoded credentials
- [ ] All dependencies up to date
- [ ] ProGuard/R8 rules configured
- [ ] Network security config verified
- [ ] Backup rules exclude sensitive data
- [ ] Permissions minimized
- [ ] Code obfuscation enabled
- [ ] Security scanning completed

### Post-Release
- [ ] Monitor for security advisories
- [ ] Update dependencies regularly
- [ ] Review and respond to vulnerability reports
- [ ] Maintain security documentation
- [ ] Issue security patches promptly

## Dependencies Security

### Regular Updates

Keep dependencies updated to patch vulnerabilities:

```bash
# Check for dependency updates
./gradlew dependencyUpdates

# Update Gradle wrapper
./gradlew wrapper --gradle-version latest
```

### Known Dependencies

- **JGit**: Git operations library
  - Monitor: https://github.com/eclipse/jgit/security
- **AndroidX Security**: Encryption library
  - Monitor: https://developer.android.com/jetpack/androidx/releases/security
- **OkHttp**: Network library
  - Monitor: https://github.com/square/okhttp/security

## Compliance

### Data Protection

- **GDPR**: Minimal data collection, user control over data
- **CCPA**: No sale of personal information
- **COPPA**: No intentional collection from children under 13

### Open Source License

- MIT License (see LICENSE file)
- No warranty provided
- Use at your own risk

## Security Contacts

- GitHub Issues: https://github.com/TeacherEvan/Unicorn_Pajamas/issues
- Security Email: (Check repository owner's GitHub profile)

## Security Updates

Security updates will be released as:
- Patch versions (1.0.x) for minor issues
- Minor versions (1.x.0) for moderate issues
- Documented in CHANGELOG.md
- Announced in GitHub Releases

## Additional Resources

- [Android Security Best Practices](https://developer.android.com/topic/security/best-practices)
- [OWASP Mobile Security](https://owasp.org/www-project-mobile-security/)
- [GitHub Security Best Practices](https://docs.github.com/en/code-security)
- [Hugging Face Security](https://huggingface.co/docs/hub/security)

---

**Last Updated**: 2025-12-25  
**Version**: 1.0
