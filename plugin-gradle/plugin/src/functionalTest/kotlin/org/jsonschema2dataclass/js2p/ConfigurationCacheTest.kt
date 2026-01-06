package org.jsonschema2dataclass.js2p


import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class ConfigurationCacheTest {

    @TempDir
    lateinit var testProjectDir: Path

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

        val result = runWithConfigCache() // Second run - should NOT reuse cache

        val cacheNotReused = !result.output.contains("Reusing configuration cache") ||
            result.output.contains("Calculating task graph") ||
            result.output.contains("Configuration cache entry stored")

        assertTrue(cacheNotReused, "Configuration cache should be invalidated when build script changes")
    }
}
