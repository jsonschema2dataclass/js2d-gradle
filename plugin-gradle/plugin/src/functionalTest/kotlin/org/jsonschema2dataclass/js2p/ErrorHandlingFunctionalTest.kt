package org.jsonschema2dataclass.js2p

import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

/**
 * Functional tests for error handling.
 * Tests that the plugin fails gracefully with clear messages.
 */
class ErrorHandlingFunctionalTest {

    @TempDir
    lateinit var testProjectDir: Path

    @Test
    @DisplayName("invalid config name with hyphen fails with clear message")
    fun invalidConfigNameWithHyphenFails() {
        val buildGradle = buildGradle("""
            |jsonSchema2Pojo {
            |  executions {
            |    "my-config" {
            |      klass.targetPackage = "com.example"
            |    }
            |  }
            |}
        """.trimMargin())

        setupBasicProject(testProjectDir, buildGradle)

        val result = runGradle(testProjectDir, JS2P_TASK_NAME, shouldFail = true)
        // Build should fail - that's the key assertion
        // Error should mention the problematic config name
        assertTrue(
            result.output.contains("my-config"),
            "Error should reference the invalid config name 'my-config'. Output:\n${result.output.takeLast(500)}"
        )
    }

    @Test
    @DisplayName("config name starting with uppercase fails")
    fun configNameStartingWithUppercaseFails() {
        val buildGradle = buildGradle("""
            |jsonSchema2Pojo {
            |  executions {
            |    Main {
            |      klass.targetPackage = "com.example"
            |    }
            |  }
            |}
        """.trimMargin())

        setupBasicProject(testProjectDir, buildGradle)

        val result = runGradle(testProjectDir, JS2P_TASK_NAME, shouldFail = true)
        // Build should fail - that's the key assertion
        // Error should mention the problematic config name
        assertTrue(
            result.output.contains("Main"),
            "Error should reference the invalid config name 'Main'. Output:\n${result.output.takeLast(500)}"
        )
    }

    @Test
    @DisplayName("nonexistent source directory results in NO_SOURCE")
    fun nonexistentSourceDirectoryResultsInNoSource() {
        val buildGradle = buildGradle("""
            |jsonSchema2Pojo {
            |  executions {
            |    main {
            |      io.source.setFrom(files("nonexistent/path"))
            |      klass.targetPackage = "com.example"
            |    }
            |  }
            |}
        """.trimMargin())

        // Don't create schema files - only build config
        testProjectDir.resolve("build.gradle").toFile().writeText(buildGradle)
        testProjectDir.resolve("settings.gradle").toFile().writeText("")

        val result = runGradle(testProjectDir, JS2P_TASK_NAME)

        // Assert using task outcome, not output string
        val taskOutcome = result.task(":${JS2P_TASK_NAME}ConfigMain")?.outcome
        assertNotNull(taskOutcome, "Task should have executed")
        assertEquals(
            TaskOutcome.NO_SOURCE,
            taskOutcome,
            "Task should be NO_SOURCE when source directory doesn't exist. Output:\n${result.output.takeLast(500)}",
        )
    }

    @Test
    @DisplayName("malformed JSON schema fails build")
    fun malformedJsonSchemaFailsBuild() {
        val buildGradle = buildGradle("""
            |jsonSchema2Pojo {
            |  executions {
            |    main {
            |      klass.targetPackage = "com.example"
            |    }
            |  }
            |}
        """.trimMargin())

        val malformedSchema = "{ this is not valid json }"
        setupBasicProject(testProjectDir, buildGradle, malformedSchema)

        val result = runGradle(testProjectDir, JS2P_TASK_NAME, shouldFail = true)

        // Build should fail - that's the key assertion (shouldFail = true)
        // Task should have been attempted (not skipped)
        val taskOutcome = result.task(":${JS2P_TASK_NAME}ConfigMain")?.outcome
        assertNotNull(taskOutcome, "Task should have been attempted")
        assertEquals(
            TaskOutcome.FAILED,
            taskOutcome,
            "Task should fail when schema is malformed. Output:\n${result.output.takeLast(500)}",
        )
    }
}
