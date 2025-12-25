package com.teacherevan.unicornpajamas

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecurePreferences(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "encrypted_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveGitHubToken(token: String) {
        sharedPreferences.edit().putString(KEY_GITHUB_TOKEN, token).apply()
    }

    fun getGitHubToken(): String? {
        return sharedPreferences.getString(KEY_GITHUB_TOKEN, null)
    }

    fun saveHuggingFaceToken(token: String) {
        sharedPreferences.edit().putString(KEY_HF_TOKEN, token).apply()
    }

    fun getHuggingFaceToken(): String? {
        return sharedPreferences.getString(KEY_HF_TOKEN, null)
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val KEY_GITHUB_TOKEN = "github_token"
        private const val KEY_HF_TOKEN = "huggingface_token"
    }
}
