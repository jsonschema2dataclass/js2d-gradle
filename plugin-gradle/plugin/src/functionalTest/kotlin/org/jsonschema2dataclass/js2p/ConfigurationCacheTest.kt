package org.jsonschema2dataclass.js2p


import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
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
    @Disabled("Plugin has configuration cache issues - remove to verify fix")
    @DisplayName("task is compatible with configuration cache")
    fun taskIsCompatibleWithConfigurationCache() {
        setupMinimalProject()

        runWithConfigCache() // First run - stores configuration cache
        val result = runWithConfigCache() // Second run - should reuse

        assertTrue(
            result.output.contains("Reusing configuration cache") ||
                result.output.contains("Configuration cache entry reused"),
            "Configuration cache should be reused on second run"
        )
    }

    @Test
    @DisplayName("configuration cache invalidated when build script changes")
    fun configurationCacheInvalidatedWhenBuildScriptChanges() {
        setupMinimalProject()

        runWithConfigCache() // First run - stores configuration cache

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
