package com.github.eirnym.js2p

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.NullSource

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream

import static com.github.eirnym.js2p.JsonSchemaPlugin.TASK_NAME
import static com.github.eirnym.js2p.JsonSchemaPlugin.TARGET_FOLDER_BASE
import static com.github.eirnym.js2p.JsonSchemaPlugin.PLUGIN_ID

import static org.junit.jupiter.api.Assertions.*

class JavaTaskFunctionalTest {
    private static final String COLON_TASK_NAME = ':' + TASK_NAME
    private static final String COLON_TASK_NAME0 = COLON_TASK_NAME + '0'
    private static final String COLON_TASK_NAME1 = COLON_TASK_NAME + '1'

    private static final String TARGET_FOLDER_BASE0 = TARGET_FOLDER_BASE + '0'
    private static final String TARGET_FOLDER_BASE_CUSTOM = TARGET_FOLDER_BASE + 's'
    private static final String TARGET_FOLDER_BASE_CUSTOM0 = TARGET_FOLDER_BASE_CUSTOM + '0'
    private static final String TARGET_FOLDER_BASE_CUSTOM1 = TARGET_FOLDER_BASE_CUSTOM + '1'
    private static final List<String> GRADLE_RELEASES = [
            '6.8.3', '6.8', '6.7.1', '6.6.1', '6.5.1', '6.4.1', '6.3', '6.2.2', '6.2.1', '6.1.1', '6.0.1',  // 6.x
    ]

    @TempDir
    public Path testProjectDir
    private Path buildFile

    @BeforeEach
    void setup() throws IOException {
        buildFile = testProjectDir.resolve("build.gradle")
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource('gradleReleases')
    @DisplayName("single execution, no extension")
    void withoutExtension(String gradleVersion) {
        createBuildFilesSingleNoExtension()
        copyAddressJSON()

        def result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0).outcome)
        addressJavaExists(testProjectDir, "build/${TARGET_FOLDER_BASE0}")
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource('gradleReleases')
    @DisplayName("single execution")
    void singleExtension(String gradleVersion) {
        createBuildFilesSingle()
        copyAddressJSON()

        def result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0).outcome)
        addressJavaExists(testProjectDir, "build/${TARGET_FOLDER_BASE0}/com/example")
    }
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource('gradleReleases')
    @DisplayName("single execution")
    void singleExtensionInherited(String gradleVersion) {
        createBuildFilesSingleSourceInherit()
        copyAddressJSON()

        def result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0).outcome)
        addressJavaExists(testProjectDir, "build/${TARGET_FOLDER_BASE0}/com/example")
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource('gradleReleases')
    @DisplayName("single execution")
    void singleExtensionSimple(String gradleVersion) {
        createBuildFilesSingleSimple()
        copyAddressJSON()

        def result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0).outcome)
        addressJavaExists(testProjectDir, "build/${TARGET_FOLDER_BASE0}/com/example")
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource('gradleReleases')
    @DisplayName("multiple executions")
    void multipleExecutions(String gradleVersion) {
        createBuildFilesMultiple()

        copyAddressJSON()

        def result = executeRunner(gradleVersion, testProjectDir, COLON_TASK_NAME)
        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0).outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME1).outcome)

        addressJavaExists(testProjectDir, "build/${TARGET_FOLDER_BASE_CUSTOM0}/com/example")
        addressJavaExists(testProjectDir, "build/${TARGET_FOLDER_BASE_CUSTOM1}/org/example")
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource('gradleReleases')
    @DisplayName("compileJava task depends task even when project has no java code")
    void noJavaCode(String gradleVersion) {
        createBuildFilesSingle()
        copyAddressJSON()

        def result = executeRunner(gradleVersion, testProjectDir, "compileJava")

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME0).outcome)
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    @MethodSource('gradleReleases')
    @DisplayName('task is cache-able')
    void taskIsCachable(String gradleVersion) {
        createBuildFilesSingle()
        copyAddressJSON()

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
        createBuildFilesSingle()

        def result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.NO_SOURCE, result.task(COLON_TASK_NAME0).outcome)
    }

    private static final BUILD_FILE_HEADER = """|plugins {
        |  id 'java'
        |  id '${PLUGIN_ID}' version '1.2.3'
        |}
        |repositories {
        |  mavenCentral()
        |}
        |dependencies {
        |  implementation 'com.fasterxml.jackson.core:jackson-annotations:2.11.2'
        |}"""

    private void createBuildFilesSingleSimple() {
        Files.write(buildFile, (BUILD_FILE_HEADER + """
        |jsonSchema2Pojo{
        |  targetPackage = 'com.example'
        |}
        |""").stripMargin().bytes)
        Files.write(testProjectDir.resolve('settings.gradle'), ''.bytes)
    }

    private void createBuildFilesSingleNoExtension() {
        Files.write(buildFile, BUILD_FILE_HEADER.stripMargin().bytes)
        Files.write(testProjectDir.resolve('settings.gradle'), ''.bytes)
    }

    /**
     * Multiple executions
     */
    private void createBuildFilesMultiple() {
        Files.write(buildFile, (BUILD_FILE_HEADER + """
        |jsonSchema2Pojo{
        |   targetDirectoryPrefix = "${TARGET_FOLDER_BASE_CUSTOM}"
        |   executions {
        |     com{
        |       targetPackage = 'com.example'
        |     }
        |     org{
        |       targetPackage = 'org.example'
        |     }
        |  }
        |}
        |""").stripMargin().bytes)
        Files.write(testProjectDir.resolve('settings.gradle'), ''.bytes)
    }

    /**
     * Single with execution
     */
    private void createBuildFilesSingle() {
        Files.write(buildFile, (BUILD_FILE_HEADER + '''
        |jsonSchema2Pojo{
        |   executions {
        |     com{
        |       source.setFrom files("${project.rootDir}/src/main/resources/json")
        |       targetPackage = 'com.example'
        |     }
        |  }
        |}
        |''').stripMargin().bytes)
        Files.write(testProjectDir.resolve('settings.gradle'), ''.bytes)
    }
    /**
     * Single with execution, inherited
     */
    private void createBuildFilesSingleSourceInherit() {
        Files.write(buildFile, (BUILD_FILE_HEADER + '''
        |jsonSchema2Pojo{
        |   source.setFrom files("${project.rootDir}/src/main/resources/json")
        |   executions {
        |     com{
        |       targetPackage = 'com.example'
        |     }
        |  }
        |}
        |''').stripMargin().bytes)
        Files.write(testProjectDir.resolve('settings.gradle'), ''.bytes)
    }

    private void copyAddressJSON() {
        Path jsonDir = testProjectDir.resolve('src/main/resources/json')
        new File(jsonDir.toString()).mkdirs()
        def addressJson = jsonDir.resolve("address.json")
        Files.copy(Paths.get("demo", "java", "src", "main", "resources", "json", "address.json"), addressJson)
    }

    private static void assertExists(File file) {
        assertNotNull(file)
        assertTrue(file.exists())
    }

    private static void addressJavaExists(Path testProjectDir, String targetDirectory) {
        def js2pDir = testProjectDir.resolve(targetDirectory)
        assertExists(js2pDir.toFile())
        assertExists(js2pDir.resolve("Address.java").toFile())
    }

    private static BuildResult executeRunner(
            String gradleVersion, Path testProjectDir, String task = COLON_TASK_NAME, boolean shouldFail = false) {
        def arguments = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.toFile())
                .withArguments(task, '-S', '--info')

        if (gradleVersion) {
            arguments.withGradleVersion(gradleVersion)
        }
        if (shouldFail) {
            return arguments.buildAndFail()
        }
        return arguments.build()
    }

    static boolean gradleSupported(String gradleVersion, String javaSpecificationVersion) {
        def javaVersion = javaSpecificationVersion.toFloat()
        if (javaVersion <= 13) {  // this includes java '1.8' :)
            return true
        }

        def parts = gradleVersion.split(/\./)

        switch ((int) javaVersion) {
            case 14: return parts[1].toInteger() >= 3
            case 15: return parts[1].toInteger() >= 6
            default: return false // no official information on Gradle compatibility with further versions of Java
        }
    }

    static Stream<Arguments> gradleReleases() {
        String javaSpecificationVersion = System.getProperty('java.specification.version')
        return GRADLE_RELEASES.stream().filter { it -> gradleSupported(it, javaSpecificationVersion) }
                .map { it -> Arguments.of(it) } as Stream<Arguments>
    }
}
