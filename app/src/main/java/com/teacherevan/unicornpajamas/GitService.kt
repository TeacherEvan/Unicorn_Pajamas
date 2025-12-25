package com.teacherevan.unicornpajamas

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File

class GitService {
    
    suspend fun cloneRepository(
        repoUrl: String,
        destinationPath: File,
        token: String?,
        onProgress: (String) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            onProgress("Cloning repository from $repoUrl")
            
            // Delete directory if it exists
            if (destinationPath.exists()) {
                destinationPath.deleteRecursively()
            }
            
            val git = if (token != null) {
                Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(destinationPath)
                    .setCredentialsProvider(UsernamePasswordCredentialsProvider(token, ""))
                    .setProgressMonitor(GitProgressMonitor(onProgress))
                    .call()
            } else {
                Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(destinationPath)
                    .setProgressMonitor(GitProgressMonitor(onProgress))
                    .call()
            }
            
            git.close()
            onProgress("Clone completed successfully")
            Result.success(destinationPath)
        } catch (e: Exception) {
            Log.e(TAG, "Error cloning repository", e)
            onProgress("Clone failed: ${e.message}")
            Result.failure(e)
        }
    }
    
    suspend fun pushToRemote(
        localRepo: File,
        remoteUrl: String,
        token: String?,
        onProgress: (String) -> Unit
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            onProgress("Opening local repository")
            val git = Git.open(localRepo)
            
            // Remove existing remote if it exists
            val remotes = git.remoteList().call()
            if (remotes.any { it.name == REMOTE_NAME }) {
                git.remoteRemove().setRemoteName(REMOTE_NAME).call()
                onProgress("Removed existing remote")
            }
            
            // Add new remote
            git.remoteAdd()
                .setName(REMOTE_NAME)
                .setUri(org.eclipse.jgit.transport.URIish(remoteUrl))
                .call()
            onProgress("Added remote: $remoteUrl")
            
            // Push to remote
            onProgress("Pushing to remote...")
            val pushCommand = git.push()
                .setRemote(REMOTE_NAME)
                .setPushAll()
                .setForce(true)
                .setProgressMonitor(GitProgressMonitor(onProgress))
            
            if (token != null) {
                pushCommand.setCredentialsProvider(
                    UsernamePasswordCredentialsProvider(token, "")
                )
            }
            
            pushCommand.call()
            git.close()
            
            onProgress("Push completed successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error pushing to remote", e)
            onProgress("Push failed: ${e.message}")
            Result.failure(e)
        }
    }
    
    private class GitProgressMonitor(
        private val onProgress: (String) -> Unit
    ) : org.eclipse.jgit.lib.ProgressMonitor {
        
        override fun start(totalTasks: Int) {
            onProgress("Starting git operation with $totalTasks tasks")
        }
        
        override fun beginTask(title: String?, totalWork: Int) {
            onProgress("Task: $title")
        }
        
        override fun update(completed: Int) {
            // Can report progress percentage if needed
        }
        
        override fun endTask() {
            // Task completed
        }
        
        override fun isCancelled(): Boolean = false
    }
    
    companion object {
        private const val TAG = "GitService"
        private const val REMOTE_NAME = "huggingface"
    }
}
