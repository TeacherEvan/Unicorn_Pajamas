# Architecture Documentation

## System Overview

RepoSync is an Android application designed to automate the synchronization of Git repositories from GitHub to Hugging Face. The application follows modern Android development best practices with a clean architecture approach.

## Core Components

### 1. Presentation Layer

#### MainActivity
- Main entry point for the application
- Handles user input and displays sync history
- Manages navigation and user interactions
- Observes data from the data layer using LiveData/Flow

#### SyncHistoryAdapter
- RecyclerView adapter for displaying sync operations
- Uses DiffUtil for efficient list updates
- Implements ViewHolder pattern

### 2. Domain Layer

#### Models
- **SyncRecord**: Represents a single sync operation
  - Contains GitHub URL, HF repo ID, status, timestamps
  - Uses Room annotations for persistence
  - Implements data validation

#### Repository Pattern
- **SyncDatabase**: Single source of truth for sync history
- **SyncDao**: Data access interface

### 3. Data Layer

#### Local Storage
- **Room Database**: SQLite-based persistence for sync history
- **EncryptedSharedPreferences**: Secure storage for API tokens
- **Android Keystore**: Hardware-backed encryption

#### Security
- **SecureTokenStorage**: Manages encrypted token storage
- **BiometricAuthHelper**: Handles biometric authentication

### 4. Background Processing

#### WorkManager Integration
- **RepoSyncWorker**: Executes sync operations in background
- Supports constraints (network connectivity)
- Provides work status updates
- Handles retries and failures

### 5. Python Integration Layer

#### Chaquopy Bridge
- Enables running Python code within Android app
- Provides access to Python ecosystem (huggingface_hub, GitPython)
- Manages Python interpreter lifecycle

#### Python Module (repo_sync.py)
- Implements core sync logic
- Uses `huggingface_hub` for HF API interactions
- Uses `GitPython` or subprocess for Git operations
- Handles error scenarios and cleanup

## Data Flow

### Sync Operation Flow

```
User Input → MainActivity → Validation → BiometricAuth
                                              ↓
                                    Create SyncRecord
                                              ↓
                                    Save to Database
                                              ↓
                              Create WorkManager Request
                                              ↓
                              RepoSyncWorker.doWork()
                                              ↓
                            Initialize Python Interpreter
                                              ↓
                              Call repo_sync.sync_repos()
                                              ↓
                            Clone GitHub Repository
                                              ↓
                          Upload to Hugging Face
                                              ↓
                                    Cleanup Temp Files
                                              ↓
                              Update SyncRecord Status
                                              ↓
                                  Notify UI via LiveData
```

## Security Architecture

### Token Security

1. **Storage**: Tokens stored using EncryptedSharedPreferences
2. **Encryption**: AES256_GCM encryption scheme
3. **Key Management**: Android Keystore System
4. **Access Control**: Biometric authentication before sync

### Security Layers

```
┌─────────────────────────────────────────┐
│         User Authentication             │
│    (Biometric / Device Credential)      │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│      Application Security Layer         │
│  - Input Validation                     │
│  - HTTPS Only                           │
│  - No Token Logging                     │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│       Storage Security Layer            │
│  - EncryptedSharedPreferences           │
│  - AES256_GCM Encryption                │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│       Android Keystore System           │
│  - Hardware-Backed Keys                 │
│  - Tamper-Resistant                     │
└─────────────────────────────────────────┘
```

## Threading Model

### Main Thread
- UI rendering and user interactions
- LiveData/Flow observations
- Navigation

### Background Threads
- **WorkManager Thread**: Executes RepoSyncWorker
- **Room Database**: All database operations run on IO dispatcher
- **Python Execution**: Runs in WorkManager's background thread

### Coroutines Usage
```kotlin
lifecycleScope.launch {
    // Runs on Main dispatcher by default
    withContext(Dispatchers.IO) {
        // Database operations
    }
    // Back to Main for UI updates
}
```

## Dependencies

### Android Dependencies
- **AndroidX Core**: Core Android components
- **Material Components**: UI components
- **Room**: Database ORM
- **WorkManager**: Background tasks
- **Biometric**: Authentication
- **Security Crypto**: Encrypted storage

### Python Dependencies
- **huggingface_hub**: HF API client
- **GitPython**: Git operations

## Build Configuration

### Gradle Structure
- **Project-level**: Plugin versions and repositories
- **App-level**: Dependencies and build variants
- **Chaquopy Configuration**: Python integration settings

### Build Variants
- **Debug**: Development builds with debugging enabled
- **Release**: Production builds with ProGuard (future)

## Error Handling

### Strategy
1. **Input Validation**: Validate before processing
2. **Try-Catch**: Wrap risky operations
3. **User Feedback**: Display meaningful error messages
4. **Logging**: Log errors for debugging (no sensitive data)
5. **Database Recording**: Store error details in SyncRecord

### Error Types
- **Network Errors**: GitHub/HF connectivity issues
- **Authentication Errors**: Invalid tokens
- **Storage Errors**: Insufficient space
- **Python Errors**: Script execution failures

## Testing Strategy

### Unit Tests
- Data models
- Database operations
- Token storage
- Validation logic

### Integration Tests
- WorkManager execution
- Database migrations
- Python bridge functionality

### UI Tests
- User flows
- Form validation
- History display

## Performance Considerations

### Optimization Techniques
1. **Lazy Loading**: Load sync history on demand
2. **Pagination**: Limit database queries
3. **Background Processing**: All heavy operations in WorkManager
4. **Temporary Storage**: Clean up after sync
5. **Constraint-Based Execution**: Only sync when conditions met

### Memory Management
- Release Python interpreter when not needed
- Clean temporary files immediately after sync
- Use weak references where appropriate

## Future Enhancements

1. **Scheduled Syncs**: Periodic automatic synchronization
2. **Notifications**: Progress and completion notifications
3. **Batch Operations**: Sync multiple repositories
4. **Configuration Export**: Backup/restore sync configurations
5. **Advanced Filtering**: Filter sync history by various criteria
