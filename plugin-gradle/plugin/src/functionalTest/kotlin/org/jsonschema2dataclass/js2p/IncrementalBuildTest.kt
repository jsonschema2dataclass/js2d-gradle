package org.jsonschema2dataclass.js2p

import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class IncrementalBuildTest {

    @TempDir
    lateinit var testProjectDir: Path

    @TempDir
    lateinit var secondProjectDir: Path

    @TempDir
    lateinit var localBuildCacheDir: Path

    private val minimalBuildGradle = buildGradle("""
        |jsonSchema2Pojo {
        |  executions {
        |    main {
        |      klass.targetPackage = "com.example"
        |    }
        |  }
        |}
    """.trimMargin())

    private fun buildCacheSettings() = """
        buildCache {
            local {
                directory = '${localBuildCacheDir.toUri()}'
            }
        }
    """.trimIndent()

    private fun setupProjectWithBuildCache(projectDir: Path) {
        setupBasicProject(projectDir, minimalBuildGradle)
        projectDir.resolve("settings.gradle").toFile().writeText(buildCacheSettings())
    }

    @Test
    @DisplayName("unchanged inputs produce UP_TO_DATE")
    fun unchangedInputsProduceUpToDate() {
        setupBasicProject(testProjectDir, minimalBuildGradle)

        runGradle(testProjectDir, JS2P_TASK_NAME)
        val result = runGradle(testProjectDir, JS2P_TASK_NAME)

        assertEquals(TaskOutcome.UP_TO_DATE, result.task(":${JS2P_TASK_NAME}ConfigMain")?.outcome)
    }

    @Test
    @DisplayName("task restores from build cache")
    fun taskRestoresFromBuildCache() {
        setupProjectWithBuildCache(testProjectDir)

        runGradle(testProjectDir, JS2P_TASK_NAME, "--build-cache")
        testProjectDir.resolve("build").toFile().deleteRecursively()
        val result = runGradle(testProjectDir, JS2P_TASK_NAME, "--build-cache")

        assertEquals(TaskOutcome.FROM_CACHE, result.task(":${JS2P_TASK_NAME}ConfigMain")?.outcome)
    }

    @Test
    @Disabled("Plugin has cache relocatability issues (absolute paths in cache key) - remove to verify fix")
    @DisplayName("build cache is relocatable to different directory")
    fun buildCacheIsRelocatable() {
        setupProjectWithBuildCache(testProjectDir)
        runGradle(testProjectDir, JS2P_TASK_NAME, "--build-cache")

        setupProjectWithBuildCache(secondProjectDir)
        val result = runGradle(secondProjectDir, JS2P_TASK_NAME, "--build-cache")

        assertEquals(TaskOutcome.FROM_CACHE, result.task(":${JS2P_TASK_NAME}ConfigMain")?.outcome)
    }

    @Test
    @DisplayName("modified schema file triggers regeneration")
    fun modifiedSchemaFileTriggersRegeneration() {
        setupBasicProject(testProjectDir, minimalBuildGradle)

        runGradle(testProjectDir, JS2P_TASK_NAME)

        val schemaFile = testProjectDir.resolve("src/main/resources/json/address.json").toFile()
        schemaFile.writeText(ADDRESS_SCHEMA.replace("post_office_box", "po_box"))

        val result = runGradle(testProjectDir, JS2P_TASK_NAME)
        assertEquals(TaskOutcome.SUCCESS, result.task(":${JS2P_TASK_NAME}ConfigMain")?.outcome)
    }

    @Test
    @DisplayName("changed configuration option triggers regeneration")
    fun changedConfigurationOptionTriggersRegeneration() {
        setupBasicProject(testProjectDir, minimalBuildGradle)

        runGradle(testProjectDir, JS2P_TASK_NAME)

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

        val result = runGradle(testProjectDir, JS2P_TASK_NAME)
        assertEquals(TaskOutcome.SUCCESS, result.task(":${JS2P_TASK_NAME}ConfigMain")?.outcome)
    }
}
