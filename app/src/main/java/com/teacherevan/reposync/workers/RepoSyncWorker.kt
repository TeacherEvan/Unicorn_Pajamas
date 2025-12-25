package com.teacherevan.reposync.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.Data
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.teacherevan.reposync.data.SyncDatabase
import com.teacherevan.reposync.data.SyncRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Background worker for synchronizing repositories
 */
class RepoSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    companion object {
        const val TAG = "RepoSyncWorker"
        const val KEY_GITHUB_URL = "github_url"
        const val KEY_HF_REPO_ID = "hf_repo_id"
        const val KEY_HF_TOKEN = "hf_token"
        const val KEY_REPO_TYPE = "repo_type"
        const val KEY_SYNC_ID = "sync_id"
    }
    
    private val database = SyncDatabase.getDatabase(context)
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Extract input parameters
            val githubUrl = inputData.getString(KEY_GITHUB_URL) 
                ?: return@withContext Result.failure(createErrorData("Missing GitHub URL"))
            val hfRepoId = inputData.getString(KEY_HF_REPO_ID)
                ?: return@withContext Result.failure(createErrorData("Missing HF Repo ID"))
            val hfToken = inputData.getString(KEY_HF_TOKEN)
                ?: return@withContext Result.failure(createErrorData("Missing HF Token"))
            val repoType = inputData.getString(KEY_REPO_TYPE) ?: "model"
            val syncId = inputData.getLong(KEY_SYNC_ID, -1)
            
            Log.d(TAG, "Starting sync: $githubUrl -> $hfRepoId")
            
            // Update status to IN_PROGRESS
            if (syncId != -1L) {
                val record = database.syncDao().getSyncById(syncId)
                record?.let {
                    database.syncDao().updateSync(
                        it.copy(status = SyncRecord.SyncStatus.IN_PROGRESS)
                    )
                }
            }
            
            // Initialize Python if not already initialized
            if (!Python.isStarted()) {
                AndroidPlatform.start(applicationContext)
            }
            
            val python = Python.getInstance()
            val module = python.getModule("repo_sync")
            
            // Call Python sync function
            val result = module.callAttr(
                "sync_repos",
                githubUrl,
                hfRepoId,
                hfToken,
                repoType
            )
            
            val status = result.get("status")?.toString()
            val message = result.get("message")?.toString()
            
            Log.d(TAG, "Sync completed: $status - $message")
            
            // Update sync record
            if (syncId != -1L) {
                val record = database.syncDao().getSyncById(syncId)
                record?.let {
                    database.syncDao().updateSync(
                        it.copy(
                            status = SyncRecord.SyncStatus.COMPLETED,
                            endTime = System.currentTimeMillis()
                        )
                    )
                }
            }
            
            Result.success(Data.Builder()
                .putString("status", status)
                .putString("message", message)
                .build())
            
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed", e)
            
            // Update sync record with error
            val syncId = inputData.getLong(KEY_SYNC_ID, -1)
            if (syncId != -1L) {
                val record = database.syncDao().getSyncById(syncId)
                record?.let {
                    database.syncDao().updateSync(
                        it.copy(
                            status = SyncRecord.SyncStatus.FAILED,
                            errorMessage = e.message,
                            endTime = System.currentTimeMillis()
                        )
                    )
                }
            }
            
            Result.failure(createErrorData(e.message ?: "Unknown error"))
        }
    }
    
    private fun createErrorData(errorMessage: String): Data {
        return Data.Builder()
            .putString("error", errorMessage)
            .build()
    }
}
