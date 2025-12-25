package com.teacherevan.reposync.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Instrumented test for SyncDao
 */
@RunWith(AndroidJUnit4::class)
class SyncDaoTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var database: SyncDatabase
    private lateinit var syncDao: SyncDao
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SyncDatabase::class.java
        ).allowMainThreadQueries().build()
        
        syncDao = database.syncDao()
    }
    
    @After
    fun teardown() {
        database.close()
    }
    
    @Test
    fun insertAndRetrieveSync() = runBlocking {
        // Create test record
        val record = SyncRecord(
            githubUrl = "https://github.com/test/repo",
            hfRepoId = "user/test-model",
            repoType = "model",
            status = SyncRecord.SyncStatus.COMPLETED
        )
        
        // Insert
        val id = syncDao.insertSync(record)
        
        // Retrieve
        val retrieved = syncDao.getSyncById(id)
        
        // Verify
        assertNotNull(retrieved)
        assertEquals(record.githubUrl, retrieved?.githubUrl)
        assertEquals(record.hfRepoId, retrieved?.hfRepoId)
        assertEquals(record.status, retrieved?.status)
    }
    
    @Test
    fun updateSyncStatus() = runBlocking {
        // Create and insert
        val record = SyncRecord(
            githubUrl = "https://github.com/test/repo",
            hfRepoId = "user/test-model",
            repoType = "model",
            status = SyncRecord.SyncStatus.PENDING
        )
        val id = syncDao.insertSync(record)
        
        // Update status
        val retrieved = syncDao.getSyncById(id)!!
        val updated = retrieved.copy(
            status = SyncRecord.SyncStatus.COMPLETED,
            endTime = System.currentTimeMillis()
        )
        syncDao.updateSync(updated)
        
        // Verify
        val final = syncDao.getSyncById(id)
        assertEquals(SyncRecord.SyncStatus.COMPLETED, final?.status)
        assertNotNull(final?.endTime)
    }
    
    @Test
    fun getAllSyncsOrdered() = runBlocking {
        // Insert multiple records
        val record1 = SyncRecord(
            githubUrl = "https://github.com/test/repo1",
            hfRepoId = "user/model1",
            repoType = "model",
            status = SyncRecord.SyncStatus.COMPLETED,
            startTime = 1000
        )
        val record2 = SyncRecord(
            githubUrl = "https://github.com/test/repo2",
            hfRepoId = "user/model2",
            repoType = "dataset",
            status = SyncRecord.SyncStatus.COMPLETED,
            startTime = 2000
        )
        
        syncDao.insertSync(record1)
        syncDao.insertSync(record2)
        
        // Retrieve all
        val all = syncDao.getAllSyncs().first()
        
        // Verify order (most recent first)
        assertEquals(2, all.size)
        assertTrue(all[0].startTime > all[1].startTime)
    }
    
    @Test
    fun deleteSync() = runBlocking {
        // Insert
        val record = SyncRecord(
            githubUrl = "https://github.com/test/repo",
            hfRepoId = "user/test-model",
            repoType = "model",
            status = SyncRecord.SyncStatus.COMPLETED
        )
        val id = syncDao.insertSync(record)
        
        // Verify exists
        assertNotNull(syncDao.getSyncById(id))
        
        // Delete
        syncDao.deleteSync(id)
        
        // Verify deleted
        assertNull(syncDao.getSyncById(id))
    }
}
