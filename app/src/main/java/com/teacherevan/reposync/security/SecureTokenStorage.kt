package com.teacherevan.reposync.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Secure storage for API tokens using Android Keystore
 */
class SecureTokenStorage(context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_tokens",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    companion object {
        private const val KEY_GITHUB_TOKEN = "github_token"
        private const val KEY_HF_TOKEN = "hf_token"
        
        @Volatile
        private var INSTANCE: SecureTokenStorage? = null
        
        fun getInstance(context: Context): SecureTokenStorage {
            return INSTANCE ?: synchronized(this) {
                val instance = SecureTokenStorage(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
    
    /**
     * Save GitHub Personal Access Token
     */
    fun saveGithubToken(token: String) {
        sharedPreferences.edit().putString(KEY_GITHUB_TOKEN, token).apply()
    }
    
    /**
     * Get GitHub Personal Access Token
     */
    fun getGithubToken(): String? {
        return sharedPreferences.getString(KEY_GITHUB_TOKEN, null)
    }
    
    /**
     * Save Hugging Face token
     */
    fun saveHuggingFaceToken(token: String) {
        sharedPreferences.edit().putString(KEY_HF_TOKEN, token).apply()
    }
    
    /**
     * Get Hugging Face token
     */
    fun getHuggingFaceToken(): String? {
        return sharedPreferences.getString(KEY_HF_TOKEN, null)
    }
    
    /**
     * Check if GitHub token is stored
     */
    fun hasGithubToken(): Boolean {
        return !getGithubToken().isNullOrEmpty()
    }
    
    /**
     * Check if Hugging Face token is stored
     */
    fun hasHuggingFaceToken(): Boolean {
        return !getHuggingFaceToken().isNullOrEmpty()
    }
    
    /**
     * Clear GitHub token
     */
    fun clearGithubToken() {
        sharedPreferences.edit().remove(KEY_GITHUB_TOKEN).apply()
    }
    
    /**
     * Clear Hugging Face token
     */
    fun clearHuggingFaceToken() {
        sharedPreferences.edit().remove(KEY_HF_TOKEN).apply()
    }
    
    /**
     * Clear all stored tokens
     */
    fun clearAllTokens() {
        sharedPreferences.edit().clear().apply()
    }
}
