package org.jsonschema2dataclass.js2p

import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class ConfigurationOptionsFunctionalTest {
    @TempDir
    lateinit var testProjectDir: Path

    @Test
    @DisplayName("generates Java class from JSON schema")
    fun smokeTestGenerationWorks() {
        val buildGradle =
            buildGradle(
                """
            |jsonSchema2Pojo {
            |  executions {
            |    main {
            |      klass.targetPackage = "com.example"
            |    }
            |  }
            |}
                """.trimMargin(),
            )

        setupBasicProject(testProjectDir, buildGradle)
        val result = runGradle(testProjectDir, JS2P_TASK_NAME)

        assertEquals(TaskOutcome.SUCCESS, result.task(":${JS2P_TASK_NAME}ConfigMain")?.outcome)

        val generated = testProjectDir
            .resolve("build/generated/sources/js2d/main/com/example/Address.java")
            .toFile()
        assertTrue(generated.exists(), "Generated file should exist")
        assertTrue(generated.containsText("public class Address"), "Should generate Address class")
        assertTrue(generated.containsText("package com.example;"), "Should have correct package")
    }

    @Test
    @DisplayName("klass.targetPackage sets output package")
    fun klassTargetPackage() {
        val buildGradle =
            buildGradle(
                """
            |jsonSchema2Pojo {
            |  executions {
            |    main {
            |      klass.targetPackage = "org.custom.pkg"
            |    }
            |  }
            |}
                """.trimMargin(),
            )

        setupBasicProject(testProjectDir, buildGradle)
        val result = runGradle(testProjectDir, JS2P_TASK_NAME)

        assertEquals(TaskOutcome.SUCCESS, result.task(":${JS2P_TASK_NAME}ConfigMain")?.outcome)

        val generated = testProjectDir
            .resolve("build/generated/sources/js2d/main/org/custom/pkg/Address.java")
            .toFile()
        assertTrue(generated.exists(), "Should exist in custom package directory")
        assertTrue(generated.containsText("package org.custom.pkg;"), "Should have correct package")
    }
}
