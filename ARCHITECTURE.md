# Architecture Overview

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Unicorn Pajamas App                     │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                      Presentation Layer                      │
├─────────────────────────────────────────────────────────────┤
│  MainActivity.kt                                             │
│  ├─ UI Controls (Material Design)                           │
│  ├─ User Input Handling                                     │
│  ├─ Progress Display                                        │
│  └─ Log Viewing                                             │
└─────────────────────────────────────────────────────────────┘
                             │
                             ├─ Observes WorkInfo
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                      Business Layer                          │
├─────────────────────────────────────────────────────────────┤
│  SyncWorker.kt (WorkManager)                                │
│  ├─ Background Task Orchestration                           │
│  ├─ Network-Aware Execution                                 │
│  └─ Guaranteed Delivery                                     │
└─────────────────────────────────────────────────────────────┘
                             │
                             ├─ Uses GitService
                             ├─ Uses SecurePreferences
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                      Service Layer                           │
├─────────────────────────────────────────────────────────────┤
│  GitService.kt                                               │
│  ├─ Clone Repository (JGit)                                 │
│  ├─ Push to Remote (JGit)                                   │
│  └─ Progress Monitoring                                     │
│                                                              │
│  SecurePreferences.kt                                        │
│  ├─ Token Storage (Encrypted)                               │
│  ├─ Token Retrieval                                         │
│  └─ AES256_GCM Encryption                                   │
└─────────────────────────────────────────────────────────────┘
                             │
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                     External Systems                         │
├─────────────────────────────────────────────────────────────┤
│  GitHub.com                    Hugging Face                  │
│  ├─ Source Repository          ├─ Destination Repository    │
│  └─ PAT Authentication         └─ Token Authentication      │
└─────────────────────────────────────────────────────────────┘
```

## Data Flow

### Sync Operation Flow

```
1. User Input
   └─> MainActivity captures GitHub URL, HF URL
   
2. Credential Loading
   └─> SecurePreferences retrieves encrypted tokens
   
3. Work Enqueueing
   └─> WorkManager schedules SyncWorker
   
4. Background Execution
   ├─> Clone from GitHub (GitService)
   │   ├─> Authenticate with GitHub PAT
   │   ├─> Download repository to cache
   │   └─> Progress callback to UI
   │
   └─> Push to Hugging Face (GitService)
       ├─> Authenticate with HF Token
       ├─> Upload repository
       ├─> Progress callback to UI
       └─> Cleanup temporary files
       
5. Completion
   └─> WorkInfo.State.SUCCEEDED
       └─> UI updates with success message
```

## Component Interaction

```
┌──────────────┐     Intent     ┌──────────────┐
│  External    │ ──────────────> │  MainActivity │
│  App/Browser │                 └──────┬───────┘
└──────────────┘                        │
                                        │ enqueue()
                                        ▼
                             ┌──────────────────┐
                             │   WorkManager    │
                             └────────┬─────────┘
                                      │ schedules
                                      ▼
                             ┌──────────────────┐
                             │   SyncWorker     │
                             └────┬──────┬──────┘
                                  │      │
                         clone()  │      │  push()
                                  │      │
                                  ▼      ▼
                             ┌──────────────────┐
                             │   GitService     │
                             └────────┬─────────┘
                                      │
                         ┌────────────┼────────────┐
                         ▼            ▼            ▼
                    ┌────────┐  ┌────────┐  ┌─────────────┐
                    │ GitHub │  │   HF   │  │ Temp Cache  │
                    └────────┘  └────────┘  └─────────────┘
```

## Security Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                         │
│  ┌────────────┐                           ┌──────────────┐  │
│  │ MainActivity│                           │  SyncWorker  │  │
│  └─────┬──────┘                           └──────┬───────┘  │
│        │                                          │          │
│        │ save/get tokens                         │          │
│        ▼                                          ▼          │
│  ┌─────────────────────────────────────────────────────┐   │
│  │           SecurePreferences (Encryption)             │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │  EncryptedSharedPreferences                          │   │
│  │  ├─ AES256_GCM Value Encryption                      │   │
│  │  ├─ AES256_SIV Key Encryption                        │   │
│  │  └─ MasterKey (Android Keystore)                     │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                             │
                             │ HTTPS Only
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                    Network Layer (TLS 1.2+)                  │
│  ┌──────────────────────┐    ┌──────────────────────┐      │
│  │  GitHub API          │    │  Hugging Face API    │      │
│  │  + PAT Token         │    │  + HF Token          │      │
│  └──────────────────────┘    └──────────────────────┘      │
└─────────────────────────────────────────────────────────────┘
```

## Storage Architecture

```
Android Device
├─ App Private Directory (/data/data/com.teacherevan.unicornpajamas/)
│  ├─ shared_prefs/
│  │  └─ encrypted_prefs.xml (Encrypted Tokens)
│  │
│  ├─ cache/ (Temporary Repositories)
│  │  └─ temp_repo_<timestamp>/
│  │     ├─ .git/
│  │     └─ repository files
│  │
│  └─ files/
│     └─ (future use)
│
└─ External Storage (NOT USED - Privacy)
```

## Threading Model

```
Main Thread (UI)
├─ MainActivity UI Updates
├─ User Input Handling
└─ WorkInfo Observation

Background Thread (WorkManager)
├─ SyncWorker.doWork()
│  ├─ GitService.cloneRepository() [IO Dispatcher]
│  └─ GitService.pushToRemote() [IO Dispatcher]
│
└─ Kotlin Coroutines
   └─ Dispatchers.IO for blocking Git operations
```

## Permission Model

```
Manifest Permissions
├─ INTERNET (Required)
│  └─ Network access for Git operations
│
└─ ACCESS_NETWORK_STATE (Required)
   └─ Check network availability before sync

NO Dangerous Permissions
├─ ✗ CAMERA
├─ ✗ LOCATION
├─ ✗ CONTACTS
├─ ✗ STORAGE (API 26+ uses scoped storage)
└─ ✗ SMS
```

## Build Architecture

```
Project Root
├─ app/
│  ├─ build.gradle (Module config)
│  ├─ src/
│  │  ├─ main/
│  │  │  ├─ java/com/teacherevan/unicornpajamas/
│  │  │  │  ├─ MainActivity.kt
│  │  │  │  ├─ GitService.kt
│  │  │  │  ├─ SyncWorker.kt
│  │  │  │  └─ SecurePreferences.kt
│  │  │  ├─ res/ (UI Resources)
│  │  │  └─ AndroidManifest.xml
│  │  ├─ test/ (Unit Tests)
│  │  └─ androidTest/ (Instrumented Tests)
│  └─ proguard-rules.pro
│
├─ gradle/wrapper/ (Build Tool)
├─ build.gradle (Project config)
├─ settings.gradle (Module inclusion)
└─ gradle.properties (Build properties)
```

## Deployment Pipeline

```
Development
    │
    ├─ Local Build
    │  └─ ./gradlew assembleDebug
    │
    ├─ Testing
    │  ├─ ./gradlew test (Unit)
    │  └─ ./gradlew connectedAndroidTest (Integration)
    │
    ├─ Code Review
    │  └─ Static Analysis
    │
    └─ Release Build
       ├─ ./gradlew assembleRelease
       ├─ Sign APK
       └─ Distribute
          ├─ Google Play Store
          └─ Direct APK Distribution
```

## Integration Points

### Inbound
1. **Deep Links** (android.intent.action.VIEW)
   - From web browsers
   - From other Android apps
   - Intent extras with repository URL

2. **Direct Input**
   - Manual URL entry
   - Settings configuration

### Outbound
1. **GitHub API** (via JGit)
   - Clone operations
   - Authentication via PAT

2. **Hugging Face API** (via JGit)
   - Push operations
   - Authentication via Token

3. **Future Integrations**
   - Broadcast receivers
   - Content providers
   - Bound services
   - Firebase Cloud Messaging

## Error Handling Flow

```
User Action
    │
    ├─ Input Validation
    │  └─ Error: Toast + Log
    │
    ├─ Token Verification
    │  └─ Error: Settings Dialog
    │
    ├─ Network Check
    │  └─ Error: WorkManager Retry
    │
    ├─ Clone Operation
    │  ├─ Auth Error → Token Issue
    │  ├─ Network Error → Retry
    │  └─ Git Error → Log + Fail
    │
    └─ Push Operation
       ├─ Auth Error → Token Issue
       ├─ Network Error → Retry
       ├─ Conflict → Force Push
       └─ Git Error → Log + Fail
```

## Key Design Patterns

1. **Repository Pattern**
   - SecurePreferences for data access
   - Abstraction of storage details

2. **Worker Pattern**
   - SyncWorker for background tasks
   - Decoupled from UI lifecycle

3. **Observer Pattern**
   - LiveData for WorkInfo updates
   - UI reacts to state changes

4. **Singleton Pattern**
   - WorkManager instance
   - Application-scoped services

5. **Dependency Injection**
   - Context passed to services
   - Testable components

## Performance Considerations

1. **Memory Management**
   - Temporary files in cache (auto-cleared)
   - No large object retention
   - Proper cleanup after sync

2. **Network Optimization**
   - HTTPS connection reuse
   - Progress streaming
   - Chunked transfers (JGit)

3. **Battery Optimization**
   - WorkManager battery-aware
   - No foreground services
   - Doze mode compatible

4. **Storage Optimization**
   - Cache directory usage
   - No external storage
   - Automatic cleanup
