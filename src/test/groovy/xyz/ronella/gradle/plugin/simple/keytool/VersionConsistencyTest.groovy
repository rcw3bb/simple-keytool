package xyz.ronella.gradle.plugin.simple.keytool

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

import java.util.regex.Pattern

class VersionConsistencyTest {

    @Test
    void testVersionConsistencyAcrossFiles() {
        // Read version from gradle.properties
        def gradlePropertiesFile = new File("gradle.properties")
        assertTrue(gradlePropertiesFile.exists(), "gradle.properties file should exist")
        
        def gradlePropertiesContent = gradlePropertiesFile.text
        def gradleVersionPattern = Pattern.compile(/version\s*=\s*(.+)/)
        def gradleVersionMatcher = gradleVersionPattern.matcher(gradlePropertiesContent)
        assertTrue(gradleVersionMatcher.find(), "Version should be found in gradle.properties")
        def gradlePropertiesVersion = gradleVersionMatcher.group(1).trim()

        // Read version from README.md
        def readmeFile = new File("README.md")
        assertTrue(readmeFile.exists(), "README.md file should exist")
        
        def readmeContent = readmeFile.text
        def readmeVersionPattern = Pattern.compile(/id\s+"xyz\.ronella\.simple-keytool"\s+version\s+"([^"]+)"/)
        def readmeVersionMatcher = readmeVersionPattern.matcher(readmeContent)
        assertTrue(readmeVersionMatcher.find(), "Version should be found in README.md")
        def readmeVersion = readmeVersionMatcher.group(1).trim()

        // Read version from CHANGELOG.md
        def changelogFile = new File("CHANGELOG.md")
        assertTrue(changelogFile.exists(), "CHANGELOG.md file should exist")
        
        def changelogContent = changelogFile.text
        def lines = changelogContent.split('\n')
        assertTrue(lines.length >= 3, "CHANGELOG.md should have at least 3 lines")
        def changelogLine = lines[2].trim() // Line 3 (0-indexed as 2)
        
        // Extract version number from "## 1.1.1 : 2024-12-09" format
        def changelogVersionPattern = Pattern.compile(/^##\s+([^\s:]+)/)
        def changelogVersionMatcher = changelogVersionPattern.matcher(changelogLine)
        assertTrue(changelogVersionMatcher.find(), "Version should be found in CHANGELOG.md line 3: '${changelogLine}'")
        def changelogVersion = changelogVersionMatcher.group(1).trim()

        // Strip -SNAPSHOT suffix for comparison
        def normalizedGradleVersion = stripSnapshotSuffix(gradlePropertiesVersion)
        def normalizedReadmeVersion = stripSnapshotSuffix(readmeVersion)
        def normalizedChangelogVersion = stripSnapshotSuffix(changelogVersion)

        // Assert all versions are equal (ignoring -SNAPSHOT suffix)
        assertEquals(normalizedGradleVersion, normalizedReadmeVersion, 
            "Version in gradle.properties (${gradlePropertiesVersion}) should match version in README.md (${readmeVersion}) when ignoring -SNAPSHOT suffix")
        
        assertEquals(normalizedGradleVersion, normalizedChangelogVersion, 
            "Version in gradle.properties (${gradlePropertiesVersion}) should match version in CHANGELOG.md (${changelogVersion}) when ignoring -SNAPSHOT suffix")
        
        assertEquals(normalizedReadmeVersion, normalizedChangelogVersion, 
            "Version in README.md (${readmeVersion}) should match version in CHANGELOG.md (${changelogVersion}) when ignoring -SNAPSHOT suffix")

        // Print versions for verification
        println("All versions are consistent:")
        println("  gradle.properties: ${gradlePropertiesVersion}")
        println("  README.md: ${readmeVersion}")
        println("  CHANGELOG.md: ${changelogVersion}")
    }

    /**
     * Strips the -SNAPSHOT suffix from a version string for comparison purposes.
     * @param version The version string to normalize
     * @return The version string without the -SNAPSHOT suffix
     */
    private String stripSnapshotSuffix(String version) {
        return version.replaceAll(/-SNAPSHOT$/, '')
    }
}