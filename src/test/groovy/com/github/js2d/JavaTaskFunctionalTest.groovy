package com.github.js2d

import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.NullSource

import java.nio.file.Path
import java.util.stream.Stream

import static com.github.js2d.JsonSchemaPlugin.DEFAULT_EXECUTION_NAME
import static com.github.js2d.JsonSchemaPlugin.TARGET_FOLDER_BASE

import static TestCasesGenerator.*
import static TestUtils.*

import static org.junit.jupiter.api.Assertions.*

class JavaTaskFunctionalTest {
    private static final String COLON_TASK_NAME0 = COLON_TASK_NAME + '0'
    private static final String COLON_TASK_NAME1 = COLON_TASK_NAME + '1'

    private static final String TARGET_FOLDER_CUSTOM = 'build/' + TARGET_FOLDER_BASE_CUSTOM
    private static final String TARGET_FOLDER_DEFAULT = 'build/' + TARGET_FOLDER_BASE
    private static final String PACKAGE_EMPTY = ''

    private static final String EXECUTION_NAME_COM = 'com'
    private static final String PACKAGE_COM_EXAMPLE = 'com/example'

    private static final String EXECUTION_NAME_ORG = 'org'
    private static final String PACKAGE_ORG_EXAMPLE = 'org/example'

    @TempDir
    public Path testProjectDir

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource('gradleReleases')
    @DisplayName("single execution, no extension")
    void withoutExtension(String gradleVersion) {
        createBuildFilesSingleNoExtension(testProjectDir, true)

        def result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0).outcome)
        addressJavaExists(
                testProjectDir,
                TARGET_FOLDER_DEFAULT,
                DEFAULT_EXECUTION_NAME,
                PACKAGE_EMPTY)
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource('gradleReleases')
    @DisplayName("single execution")
    void singleExtension(String gradleVersion) {
        createBuildFilesSingle(testProjectDir, true)

        def result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0).outcome)
        addressJavaExists(
                testProjectDir,
                TARGET_FOLDER_DEFAULT,
                EXECUTION_NAME_COM,
                PACKAGE_COM_EXAMPLE)
    }
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource('gradleReleases')
    @DisplayName("single execution")
    void singleExtensionInherited(String gradleVersion) {
        createBuildFilesSingleSourceInherit(testProjectDir, true)

        def result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0).outcome)
        addressJavaExists(
                testProjectDir,
                TARGET_FOLDER_DEFAULT,
                EXECUTION_NAME_COM,
                PACKAGE_COM_EXAMPLE)
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource('gradleReleases')
    @DisplayName("single execution")
    void singleExtensionSimple(String gradleVersion) {
        createBuildFilesSingleSimple(testProjectDir, true)

        def result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0).outcome)

        addressJavaExists(
                testProjectDir,
                TARGET_FOLDER_DEFAULT,
                DEFAULT_EXECUTION_NAME,
                PACKAGE_COM_EXAMPLE)
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource('gradleReleases')
    @DisplayName("multiple executions")
    void multipleExecutions(String gradleVersion) {
        createBuildFilesMultiple(testProjectDir, true)

        def result = executeRunner(gradleVersion, testProjectDir, COLON_TASK_NAME)
        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0).outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME1).outcome)

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
    @MethodSource('gradleReleases')
    @DisplayName("compileJava task depends task even when project has no java code")
    void noJavaCode(String gradleVersion) {
        createBuildFilesSingle(testProjectDir, true)

        def result = executeRunner(gradleVersion, testProjectDir, "compileJava")

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0).outcome)
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource('gradleReleases')
    @DisplayName('task is cache-able')
    void taskIsCachable(String gradleVersion) {
        createBuildFilesSingle(testProjectDir, true)

        // Run our task twice to be sure that results has been cached
        executeRunner(gradleVersion, testProjectDir)
        def result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.UP_TO_DATE, result.task(COLON_TASK_NAME0).outcome)
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource('gradleReleases')
    @DisplayName('task skips if no json file exists')
    void noJsonFiles(String gradleVersion) {
        createBuildFilesSingle(testProjectDir, false)

        def result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.NO_SOURCE, result.task(COLON_TASK_NAME0).outcome)
    }

    static Stream<Arguments> gradleReleases() {
        String javaSpecificationVersion = System.getProperty('java.specification.version')
        return GRADLE_RELEASES.stream().filter { it -> gradleSupported(it, javaSpecificationVersion) }
                .map { it -> Arguments.of(it) } as Stream<Arguments>
    }
}
