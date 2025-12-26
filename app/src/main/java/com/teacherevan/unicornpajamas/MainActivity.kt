package com.teacherevan.unicornpajamas

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.material.textfield.TextInputEditText
import com.teacherevan.unicornpajamas.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var securePreferences: SecurePreferences
    private lateinit var voiceService: VoiceService
    private val workManager by lazy { WorkManager.getInstance(this) }
    private val logBuilder = StringBuilder()
    private val mainScope = CoroutineScope(Dispatchers.Main + Job())
    private var welcomeVoicePlayed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        securePreferences = SecurePreferences(this)
        voiceService = VoiceService(this)

        setupUI()
        handleIntent(intent)
        playWelcomeVoice()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.data?.let { uri ->
            // Handle deep link - auto-fill GitHub URL
            binding.githubUrlInput.setText(uri.toString())
            log("Received repository URL via intent: $uri")
        }
    }

    private fun setupUI() {
        binding.syncButton.setOnClickListener {
            startSync()
        }

        // Load saved URLs if any
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        binding.githubUrlInput.setText(prefs.getString("last_github_url", ""))
        binding.huggingfaceUrlInput.setText(prefs.getString("last_hf_url", ""))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                showSettingsDialog()
                true
            }
            R.id.action_clear_logs -> {
                clearLogs()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_settings, null)
        val githubTokenInput = dialogView.findViewById<TextInputEditText>(R.id.githubTokenInput)
        val hfTokenInput = dialogView.findViewById<TextInputEditText>(R.id.hfTokenInput)
        val elevenLabsApiKeyInput = dialogView.findViewById<TextInputEditText>(R.id.elevenLabsApiKeyInput)

        // Load existing tokens (masked)
        val githubToken = securePreferences.getGitHubToken()
        val hfToken = securePreferences.getHuggingFaceToken()
        val elevenLabsApiKey = securePreferences.getElevenLabsApiKey()
        
        if (githubToken != null) {
            githubTokenInput.setText("••••••••")
        }
        if (hfToken != null) {
            hfTokenInput.setText("••••••••")
        }
        if (elevenLabsApiKey != null) {
            elevenLabsApiKeyInput.setText("••••••••")
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.settings)
            .setView(dialogView)
            .setPositiveButton(R.string.save_credentials) { _, _ ->
                val newGithubToken = githubTokenInput.text.toString()
                val newHfToken = hfTokenInput.text.toString()
                val newElevenLabsApiKey = elevenLabsApiKeyInput.text.toString()

                if (newGithubToken.isNotEmpty() && !newGithubToken.startsWith("•")) {
                    securePreferences.saveGitHubToken(newGithubToken)
                }
                if (newHfToken.isNotEmpty() && !newHfToken.startsWith("•")) {
                    securePreferences.saveHuggingFaceToken(newHfToken)
                }
                if (newElevenLabsApiKey.isNotEmpty() && !newElevenLabsApiKey.startsWith("•")) {
                    securePreferences.saveElevenLabsApiKey(newElevenLabsApiKey)
                }

                Toast.makeText(this, R.string.credentials_saved, Toast.LENGTH_SHORT).show()
                log("Credentials updated")
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun startSync() {
        val githubUrl = binding.githubUrlInput.text.toString().trim()
        val hfUrl = binding.huggingfaceUrlInput.text.toString().trim()

        if (githubUrl.isEmpty() || hfUrl.isEmpty()) {
            Toast.makeText(this, R.string.invalid_url, Toast.LENGTH_SHORT).show()
            return
        }

        if (securePreferences.getGitHubToken() == null || securePreferences.getHuggingFaceToken() == null) {
            Toast.makeText(this, R.string.missing_tokens, Toast.LENGTH_LONG).show()
            showSettingsDialog()
            return
        }

        // Save URLs for next time
        getSharedPreferences("app_prefs", MODE_PRIVATE).edit()
            .putString("last_github_url", githubUrl)
            .putString("last_hf_url", hfUrl)
            .apply()

        log("Starting sync...")
        log("GitHub: $githubUrl")
        log("Hugging Face: $hfUrl")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setInputData(
                workDataOf(
                    SyncWorker.KEY_GITHUB_URL to githubUrl,
                    SyncWorker.KEY_HUGGINGFACE_URL to hfUrl
                )
            )
            .build()

        workManager.enqueue(syncRequest)

        // Observe work progress
        workManager.getWorkInfoByIdLiveData(syncRequest.id)
            .observe(this, Observer { workInfo ->
                handleWorkInfo(workInfo)
            })

        binding.progressBar.visibility = View.VISIBLE
        binding.statusText.text = getString(R.string.status_syncing)
        binding.syncButton.isEnabled = false
    }

    private fun handleWorkInfo(workInfo: WorkInfo?) {
        when (workInfo?.state) {
            WorkInfo.State.RUNNING -> {
                val progress = workInfo.progress.getString(SyncWorker.KEY_PROGRESS)
                if (progress != null) {
                    log(progress)
                }
            }
            WorkInfo.State.SUCCEEDED -> {
                binding.progressBar.visibility = View.GONE
                binding.statusText.text = getString(R.string.status_success)
                binding.syncButton.isEnabled = true
                log("✓ Sync completed successfully!")
                Toast.makeText(this, R.string.status_success, Toast.LENGTH_LONG).show()
            }
            WorkInfo.State.FAILED -> {
                binding.progressBar.visibility = View.GONE
                val error = workInfo.outputData.getString(SyncWorker.KEY_ERROR) ?: "Unknown error"
                binding.statusText.text = getString(R.string.status_error, error)
                binding.syncButton.isEnabled = true
                log("✗ Sync failed: $error")
                Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
            }
            WorkInfo.State.CANCELLED -> {
                binding.progressBar.visibility = View.GONE
                binding.statusText.text = getString(R.string.status_idle)
                binding.syncButton.isEnabled = true
                log("Sync cancelled")
            }
            else -> {
                // ENQUEUED or BLOCKED
                binding.progressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun log(message: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val logMessage = "[$timestamp] $message\n"
        logBuilder.append(logMessage)
        binding.logText.text = logBuilder.toString()
        
        // Auto-scroll to bottom
        binding.logText.post {
            var parent = binding.logText.parent
            while (parent != null && parent !is android.widget.ScrollView) {
                parent = parent.parent as? android.view.View
            }
            (parent as? android.widget.ScrollView)?.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun clearLogs() {
        logBuilder.clear()
        binding.logText.text = ""
        log("Logs cleared")
    }

    private fun playWelcomeVoice() {
        // Only play welcome voice once per app session
        if (welcomeVoicePlayed) {
            return
        }

        val apiKey = securePreferences.getElevenLabsApiKey()
        if (apiKey.isNullOrEmpty()) {
            log("ElevenLabs API key not configured. Set it in Settings to enable voice greetings.")
            return
        }

        welcomeVoicePlayed = true
        log("Playing welcome voice sequence...")

        mainScope.launch {
            try {
                voiceService.playWelcomeSequence(apiKey, mainScope) {
                    log("Welcome voice sequence completed")
                }
            } catch (e: Exception) {
                log("Error playing welcome voice: ${e.message}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
        voiceService.cleanup()
    }
}
