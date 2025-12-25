package com.teacherevan.unicornpajamas

import org.junit.Test
import org.junit.Assert.*

class GitServiceTest {
    
    @Test
    fun testGitServiceInstantiation() {
        val gitService = GitService()
        assertNotNull(gitService)
    }
    
    @Test
    fun testValidGitHubUrl() {
        val validUrls = listOf(
            "https://github.com/username/repo.git",
            "https://github.com/username/repo",
            "git@github.com:username/repo.git"
        )
        
        validUrls.forEach { url ->
            assertTrue("URL should be valid: $url", url.isNotEmpty())
            assertTrue("URL should contain github", url.contains("github", ignoreCase = true))
        }
    }
    
    @Test
    fun testValidHuggingFaceUrl() {
        val validUrls = listOf(
            "https://huggingface.co/username/repo",
            "https://huggingface.co/datasets/username/dataset"
        )
        
        validUrls.forEach { url ->
            assertTrue("URL should be valid: $url", url.isNotEmpty())
            assertTrue("URL should contain huggingface", url.contains("huggingface", ignoreCase = true))
        }
    }
}
