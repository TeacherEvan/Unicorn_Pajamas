package com.teacherevan.reposync.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Data model for repository sync history
 */
@Entity(tableName = "sync_history")
data class SyncRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val githubUrl: String,
    val hfRepoId: String,
    val repoType: String, // "model", "dataset", or "space"
    val status: SyncStatus,
    val errorMessage: String? = null,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null
) {
    enum class SyncStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
    
    val duration: Long?
        get() = if (endTime != null) endTime - startTime else null
}
