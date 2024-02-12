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

const val PARAM_SOURCE = "org.jsonschema2dataclass.js2p.GradleVersions#gradleReleasesForTests"

class JavaTaskFunctionalTest {
    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource(PARAM_SOURCE)
    @DisplayName("single execution, no extension")
    fun withoutExtension(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesSingleNoExtension(testProjectDir, true)

        val result = createRunner(gradleVersion = gradleVersion, testProjectDir = testProjectDir)
            .execute(true)

        assertNull(result.task(COLON_TASK_NAME_FOR_COM)?.outcome)
    }

    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource(PARAM_SOURCE)
    @DisplayName("single execution")
    fun singleExtension(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesSingle(testProjectDir, true)

        createRunner(gradleVersion = gradleVersion, testProjectDir = testProjectDir)
            .execute()
            .assertResultAndGeneratedClass()
    }

    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource(PARAM_SOURCE)
    @DisplayName("single extension simple")
    fun singleExtensionSimple(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesSingleSimple(testProjectDir, true)

        createRunner(gradleVersion = gradleVersion, testProjectDir = testProjectDir)
            .execute()
            .assertResultAndGeneratedClass()
    }

    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource(PARAM_SOURCE)
    @DisplayName("multiple executions")
    fun multipleExecutions(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesMultiple(testProjectDir, true)

        createRunner(gradleVersion = gradleVersion, testProjectDir = testProjectDir)
            .execute()
            .assertResultAndGeneratedClass(taskName = COLON_TASK_NAME_FOR_COM, targetFolder = TARGET_FOLDER_CUSTOM)
            .assertResultAndGeneratedClass(taskName = COLON_TASK_NAME_FOR_ORG, targetFolder = TARGET_FOLDER_CUSTOM)
    }

    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource(PARAM_SOURCE)
    @DisplayName("compileJava task depends task even when project has no java code")
    fun noJavaCode(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesSingle(testProjectDir, true)

        createRunner(gradleVersion = gradleVersion, testProjectDir = testProjectDir, task = "compileJava")
            .execute()
            .assertResultAndGeneratedClass()
    }

    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource(PARAM_SOURCE)
    @DisplayName("task is cache-able")
    fun taskIsCacheable(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesSingle(testProjectDir, true)

        val runner = createRunner(gradleVersion = gradleVersion, testProjectDir = testProjectDir)
        runner.execute().assertResultAndGeneratedClass()

        // Run our task twice to be sure that results has been cached
        val execution2 = runner.execute()
        assertEquals(TaskOutcome.UP_TO_DATE, execution2.task(COLON_TASK_NAME_FOR_COM)?.outcome)
    }

    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource(PARAM_SOURCE)
    @DisplayName("task skips if no json file exists")
    fun noJsonFiles(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesSingle(testProjectDir, false)

        val result = createRunner(gradleVersion = gradleVersion, testProjectDir = testProjectDir).execute()
        assertEquals(TaskOutcome.NO_SOURCE, result.task(COLON_TASK_NAME_FOR_COM)?.outcome)
    }

    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource(PARAM_SOURCE)
    @DisplayName("java-library applied after org.jsonschema2dataclass")
    fun lazyWithoutExtension(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesLazyInit(testProjectDir, true)

        createRunner(gradleVersion = gradleVersion, testProjectDir = testProjectDir)
            .execute()
            .assertResultAndGeneratedClass()
    }

    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource(PARAM_SOURCE)
    @DisplayName("jarring sources does not fail after code generation")
    fun sourceJarCompatibility(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesWithSourcesJar(testProjectDir)

        createRunner(gradleVersion = gradleVersion, testProjectDir = testProjectDir, task = "generateAndJarSources")
            .execute()
            .assertResultAndGeneratedClass()
    }

    @ParameterizedTest(name = "[{index}] {displayName} - {0}")
    @MethodSource(
        "org.jsonschema2dataclass.js2p.GradleVersions#configurationCacheCompatibleGradleReleasesForTests",
    )
    @DisplayName("plugin is configuration cache compatible")
    fun configurationCacheCompatibility(gradleVersion: String?, @TempDir testProjectDir: Path) {
        createBuildFilesSingle(testProjectDir, true)

        createRunner(
            gradleVersion = gradleVersion,
            testProjectDir = testProjectDir,
            arguments = arrayOf("--configuration-cache"),
        )
            .execute()
            .assertResultAndGeneratedClass()
    }
}
