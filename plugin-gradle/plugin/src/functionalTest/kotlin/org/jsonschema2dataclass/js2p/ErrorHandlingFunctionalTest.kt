package org.jsonschema2dataclass.js2p

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
        assertTrue(result.output.contains("doesn't support execution name"))
        assertTrue(result.output.contains("my-config"))
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
        assertTrue(result.output.contains("doesn't support execution name"))
        assertTrue(result.output.contains("Main"))
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

        // Don't create schema files
        testProjectDir.resolve("build.gradle").toFile().writeText(buildGradle)
        testProjectDir.resolve("settings.gradle").toFile().writeText("")

        val result = runGradle(testProjectDir, JS2P_TASK_NAME)
        // Task should be NO_SOURCE (skipped) when source directory doesn't exist
        assertTrue(result.output.contains("NO-SOURCE") || result.output.contains("UP-TO-DATE"))
    }

    @Test
    @DisplayName("malformed JSON schema fails build with clear error")
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

        val hasJsonParseError = result.output.contains("Unexpected character") ||
            result.output.contains("JsonParseException") ||
            result.output.contains("JsonMappingException")
        assertTrue(hasJsonParseError, "Should fail with JSON parsing error, got: ${result.output.takeLast(500)}")
    }
}
