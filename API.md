# API & Integration Documentation

## Python Module API

### repo_sync.py

The core Python module that handles repository synchronization.

#### sync_repos()

Main function for synchronizing repositories.

```python
def sync_repos(
    github_url: str,
    hf_repo_id: str,
    hf_token: str,
    repo_type: str = "model"
) -> dict
```

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `github_url` | `str` | Yes | GitHub repository URL (HTTPS or SSH) |
| `hf_repo_id` | `str` | Yes | Hugging Face repo ID (format: `username/repo-name`) |
| `hf_token` | `str` | Yes | Hugging Face write-access token |
| `repo_type` | `str` | No | Repository type: "model", "dataset", or "space" (default: "model") |

**Returns:**

```python
{
    "status": "success",  # or "error"
    "message": "Successfully synced...",
    "github_url": "https://github.com/...",
    "hf_repo_id": "username/repo-name",
    "repo_type": "model"
}
```

**Raises:**

- `RepoSyncError`: If any step fails (clone, upload, validation)

**Example:**

```python
from repo_sync import sync_repos

result = sync_repos(
    github_url="https://github.com/username/my-model",
    hf_repo_id="hfuser/my-model",
    hf_token="hf_...",
    repo_type="model"
)

if result["status"] == "success":
    print(f"Synced to: {result['hf_repo_id']}")
```

#### validate_github_url()

Validates a GitHub repository URL.

```python
def validate_github_url(url: str) -> bool
```

**Example:**

```python
is_valid = validate_github_url("https://github.com/user/repo")  # True
is_valid = validate_github_url("https://gitlab.com/user/repo")  # False
```

#### validate_hf_repo_id()

Validates a Hugging Face repository ID.

```python
def validate_hf_repo_id(repo_id: str) -> bool
```

**Example:**

```python
is_valid = validate_hf_repo_id("username/repo-name")  # True
is_valid = validate_hf_repo_id("invalid")  # False
```

## Android Kotlin API

### SecureTokenStorage

Secure storage for API tokens using Android Keystore.

#### Methods

```kotlin
class SecureTokenStorage(context: Context) {
    
    fun saveHuggingFaceToken(token: String)
    fun getHuggingFaceToken(): String?
    fun hasHuggingFaceToken(): Boolean
    fun clearHuggingFaceToken()
    fun clearAllTokens()
    
    companion object {
        fun getInstance(context: Context): SecureTokenStorage
    }
}
```

**Example:**

```kotlin
val storage = SecureTokenStorage.getInstance(context)

// Save token
storage.saveHuggingFaceToken("hf_...")

// Retrieve token
val token = storage.getHuggingFaceToken()

// Check if token exists
if (storage.hasHuggingFaceToken()) {
    // Token is available
}

// Clear token
storage.clearHuggingFaceToken()
```

### BiometricAuthHelper

Helper for biometric authentication.

#### authenticate()

```kotlin
fun authenticate(
    title: String = "Biometric Authentication",
    subtitle: String = "Authenticate to access secure tokens",
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
    onFailed: () -> Unit
)
```

**Example:**

```kotlin
val biometricHelper = BiometricAuthHelper(activity)

biometricHelper.authenticate(
    title = "Sync Repository",
    subtitle = "Verify your identity",
    onSuccess = {
        // User authenticated successfully
        performSync()
    },
    onError = { error ->
        // Authentication error
        showError(error)
    },
    onFailed = {
        // Authentication failed
        showError("Authentication failed")
    }
)
```

#### canAuthenticate()

```kotlin
companion object {
    fun canAuthenticate(context: Context): Boolean
}
```

**Example:**

```kotlin
if (BiometricAuthHelper.canAuthenticate(context)) {
    // Device supports biometric authentication
}
```

### SyncDatabase

Room database for sync history.

#### DAO Methods

```kotlin
interface SyncDao {
    suspend fun insertSync(record: SyncRecord): Long
    suspend fun updateSync(record: SyncRecord)
    fun getAllSyncs(): Flow<List<SyncRecord>>
    suspend fun getSyncById(id: Long): SyncRecord?
    fun getSyncsByStatus(status: SyncStatus): Flow<List<SyncRecord>>
    suspend fun deleteSync(id: Long)
    suspend fun deleteAllSyncs()
}
```

**Example:**

```kotlin
val database = SyncDatabase.getDatabase(context)
val dao = database.syncDao()

// Insert new sync record
val record = SyncRecord(
    githubUrl = "https://github.com/user/repo",
    hfRepoId = "user/model",
    repoType = "model",
    status = SyncStatus.PENDING
)
val id = dao.insertSync(record)

// Observe all syncs
lifecycleScope.launch {
    dao.getAllSyncs().collect { syncs ->
        updateUI(syncs)
    }
}

// Update sync status
val updated = record.copy(status = SyncStatus.COMPLETED)
dao.updateSync(updated)
```

### RepoSyncWorker

Background worker for repository synchronization.

#### Input Data

```kotlin
val inputData = Data.Builder()
    .putString(RepoSyncWorker.KEY_GITHUB_URL, githubUrl)
    .putString(RepoSyncWorker.KEY_HF_REPO_ID, hfRepoId)
    .putString(RepoSyncWorker.KEY_HF_TOKEN, hfToken)
    .putString(RepoSyncWorker.KEY_REPO_TYPE, repoType)
    .putLong(RepoSyncWorker.KEY_SYNC_ID, syncId)
    .build()
```

#### Creating Work Request

```kotlin
val workRequest = OneTimeWorkRequestBuilder<RepoSyncWorker>()
    .setInputData(inputData)
    .setConstraints(
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    )
    .build()

WorkManager.getInstance(context).enqueue(workRequest)
```

#### Observing Work Status

```kotlin
WorkManager.getInstance(context)
    .getWorkInfoByIdLiveData(workRequest.id)
    .observe(lifecycleOwner) { workInfo ->
        when (workInfo.state) {
            WorkInfo.State.SUCCEEDED -> {
                // Sync completed
                val message = workInfo.outputData.getString("message")
            }
            WorkInfo.State.FAILED -> {
                // Sync failed
                val error = workInfo.outputData.getString("error")
            }
            WorkInfo.State.RUNNING -> {
                // Sync in progress
            }
            else -> {}
        }
    }
```

## Data Models

### SyncRecord

```kotlin
@Entity(tableName = "sync_history")
data class SyncRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val githubUrl: String,
    val hfRepoId: String,
    val repoType: String,
    val status: SyncStatus,
    val errorMessage: String? = null,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null
)

enum class SyncStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED
}
```

## External APIs Used

### Hugging Face Hub API

The app uses the `huggingface_hub` Python library.

**Key Operations:**

1. **Create Repository**
   ```python
   api.create_repo(
       repo_id="username/repo-name",
       repo_type="model",  # or "dataset", "space"
       exist_ok=True
   )
   ```

2. **Upload Folder**
   ```python
   api.upload_folder(
       folder_path="./local_repo",
       repo_id="username/repo-name",
       repo_type="model",
       commit_message="Synced from GitHub"
   )
   ```

**Documentation**: [huggingface_hub docs](https://huggingface.co/docs/huggingface_hub/)

### GitHub (via Git)

The app uses Git CLI commands for cloning.

**Clone Command:**
```bash
git clone --depth 1 <github_url> <local_path>
```

**Arguments:**
- `--depth 1`: Shallow clone (only latest commit)
- Reduces download size and time

## Error Handling

### Error Types

| Error Type | Cause | Handling |
|------------|-------|----------|
| `ValidationError` | Invalid input | Show error message |
| `NetworkError` | No connection | Retry with exponential backoff |
| `AuthenticationError` | Invalid token | Prompt for new token |
| `StorageError` | Insufficient space | Show storage warning |
| `TimeoutError` | Operation timeout | Show timeout message |

### Error Response Format

```json
{
    "status": "error",
    "error_type": "NetworkError",
    "message": "Failed to clone repository",
    "details": "Connection timeout after 300s"
}
```

## Rate Limits

### Hugging Face

- **Upload**: No strict limit, but large uploads may be throttled
- **API Calls**: ~1000 requests/hour for authenticated users
- **Best Practice**: Add delays between rapid successive syncs

### GitHub

- **Clone**: No limit for public repos
- **Private Repos**: Requires authentication token (future feature)

## Best Practices

### Token Management

```kotlin
// ✅ Good: Retrieve token just before use
val token = tokenStorage.getHuggingFaceToken()
performSync(token)

// ❌ Bad: Store token in variable
class MyClass {
    val token = tokenStorage.getHuggingFaceToken() // Don't do this
}
```

### Error Handling

```kotlin
// ✅ Good: Specific error messages
try {
    performSync()
} catch (e: NetworkException) {
    showError("Please check your internet connection")
} catch (e: AuthException) {
    showError("Invalid token. Please update in settings")
}

// ❌ Bad: Generic error message
try {
    performSync()
} catch (e: Exception) {
    showError("An error occurred")
}
```

### Background Tasks

```kotlin
// ✅ Good: Use constraints
val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .setRequiresBatteryNotLow(true)
    .build()

// ❌ Bad: No constraints
val workRequest = OneTimeWorkRequestBuilder<RepoSyncWorker>()
    .build() // May run at inappropriate times
```

## Integration Examples

### Example 1: Simple Sync

```kotlin
suspend fun syncRepository(
    githubUrl: String,
    hfRepoId: String
) {
    // Get token
    val token = tokenStorage.getHuggingFaceToken()
        ?: throw IllegalStateException("No HF token")
    
    // Create record
    val record = SyncRecord(
        githubUrl = githubUrl,
        hfRepoId = hfRepoId,
        repoType = "model",
        status = SyncStatus.PENDING
    )
    val syncId = dao.insertSync(record)
    
    // Create work request
    val workRequest = OneTimeWorkRequestBuilder<RepoSyncWorker>()
        .setInputData(Data.Builder()
            .putString(KEY_GITHUB_URL, githubUrl)
            .putString(KEY_HF_REPO_ID, hfRepoId)
            .putString(KEY_HF_TOKEN, token)
            .putLong(KEY_SYNC_ID, syncId)
            .build())
        .build()
    
    // Enqueue
    WorkManager.getInstance(context).enqueue(workRequest)
}
```

### Example 2: Batch Sync

```kotlin
suspend fun syncMultipleRepos(repos: List<Pair<String, String>>) {
    val token = tokenStorage.getHuggingFaceToken()
        ?: throw IllegalStateException("No HF token")
    
    repos.forEach { (githubUrl, hfRepoId) ->
        syncRepository(githubUrl, hfRepoId)
        delay(5000) // 5 second delay between syncs
    }
}
```

## Version History

| Version | Changes |
|---------|---------|
| 1.0.0 | Initial release with basic sync functionality |

## Support

For API questions:
- Open an issue on GitHub
- Check existing documentation
- Review code examples
