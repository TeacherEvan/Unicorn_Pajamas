package com.teacherevan.reposync.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

/**
 * Room database for sync history
 */
@Database(entities = [SyncRecord::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class SyncDatabase : RoomDatabase() {
    
    abstract fun syncDao(): SyncDao
    
    companion object {
        @Volatile
        private var INSTANCE: SyncDatabase? = null
        
        fun getDatabase(context: Context): SyncDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SyncDatabase::class.java,
                    "sync_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * Type converters for Room database
 */
class Converters {
    @TypeConverter
    fun fromSyncStatus(value: SyncRecord.SyncStatus): String {
        return value.name
    }
    
    @TypeConverter
    fun toSyncStatus(value: String): SyncRecord.SyncStatus {
        return SyncRecord.SyncStatus.valueOf(value)
    }
}
