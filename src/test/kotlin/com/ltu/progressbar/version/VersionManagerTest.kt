package com.ltu.progressbar.version

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VersionManagerTest {
    
    private val versionManager = VersionManager()
    
    @Test
    fun testParseVersion() {
        val version = "1.2.3"
        val (major, minor, patch) = versionManager.parseVersion(version)
        
        assertEquals(1, major)
        assertEquals(2, minor)
        assertEquals(3, patch)
    }
    
    @Test
    fun testParseInvalidVersion() {
        val version = "invalid"
        val (major, minor, patch) = versionManager.parseVersion(version)
        
        assertEquals(0, major)
        assertEquals(0, minor)
        assertEquals(0, patch)
    }
    
    @Test
    fun testCompareVersions() {
        assertEquals(-1, versionManager.compareVersions("1.0.0", "2.0.0"))
        assertEquals(0, versionManager.compareVersions("1.0.0", "1.0.0"))
        assertEquals(1, versionManager.compareVersions("1.0.1", "1.0.0"))
        assertEquals(-1, versionManager.compareVersions("1.9.0", "1.10.0"))
    }
}
