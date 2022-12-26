package org.jsonschema2dataclass.js2p

import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.NullSource
import java.nio.file.Path

class LateInitializationTest {
    @TempDir
    lateinit var testProjectDir: Path

    @ParameterizedTest(name = "[{index} - {0}]({argumentsWithNames}) {displayName}")
    @NullSource
    @MethodSource("org.jsonschema2dataclass.js2p.TestGradleVersionHolder#gradleReleasesForTests")
    @DisplayName("java-library applied after org.jsonschema2dataclass")
    fun withoutExtension(gradleVersion: String?) {
        writeBuildGradle(
            testProjectDir,
            """
            plugins {
                id 'org.jsonschema2dataclass'
            }
            // apply plugin: 'org.jsonschema2dataclass'
            // Even though it is not recommended to use apply syntax, we use it here
            // to ensure java-library plugin is applied after org.jsonschema2dataclass
            apply plugin: 'java-library'
            """.trimIndent()
        )
        copyAddressJSON(testProjectDir)

        val result = executeRunner(gradleVersion, testProjectDir)

        Assertions.assertNull( result.task(COLON_TASK_NAME0)?.outcome)
    }
}
