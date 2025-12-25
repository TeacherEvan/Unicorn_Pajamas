package com.teacherevan.reposync.ui

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.google.android.material.snackbar.Snackbar
import com.teacherevan.reposync.R
import com.teacherevan.reposync.data.SyncDatabase
import com.teacherevan.reposync.data.SyncRecord
import com.teacherevan.reposync.security.BiometricAuthHelper
import com.teacherevan.reposync.security.SecureTokenStorage
import com.teacherevan.reposync.workers.RepoSyncWorker
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    companion object {
        // Compiled regex patterns for validation (compiled once for performance)
        private val GITHUB_URL_PATTERN = Regex("^https://github\\.com/[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+(/|\\.git)?$")
        private val HF_REPO_ID_PATTERN = Regex("^[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+$")
    }
    
    private lateinit var tokenStorage: SecureTokenStorage
    private lateinit var database: SyncDatabase
    private lateinit var biometricHelper: BiometricAuthHelper
    
    // UI Components
    private lateinit var githubUrlInput: EditText
    private lateinit var hfRepoIdInput: EditText
    private lateinit var repoTypeSpinner: Spinner
    private lateinit var syncButton: Button
    private lateinit var setupTokensButton: Button
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: SyncHistoryAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize components
        tokenStorage = SecureTokenStorage.getInstance(this)
        database = SyncDatabase.getDatabase(this)
        biometricHelper = BiometricAuthHelper(this)
        
        // Initialize UI
        initializeViews()
        setupRepoTypeSpinner()
        setupClickListeners()
        setupHistoryRecyclerView()
        
        // Observe sync history
        observeSyncHistory()
    }
    
    private fun initializeViews() {
        githubUrlInput = findViewById(R.id.githubUrlInput)
        hfRepoIdInput = findViewById(R.id.hfRepoIdInput)
        repoTypeSpinner = findViewById(R.id.repoTypeSpinner)
        syncButton = findViewById(R.id.syncButton)
        setupTokensButton = findViewById(R.id.setupTokensButton)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        progressBar = findViewById(R.id.progressBar)
        statusText = findViewById(R.id.statusText)
    }
    
    private fun setupRepoTypeSpinner() {
        val repoTypes = arrayOf("model", "dataset", "space")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, repoTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        repoTypeSpinner.adapter = adapter
    }
    
    private fun setupClickListeners() {
        syncButton.setOnClickListener {
            startSync()
        }
        
        setupTokensButton.setOnClickListener {
            showTokenSetupDialog()
        }
    }
    
    private fun setupHistoryRecyclerView() {
        historyAdapter = SyncHistoryAdapter()
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter
    }
    
    private fun observeSyncHistory() {
        lifecycleScope.launch {
            database.syncDao().getAllSyncs().collect { syncs ->
                historyAdapter.submitList(syncs)
            }
        }
    }
    
    private fun startSync() {
        val githubUrl = githubUrlInput.text.toString().trim()
        val hfRepoId = hfRepoIdInput.text.toString().trim()
        val repoType = repoTypeSpinner.selectedItem.toString()
        
        // Validate inputs
        if (githubUrl.isEmpty()) {
            showError("Please enter a GitHub URL")
            return
        }
        
        if (hfRepoId.isEmpty()) {
            showError("Please enter a Hugging Face Repository ID")
            return
        }
        
        // Stricter format validation for GitHub URL
        // Accept URLs like: https://github.com/owner/repo or https://github.com/owner/repo.git
        if (!GITHUB_URL_PATTERN.matches(githubUrl)) {
            showError("GitHub URL must be in format: https://github.com/username/repo-name")
            return
        }
        
        // Accept HF repo IDs like: username/repo-name
        if (!HF_REPO_ID_PATTERN.matches(hfRepoId)) {
            showError("Hugging Face Repository ID must be in format: username/repo-name")
            return
        }
        
        // Check for HF token
        val hfToken = tokenStorage.getHuggingFaceToken()
        if (hfToken.isNullOrEmpty()) {
            showError("Please set up your Hugging Face token first")
            return
        }
        
        // Authenticate with biometric
        if (BiometricAuthHelper.canAuthenticate(this)) {
            biometricHelper.authenticate(
                title = "Authenticate Sync",
                subtitle = "Verify to start repository synchronization",
                onSuccess = {
                    performSync(githubUrl, hfRepoId, hfToken, repoType)
                },
                onError = { error ->
                    showError("Authentication failed: $error")
                },
                onFailed = {
                    showError("Authentication failed")
                }
            )
        } else {
            performSync(githubUrl, hfRepoId, hfToken, repoType)
        }
    }
    
    private fun performSync(githubUrl: String, hfRepoId: String, hfToken: String, repoType: String) {
        lifecycleScope.launch {
            // Create sync record
            val record = SyncRecord(
                githubUrl = githubUrl,
                hfRepoId = hfRepoId,
                repoType = repoType,
                status = SyncRecord.SyncStatus.PENDING
            )
            
            val syncId = database.syncDao().insertSync(record)
            
            // Create work request
            val inputData = Data.Builder()
                .putString(RepoSyncWorker.KEY_GITHUB_URL, githubUrl)
                .putString(RepoSyncWorker.KEY_HF_REPO_ID, hfRepoId)
                .putString(RepoSyncWorker.KEY_HF_TOKEN, hfToken)
                .putString(RepoSyncWorker.KEY_REPO_TYPE, repoType)
                .putLong(RepoSyncWorker.KEY_SYNC_ID, syncId)
                .build()
            
            val workRequest = OneTimeWorkRequestBuilder<RepoSyncWorker>()
                .setInputData(inputData)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
            
            // Enqueue work
            WorkManager.getInstance(this@MainActivity).enqueue(workRequest)
            
            // Show feedback
            progressBar.visibility = View.VISIBLE
            statusText.text = "Sync started..."
            showSuccess("Synchronization started in background")
            
            // Clear inputs
            githubUrlInput.text.clear()
            hfRepoIdInput.text.clear()
        }
    }
    
    private fun showTokenSetupDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_token_setup, null)
        val hfTokenInput = dialogView.findViewById<EditText>(R.id.hfTokenInput)
        
        // Pre-fill existing token (masked)
        tokenStorage.getHuggingFaceToken()?.let { token ->
            hfTokenInput.hint = "Token already set (${token.take(4)}...)"
        }
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Setup API Tokens")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val hfToken = hfTokenInput.text.toString().trim()
                
                if (hfToken.isNotEmpty()) {
                    tokenStorage.saveHuggingFaceToken(hfToken)
                    showSuccess("Hugging Face token saved securely")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showError(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }
    
    private fun showSuccess(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }
}
