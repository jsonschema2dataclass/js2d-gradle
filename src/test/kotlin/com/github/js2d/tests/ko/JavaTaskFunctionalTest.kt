package com.github.js2d.tests.ko

import com.github.js2d.Constants
import com.github.js2d.tests.ko.TestCasesGenerator.Companion.TARGET_FOLDER_BASE_CUSTOM
import com.github.js2d.tests.ko.TestCasesGenerator.Companion.createBuildFilesMultiple
import com.github.js2d.tests.ko.TestCasesGenerator.Companion.createBuildFilesSingle
import com.github.js2d.tests.ko.TestCasesGenerator.Companion.createBuildFilesSingleNoExtension
import com.github.js2d.tests.ko.TestCasesGenerator.Companion.createBuildFilesSingleSimple
import com.github.js2d.tests.ko.TestCasesGenerator.Companion.createBuildFilesSingleSourceInherit

import com.github.js2d.ko.plugin.Constants.Companion.DEFAULT_EXECUTION_NAME
import com.github.js2d.tests.ko.TestUtils.Companion.COLON_TASK_NAME
import com.github.js2d.tests.ko.TestUtils.Companion.GRADLE_SUPPORTED_RELEASES
import com.github.js2d.tests.ko.TestUtils.Companion.addressJavaExists
import com.github.js2d.tests.ko.TestUtils.Companion.executeRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.NullSource
import java.nio.file.Path
import java.util.stream.Stream

class JavaTaskFunctionalTest {
    val COLON_TASK_NAME0: String = COLON_TASK_NAME + "0"
    val COLON_TASK_NAME1: String = COLON_TASK_NAME + "1"

    val TARGET_FOLDER_CUSTOM: String = "build/" + TARGET_FOLDER_BASE_CUSTOM
    val TARGET_FOLDER_DEFAULT: String = "build/" + Constants.TARGET_FOLDER_BASE
    val PACKAGE_EMPTY: String = ""

    val EXECUTION_NAME_COM: String = "com"
    val PACKAGE_COM_EXAMPLE: String = "com/example"

    val EXECUTION_NAME_ORG: String = "org"
    val PACKAGE_ORG_EXAMPLE: String = "org/example"

    @TempDir
    lateinit var testProjectDir: Path
    val gradleReleasesList: List<Arguments> = GRADLE_SUPPORTED_RELEASES.map { Arguments.of(it) }

    fun gradleReleases(): Stream<Arguments> {
        return gradleReleasesList.stream()
    }


    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource("gradleReleases")
    @DisplayName("single execution, no extension")
    fun withoutExtension(gradleVersion:String) {
        createBuildFilesSingleNoExtension(testProjectDir, true)

        val result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0)?.outcome)
        addressJavaExists(
            testProjectDir,
            TARGET_FOLDER_DEFAULT,
            DEFAULT_EXECUTION_NAME,
            PACKAGE_EMPTY)
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource("gradleReleases")
    @DisplayName("single execution")
    fun singleExtension(gradleVersion:String) {
        createBuildFilesSingle(testProjectDir, true)

        val result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0)?.outcome)
        addressJavaExists(
            testProjectDir,
            TARGET_FOLDER_DEFAULT,
            EXECUTION_NAME_COM,
            PACKAGE_COM_EXAMPLE)
    }
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource("gradleReleases")
    @DisplayName("single execution")
    fun singleExtensionInherited(gradleVersion:String) {
        createBuildFilesSingleSourceInherit(testProjectDir, true)

        val result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0)?.outcome)
        addressJavaExists(
            testProjectDir,
            TARGET_FOLDER_DEFAULT,
            EXECUTION_NAME_COM,
            PACKAGE_COM_EXAMPLE)
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource("gradleReleases")
    @DisplayName("single execution")
    fun singleExtensionSimple(gradleVersion:String) {
        createBuildFilesSingleSimple(testProjectDir, true)

        val result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0)?.outcome)

        addressJavaExists(
            testProjectDir,
            TARGET_FOLDER_DEFAULT,
            DEFAULT_EXECUTION_NAME,
            PACKAGE_COM_EXAMPLE)
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource("gradleReleases")
    @DisplayName("multiple executions")
    fun multipleExecutions(gradleVersion:String) {
        createBuildFilesMultiple(testProjectDir, true)

        val result = executeRunner(gradleVersion, testProjectDir, COLON_TASK_NAME)
        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0)?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME1)?.outcome)

        addressJavaExists(
            testProjectDir,
            TARGET_FOLDER_CUSTOM,
            EXECUTION_NAME_COM,
            PACKAGE_COM_EXAMPLE)
        addressJavaExists(
            testProjectDir,
            TARGET_FOLDER_CUSTOM,
            EXECUTION_NAME_ORG,
            PACKAGE_ORG_EXAMPLE)
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource("gradleReleases")
    @DisplayName("compileJava task depends task even when project has no java code")
    fun noJavaCode(gradleVersion:String) {
        createBuildFilesSingle(testProjectDir, true)

        val result = executeRunner(gradleVersion, testProjectDir, "compileJava")

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0)?.outcome)
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource("gradleReleases")
    @DisplayName("task is cache-able")
    fun taskIsCachable(gradleVersion:String) {
        createBuildFilesSingle(testProjectDir, true)

        // Run our task twice to be sure that results has been cached
        executeRunner(gradleVersion, testProjectDir)
        val result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.UP_TO_DATE, result.task(COLON_TASK_NAME0)?.outcome)
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource("gradleReleases")
    @DisplayName("task skips if no json file exists")
    fun noJsonFiles(gradleVersion:String) {
        createBuildFilesSingle(testProjectDir, false)

        val result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.NO_SOURCE, result.task(COLON_TASK_NAME0)?.outcome)
    }
}