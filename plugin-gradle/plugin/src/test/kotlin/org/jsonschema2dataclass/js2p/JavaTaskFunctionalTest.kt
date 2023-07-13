package org.jsonschema2dataclass.js2p

import org.gradle.testkit.runner.TaskOutcome
import org.jsonschema2dataclass.internal.task.DEFAULT_TARGET_FOLDER_BASE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Path

const val TARGET_FOLDER_CUSTOM = "build/$TARGET_FOLDER_BASE_CUSTOM"
const val TARGET_FOLDER_DEFAULT = "build/$DEFAULT_TARGET_FOLDER_BASE"

const val EXECUTION_NAME_COM = "com"
const val PACKAGE_COM_EXAMPLE = "com/example"

const val EXECUTION_NAME_ORG = "org"
const val PACKAGE_ORG_EXAMPLE = "org/example"

const val source = "org.jsonschema2dataclass.js2p.TestGradleVersionHolder#gradleReleasesForTests"

class JavaTaskFunctionalTest {

    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource(source)
    @DisplayName("single execution, no extension")
    fun withoutExtension(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesSingleNoExtension(testProjectDir, true)

        val result = executeRunner(gradleVersion, testProjectDir, shouldFail = true)
        assertNull(result.task(COLON_TASK_NAME_FOR_COM)?.outcome)
    }

    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource(source)
    @DisplayName("single execution")
    fun singleExtension(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesSingle(testProjectDir, true)
        val result = executeRunner(gradleVersion, testProjectDir)
        checkResultAndGeneratedClass(result, testProjectDir, COLON_TASK_NAME_FOR_COM)
    }

    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource(source)
    @DisplayName("single extension simple")
    fun singleExtensionSimple(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesSingleSimple(testProjectDir, true)

        val result = executeRunner(gradleVersion, testProjectDir)
        checkResultAndGeneratedClass(result, testProjectDir, COLON_TASK_NAME_FOR_COM)
    }

    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource(source)
    @DisplayName("multiple executions")
    fun multipleExecutions(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesMultiple(testProjectDir, true)

        val result = executeRunner(gradleVersion, testProjectDir, COLON_TASK_NAME)
        checkResultAndGeneratedClass(result, testProjectDir, COLON_TASK_NAME_FOR_COM, TARGET_FOLDER_CUSTOM)
        checkResultAndGeneratedClass(result, testProjectDir, COLON_TASK_NAME_FOR_ORG, TARGET_FOLDER_CUSTOM)
    }

    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource(source)
    @DisplayName("compileJava task depends task even when project has no java code")
    fun noJavaCode(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesSingle(testProjectDir, true)
        val result = executeRunner(gradleVersion, testProjectDir, "compileJava")
        checkResultAndGeneratedClass(result, testProjectDir, COLON_TASK_NAME_FOR_COM)
    }

    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource(source)
    @DisplayName("task is cache-able")
    fun taskIsCacheable(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesSingle(testProjectDir, true)

        // Run our task twice to be sure that results has been cached
        val runner = createRunner(gradleVersion, testProjectDir)
        val execution1 = runner.build()
        checkResultAndGeneratedClass(execution1, testProjectDir, COLON_TASK_NAME_FOR_COM)
        val execution2 = runner.build()

        assertEquals(TaskOutcome.UP_TO_DATE, execution2.task(COLON_TASK_NAME_FOR_COM)?.outcome)
    }

    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource(source)
    @DisplayName("task skips if no json file exists")
    fun noJsonFiles(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesSingle(testProjectDir, false)
        val result = executeRunner(gradleVersion, testProjectDir)
        assertEquals(TaskOutcome.NO_SOURCE, result.task(COLON_TASK_NAME_FOR_COM)?.outcome)
    }

    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource(source)
    @DisplayName("java-library applied after org.jsonschema2dataclass")
    fun lazyWithoutExtension(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesLazyInit(testProjectDir, true)
        val result = executeRunner(gradleVersion, testProjectDir)
        checkResultAndGeneratedClass(result, testProjectDir, COLON_TASK_NAME_FOR_COM)
    }

    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource(source)
    @DisplayName("jarring sources does not fail after code generation")
    fun sourceJarCompatibility(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesWithSourcesJar(testProjectDir)

        val result = executeRunner(gradleVersion, testProjectDir, task = "generateAndJarSources")
        checkResultAndGeneratedClass(result, testProjectDir, COLON_TASK_NAME_FOR_COM)
    }

    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource("org.jsonschema2dataclass.js2p.TestGradleVersionHolder#configurationCacheCompatibleGradleReleasesForTests")
    @DisplayName("plugin is configuration cache compatible")
    fun configurationCacheCompatibility(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesSingle(testProjectDir, true)

        val runner = createRunner(gradleVersion, testProjectDir)
        val result = runner.withArguments(*runner.arguments.toTypedArray(), "--configuration-cache")
            .build()
        checkResultAndGeneratedClass(result, testProjectDir, COLON_TASK_NAME_FOR_COM)
    }
}
