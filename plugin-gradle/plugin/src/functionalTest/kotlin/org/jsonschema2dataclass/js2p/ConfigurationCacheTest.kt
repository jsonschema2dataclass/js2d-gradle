package org.jsonschema2dataclass.js2p

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.util.GradleVersion
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class ConfigurationCacheTest {

    companion object {
        private val MIN_GRADLE_VERSION = GradleVersion.version("8.1")
    }

    @TempDir
    lateinit var testProjectDir: Path

    @BeforeEach
    fun checkGradleVersion() {
        assumeTrue(
            GradleVersion.current() >= MIN_GRADLE_VERSION,
            "Configuration cache tests require Gradle ${MIN_GRADLE_VERSION.version}+ (current: ${GradleVersion.current().version})"
        )
    }

    private fun runWithConfigCache(): BuildResult = GradleRunner.create()
        .withPluginClasspath()
        .withProjectDir(testProjectDir.toFile())
        .withArguments("--configuration-cache", JS2P_TASK_NAME)
        .build()

    private fun setupMinimalProject() {
        val buildGradle = buildGradle("""
            |jsonSchema2Pojo {
            |  executions {
            |    main {
            |      klass.targetPackage = "com.example"
            |    }
            |  }
            |}
        """.trimMargin())
        setupBasicProject(testProjectDir, buildGradle)
    }

    @Test
    @DisplayName("task is compatible with configuration cache")
    fun taskIsCompatibleWithConfigurationCache() {
        setupMinimalProject()

        // First run without config cache to stabilize filesystem (processResources creates build/resources/main/json)
        runGradle(testProjectDir, JS2P_TASK_NAME)

        runWithConfigCache() // Second run - stores configuration cache
        val result = runWithConfigCache() // Third run - should reuse

        val isReused = result.output.contains("Reusing configuration cache") ||
            result.output.contains("Configuration cache entry reused")
        assertTrue(
            isReused,
            "Configuration cache should be reused on second run. Output:\n${result.output}"
        )
    }

    @Test
    @DisplayName("configuration cache invalidated when build script changes")
    fun configurationCacheInvalidatedWhenBuildScriptChanges() {
        setupMinimalProject()

        // First run without config cache to stabilize filesystem
        runGradle(testProjectDir, JS2P_TASK_NAME)

        runWithConfigCache() // Second run - stores configuration cache

        // Modify build script
        val newBuildGradle = buildGradle("""
            |jsonSchema2Pojo {
            |  executions {
            |    main {
            |      klass.targetPackage = "com.example"
            |      methods.builders = true
            |    }
            |  }
            |}
        """.trimMargin())
        testProjectDir.resolve("build.gradle").toFile().writeText(newBuildGradle)

        val result = runWithConfigCache() // Third run - should NOT reuse cache

        // Cache should NOT be reused - verify neither reuse message appears
        val cacheNotReused = !result.output.contains("Reusing configuration cache") &&
            !result.output.contains("Configuration cache entry reused")

        assertTrue(
            cacheNotReused,
            "Configuration cache should be invalidated when build script changes. Output:\n${result.output}"
        )

        // Verify invalidation was due to build script change (not some other reason)
        val invalidatedDueToBuildScript = result.output.contains("build.gradle' has changed") ||
            result.output.contains("build.gradle has changed")

        assertTrue(
            invalidatedDueToBuildScript,
            "Configuration cache should be invalidated specifically because build.gradle changed. Output:\n${result.output}"
        )
    }
}
