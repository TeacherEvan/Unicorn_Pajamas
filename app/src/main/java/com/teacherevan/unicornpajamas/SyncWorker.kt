package com.teacherevan.unicornpajamas

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val gitService = GitService()
    private val securePreferences = SecurePreferences(context)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val githubUrl = inputData.getString(KEY_GITHUB_URL)
                ?: return@withContext Result.failure(
                    workDataOf(KEY_ERROR to "GitHub URL not provided")
                )
            
            val huggingfaceUrl = inputData.getString(KEY_HUGGINGFACE_URL)
                ?: return@withContext Result.failure(
                    workDataOf(KEY_ERROR to "Hugging Face URL not provided")
                )
            
            val githubToken = securePreferences.getGitHubToken()
            val hfToken = securePreferences.getHuggingFaceToken()
            
            // Create temporary directory for cloning
            val tempDir = applicationContext.cacheDir.resolve("temp_repo_${System.currentTimeMillis()}")
            
            // Clone from GitHub
            setProgress(workDataOf(KEY_PROGRESS to "Cloning from GitHub..."))
            val cloneResult = gitService.cloneRepository(
                repoUrl = githubUrl,
                destinationPath = tempDir,
                token = githubToken
            ) { progress ->
                Log.d(TAG, progress)
            }
            
            if (cloneResult.isFailure) {
                tempDir.deleteRecursively()
                return@withContext Result.failure(
                    workDataOf(KEY_ERROR to "Failed to clone: ${cloneResult.exceptionOrNull()?.message}")
                )
            }
            
            // Push to Hugging Face
            setProgress(workDataOf(KEY_PROGRESS to "Pushing to Hugging Face..."))
            val pushResult = gitService.pushToRemote(
                localRepo = tempDir,
                remoteUrl = huggingfaceUrl,
                token = hfToken
            ) { progress ->
                Log.d(TAG, progress)
            }
            
            // Cleanup
            tempDir.deleteRecursively()
            
            if (pushResult.isFailure) {
                return@withContext Result.failure(
                    workDataOf(KEY_ERROR to "Failed to push: ${pushResult.exceptionOrNull()?.message}")
                )
            }
            
            Result.success(workDataOf(KEY_RESULT to "Sync completed successfully"))
        } catch (e: Exception) {
            Log.e(TAG, "Error during sync", e)
            Result.failure(workDataOf(KEY_ERROR to e.message.orEmpty()))
        }
    }

    companion object {
        private const val TAG = "SyncWorker"
        const val KEY_GITHUB_URL = "github_url"
        const val KEY_HUGGINGFACE_URL = "huggingface_url"
        const val KEY_PROGRESS = "progress"
        const val KEY_RESULT = "result"
        const val KEY_ERROR = "error"
    }
}
