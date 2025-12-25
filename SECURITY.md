# Security Documentation

## Overview

RepoSync implements multiple layers of security to protect user credentials and ensure safe operation. This document outlines the security measures and best practices implemented in the application.

## Security Principles

1. **Defense in Depth**: Multiple security layers
2. **Least Privilege**: Minimal permissions required
3. **Secure by Default**: Security features enabled automatically
4. **No Sensitive Logging**: Never log tokens or credentials
5. **Encryption at Rest**: All sensitive data encrypted

## Token Security

### Storage Implementation

#### Android Keystore System
- Hardware-backed key storage on supported devices
- Keys never leave the secure hardware
- Protection against extraction and tampering
- Automatic key generation and rotation support

#### EncryptedSharedPreferences
```kotlin
EncryptedSharedPreferences.create(
    context,
    "secure_tokens",
    masterKey,
    PrefKeyEncryptionScheme.AES256_SIV,  // Key encryption
    PrefValueEncryptionScheme.AES256_GCM  // Value encryption
)
```

### Encryption Schemes

#### AES256_SIV (Keys)
- Deterministic encryption for preference keys
- Prevents key enumeration attacks

#### AES256_GCM (Values)
- Authenticated encryption for token values
- Provides both confidentiality and integrity

### Token Lifecycle

1. **Creation**: User enters token in secure dialog
2. **Storage**: Encrypted and stored using Keystore
3. **Retrieval**: Decrypted only when needed
4. **Usage**: Passed directly to Python, never logged
5. **Deletion**: Secure erasure on demand

## Biometric Authentication

### Implementation

```kotlin
BiometricPrompt.PromptInfo.Builder()
    .setTitle("Authenticate Sync")
    .setSubtitle("Verify to start synchronization")
    .setAllowedAuthenticators(
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
        BiometricManager.Authenticators.DEVICE_CREDENTIAL
    )
    .build()
```

### Authentication Types Supported

1. **Biometric Strong**: Fingerprint, Face, Iris
2. **Device Credential**: PIN, Pattern, Password

### Use Cases

- Accessing sync functionality
- Viewing stored tokens
- Modifying settings (future)

## Network Security

### HTTPS Enforcement

```xml
android:usesCleartextTraffic="false"
```

- All network traffic must use HTTPS
- Prevents man-in-the-middle attacks
- Enforced at manifest level

### Certificate Validation

- Android's default certificate validation
- Pinning not implemented (allows certificate rotation)

## Input Validation

### GitHub URL Validation

```python
def validate_github_url(url):
    valid_patterns = [
        "github.com/",
        "https://github.com/",
        "http://github.com/",
        "git@github.com:"
    ]
    return any(pattern in url for pattern in valid_patterns)
```

### Hugging Face Repo ID Validation

```python
def validate_hf_repo_id(repo_id):
    if not repo_id or "/" not in repo_id:
        return False
    parts = repo_id.split("/")
    return len(parts) == 2 and all(part.strip() for part in parts)
```

### Input Sanitization

- All user inputs trimmed
- Special characters handled properly
- No direct shell execution with user input

## Secure Coding Practices

### No Logging of Sensitive Data

```kotlin
// ❌ NEVER do this
Log.d(TAG, "Token: $token")

// ✅ Safe logging
Log.d(TAG, "Token validation successful")
```

### Token Handling

```kotlin
// ✅ Correct: Direct usage without logging
val token = tokenStorage.getHuggingFaceToken()
performSync(token)

// ❌ NEVER expose in stack traces
try {
    performSync(token)
} catch (e: Exception) {
    // Don't include token in error message
    Log.e(TAG, "Sync failed: ${e.message}")
}
```

## Python Security

### Subprocess Security

```python
# ✅ Safe: No shell=True
subprocess.run(
    ["git", "clone", "--depth", "1", github_url, local_path],
    capture_output=True,
    text=True,
    timeout=300
)

# ❌ NEVER use shell=True with user input
# subprocess.run(f"git clone {github_url}", shell=True)  # DANGEROUS!
```

### Timeout Protection

```python
subprocess.run(
    [...],
    timeout=300  # Prevent hanging operations
)
```

## Permission Management

### Declared Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.USE_BIOMETRIC" />
```

### Runtime Permissions

- Biometric: Requested at runtime
- Storage: Scoped storage (no permission needed for app directory)
- Network: Normal permission (granted at install)

## Data Privacy

### What We Store

1. **Sync History**: URLs, repo IDs, timestamps, status
2. **API Tokens**: Encrypted HF token
3. **App Settings**: Preferences (future)

### What We Don't Store

1. Repository contents (deleted after sync)
2. GitHub passwords
3. Personal information
4. Usage analytics

## Threat Model

### Threats Addressed

1. **Token Theft**
   - Mitigation: Encrypted storage, biometric auth

2. **Man-in-the-Middle Attacks**
   - Mitigation: HTTPS enforcement

3. **Unauthorized Access**
   - Mitigation: Biometric authentication

4. **Data Extraction**
   - Mitigation: Android Keystore, encrypted storage

5. **Code Injection**
   - Mitigation: Input validation, no shell execution

### Residual Risks

1. **Rooted Devices**: Security compromised
2. **Malware**: Can potentially intercept data
3. **Physical Access**: Device unlock bypasses app security
4. **Cloud Backup**: Encrypted data may sync (consider excluding)

## Security Best Practices for Users

### Token Management

1. **Generate Tokens with Minimal Permissions**
   - Only grant "Write" access if needed
   - Use repository-scoped tokens when possible

2. **Rotate Tokens Regularly**
   - Change tokens every 90 days
   - Revoke old tokens immediately

3. **Revoke if Compromised**
   - If device is lost: revoke all tokens
   - Generate new tokens after regaining access

### Device Security

1. **Enable Screen Lock**
   - Biometric authentication requires device security
   - Use strong PIN/password

2. **Keep OS Updated**
   - Security patches important for Keystore
   - Update Android regularly

3. **Avoid Rooted Devices**
   - Rooting compromises security features
   - Keystore protection may be bypassed

## Compliance Considerations

### GDPR Compliance

- **Data Minimization**: Only store necessary data
- **Right to Erasure**: Token deletion supported
- **Data Portability**: Export capabilities (future)
- **Transparency**: Clear documentation

### Security Auditing

Future considerations:
- Penetration testing
- Code security analysis
- Third-party security audit
- Vulnerability disclosure program

## Security Checklist for Developers

- [ ] Never log tokens or sensitive data
- [ ] Always use EncryptedSharedPreferences for secrets
- [ ] Validate all user inputs
- [ ] Use HTTPS for all network calls
- [ ] Implement timeout for long operations
- [ ] Handle errors without exposing sensitive info
- [ ] Test biometric authentication edge cases
- [ ] Review ProGuard rules for sensitive code
- [ ] Check for hardcoded credentials
- [ ] Verify third-party library security

## Reporting Security Issues

If you discover a security vulnerability:

1. **Do NOT** open a public issue
2. Email: [security contact needed]
3. Include:
   - Description of vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if any)

## Security Update Policy

- Critical vulnerabilities: Patch within 48 hours
- High severity: Patch within 1 week
- Medium severity: Patch in next release
- Low severity: Consider for future releases

## References

- [Android Security Best Practices](https://developer.android.com/topic/security/best-practices)
- [Android Keystore System](https://developer.android.com/training/articles/keystore)
- [Biometric Authentication](https://developer.android.com/training/sign-in/biometric-auth)
- [EncryptedSharedPreferences](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences)
