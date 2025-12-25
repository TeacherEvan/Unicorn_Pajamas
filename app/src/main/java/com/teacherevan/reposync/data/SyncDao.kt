package com.teacherevan.reposync.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for sync history
 */
@Dao
interface SyncDao {
    
    @Insert
    suspend fun insertSync(record: SyncRecord): Long
    
    @Update
    suspend fun updateSync(record: SyncRecord)
    
    @Query("SELECT * FROM sync_history ORDER BY startTime DESC")
    fun getAllSyncs(): Flow<List<SyncRecord>>
    
    @Query("SELECT * FROM sync_history WHERE id = :id")
    suspend fun getSyncById(id: Long): SyncRecord?
    
    @Query("SELECT * FROM sync_history WHERE status = :status ORDER BY startTime DESC")
    fun getSyncsByStatus(status: SyncRecord.SyncStatus): Flow<List<SyncRecord>>
    
    @Query("DELETE FROM sync_history WHERE id = :id")
    suspend fun deleteSync(id: Long)
    
    @Query("DELETE FROM sync_history")
    suspend fun deleteAllSyncs()
}
