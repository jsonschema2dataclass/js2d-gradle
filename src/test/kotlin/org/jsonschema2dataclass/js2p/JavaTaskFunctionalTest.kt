package org.jsonschema2dataclass.js2p

import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.NullSource
import java.nio.file.Path

const val COLON_TASK_NAME0 = COLON_TASK_NAME + "0"
const val COLON_TASK_NAME1 = COLON_TASK_NAME + "1"

const val TARGET_FOLDER_CUSTOM = "build/$TARGET_FOLDER_BASE_CUSTOM"
const val TARGET_FOLDER_DEFAULT = "build/$TARGET_FOLDER_BASE"
const val PACKAGE_EMPTY = ""

const val EXECUTION_NAME_COM = "com"
const val PACKAGE_COM_EXAMPLE = "com/example"

const val EXECUTION_NAME_ORG = "org"
const val PACKAGE_ORG_EXAMPLE = "org/example"

class JavaTaskFunctionalTest {

    @TempDir
    @JvmField
    var testProjectDirPath: Path? = null

    @ParameterizedTest(name = "[{index} - {0}]({argumentsWithNames}) {displayName}")
    @NullSource
    @MethodSource("org.jsonschema2dataclass.js2p.TestGradleVersionHolder#gradleReleasesForTests")
    @DisplayName("single execution, no extension")
    fun withoutExtension(gradleVersion: String?) {
        val testProjectDir = testProjectDirPath ?: throw IllegalStateException("Test project dir path is null")
        createBuildFilesSingleNoExtension(testProjectDir, true)

        val result = executeRunner(gradleVersion, testProjectDir)

        assertNull(result.task(COLON_TASK_NAME0)?.outcome)
    }

    @ParameterizedTest(name = "[{index} - {0}]({argumentsWithNames}) {displayName}")
    @NullSource
    @MethodSource("org.jsonschema2dataclass.js2p.TestGradleVersionHolder#gradleReleasesForTests")
    @DisplayName("single execution")
    fun singleExtension(gradleVersion: String?) {
        val testProjectDir = testProjectDirPath ?: throw IllegalStateException("Test project dir path is null")

        createBuildFilesSingle(testProjectDir, true)

        val result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0)?.outcome)
        addressJavaExists(
            testProjectDir,
            TARGET_FOLDER_DEFAULT,
            EXECUTION_NAME_COM,
            PACKAGE_COM_EXAMPLE
        )
    }

    @ParameterizedTest(name = "[{index} - {0}]({argumentsWithNames}) {displayName}")
    @NullSource
    @MethodSource("org.jsonschema2dataclass.js2p.TestGradleVersionHolder#gradleReleasesForTests")
    @DisplayName("single execution")
    fun singleExtensionInherited(gradleVersion: String?) {
        val testProjectDir = testProjectDirPath ?: throw IllegalStateException("Test project dir path is null")

        createBuildFilesSingleSourceInherit(testProjectDir, true)

        val result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0)?.outcome)
        addressJavaExists(
            testProjectDir,
            TARGET_FOLDER_DEFAULT,
            EXECUTION_NAME_COM,
            PACKAGE_COM_EXAMPLE
        )
    }

    @ParameterizedTest(name = "[{index} - {0}]({argumentsWithNames}) {displayName}")
    @NullSource
    @MethodSource("org.jsonschema2dataclass.js2p.TestGradleVersionHolder#gradleReleasesForTests")
    @DisplayName("single extension simple")
    fun singleExtensionSimple(gradleVersion: String?) {
        val testProjectDir = testProjectDirPath ?: throw IllegalStateException("Test project dir path is null")

        createBuildFilesSingleSimple(testProjectDir, true)

        val result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0)?.outcome)

        addressJavaExists(
            testProjectDir,
            TARGET_FOLDER_DEFAULT,
            DEFAULT_EXECUTION_NAME,
            PACKAGE_COM_EXAMPLE
        )
    }

    @ParameterizedTest(name = "[{index} - {0}]({argumentsWithNames}) {displayName}")
    @NullSource
    @MethodSource("org.jsonschema2dataclass.js2p.TestGradleVersionHolder#gradleReleasesForTests")
    @DisplayName("multiple executions")
    fun multipleExecutions(gradleVersion: String?) {
        val testProjectDir = testProjectDirPath ?: throw IllegalStateException("Test project dir path is null")

        createBuildFilesMultiple(testProjectDir, true)

        val result = executeRunner(gradleVersion, testProjectDir, COLON_TASK_NAME)
        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0)?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME1)?.outcome)

        addressJavaExists(
            testProjectDir,
            TARGET_FOLDER_CUSTOM,
            EXECUTION_NAME_COM,
            PACKAGE_COM_EXAMPLE
        )
        addressJavaExists(
            testProjectDir,
            TARGET_FOLDER_CUSTOM,
            EXECUTION_NAME_ORG,
            PACKAGE_ORG_EXAMPLE
        )
    }

    @ParameterizedTest(name = "[{index} - {0}]({argumentsWithNames}) {displayName}")
    @NullSource
    @MethodSource("org.jsonschema2dataclass.js2p.TestGradleVersionHolder#gradleReleasesForTests")
    @DisplayName("compileJava task depends task even when project has no java code")
    fun noJavaCode(gradleVersion: String?) {
        val testProjectDir = testProjectDirPath ?: throw IllegalStateException("Test project dir path is null")

        createBuildFilesSingle(testProjectDir, true)

        val result = executeRunner(gradleVersion, testProjectDir, "compileJava")

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0)?.outcome)
    }

    @ParameterizedTest(name = "[{index} - {0}]({argumentsWithNames}) {displayName}")
    @NullSource
    @MethodSource("org.jsonschema2dataclass.js2p.TestGradleVersionHolder#gradleReleasesForTests")
    @DisplayName("task is cache-able")
    fun taskIsCacheable(gradleVersion: String?) {
        val testProjectDir = testProjectDirPath ?: throw IllegalStateException("Test project dir path is null")

        createBuildFilesSingle(testProjectDir, true)

        // Run our task twice to be sure that results has been cached
        executeRunner(gradleVersion, testProjectDir)
        val result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.UP_TO_DATE, result.task(COLON_TASK_NAME0)?.outcome)
    }

    @ParameterizedTest(name = "[{index} - {0}]({argumentsWithNames}) {displayName}")
    @NullSource
    @MethodSource("org.jsonschema2dataclass.js2p.TestGradleVersionHolder#gradleReleasesForTests")
    @DisplayName("task skips if no json file exists")
    fun noJsonFiles(gradleVersion: String?) {
        val testProjectDir = testProjectDirPath ?: throw IllegalStateException("Test project dir path is null")

        createBuildFilesSingle(testProjectDir, false)

        val result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.NO_SOURCE, result.task(COLON_TASK_NAME0)?.outcome)
    }
}
