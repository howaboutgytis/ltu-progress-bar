package com.ltu.progressbar.version

import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.components.Service
import java.util.Properties
import java.io.InputStreamReader

/**
 * Service responsible for managing plugin version information.
 * This class provides methods to retrieve the current plugin version,
 * check for updates, and other version-related functions.
 */
@Service(Service.Level.APP)
class VersionManager {
    
    private val currentVersion: String by lazy {
        loadVersionFromProperties()
    }
    
    /**
     * Loads the version from gradle.properties file.
     * This is the single source of truth for version information.
     */
    private fun loadVersionFromProperties(): String {
        val resourceStream = javaClass.classLoader.getResourceAsStream("META-INF/plugin-info.properties")
        
        return if (resourceStream != null) {
            val properties = Properties()
            InputStreamReader(resourceStream, Charsets.UTF_8).use {
                properties.load(it)
            }
            properties.getProperty("version", "0.0.0")
        } else {
            // Fallback version if properties file is not found
            "0.0.0"
        }
    }
    
    /**
     * Returns the current version of the plugin.
     */
    fun getCurrentVersion(): String = currentVersion
    
    /**
     * Checks if the current version is compatible with the IDE version.
     */
    fun isCompatibleWithCurrentIde(): Boolean {
        val ideVersion = ApplicationInfo.getInstance().build.baselineVersion
        // Implement compatibility logic here
        return true
    }
    
    /**
     * Parses a version string into its components.
     */
    fun parseVersion(version: String): Triple<Int, Int, Int> {
        val components = version.split(".")
        val major = components.getOrNull(0)?.toIntOrNull() ?: 0
        val minor = components.getOrNull(1)?.toIntOrNull() ?: 0
        val patch = components.getOrNull(2)?.toIntOrNull() ?: 0
        
        return Triple(major, minor, patch)
    }
    
    /**
     * Compares two version strings.
     * @return -1 if version1 < version2, 0 if equal, 1 if version1 > version2
     */
    fun compareVersions(version1: String, version2: String): Int {
        val v1 = parseVersion(version1)
        val v2 = parseVersion(version2)
        
        return when {
            v1.first != v2.first -> v1.first.compareTo(v2.first)
            v1.second != v2.second -> v1.second.compareTo(v2.second)
            else -> v1.third.compareTo(v2.third)
        }
    }
}
