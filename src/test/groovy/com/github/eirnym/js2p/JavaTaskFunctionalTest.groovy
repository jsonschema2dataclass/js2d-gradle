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
import static org.junit.jupiter.api.Assertions.*

class JavaTaskFunctionalTest {
    private static final String COLON_TASK_NAME = ':' + TASK_NAME
    private static final List<String> GRADLE_RELEASES = [
            '5.6.4',      // 5.x for java8-java11 only
            '6.8.3', '6.8', '6.7.1', '6.6.1', '6.5.1', '6.4.1', '6.3', '6.2.2', '6.2.1', '6.1.1', '6.0.1',  // 6.x
    ]

    @TempDir
    public Path testProjectDir
    private Path buildFile

    @BeforeEach
    void setup() throws IOException {
        buildFile = testProjectDir.resolve("build.gradle")
    }

    @ParameterizedTest
    @NullSource
    @MethodSource('gradleReleases')
    void withoutExtension(String gradleVersion) {
        createBuildFiles()
        copyAddressJSON()

        def result = executeRunner(gradleVersion, testProjectDir)

        def js2pDir = testProjectDir.resolve("build/generated/sources/js2d")
        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME).outcome)
        assertExists(js2pDir.toFile())
        assertExists(js2pDir.resolve("Address.java").toFile())
    }


    @ParameterizedTest
    @NullSource
    @MethodSource('gradleReleases')
    @DisplayName("compileJava task depends task even when project has no java code")
    void noJavaCode(String gradleVersion) {
        createBuildFiles()
        copyAddressJSON()

        def result = executeRunner(gradleVersion, testProjectDir, "compileJava")

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME).outcome)
    }

    @ParameterizedTest
    @NullSource
    @MethodSource('gradleReleases')
    @DisplayName('task is cache-able')
    void taskIsCachable(String gradleVersion) {
        createBuildFiles()
        copyAddressJSON()

        // Run our task twice to be sure that results has been cached

        executeRunner(gradleVersion, testProjectDir)
        def result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.UP_TO_DATE, result.task(COLON_TASK_NAME).outcome)
    }

    @ParameterizedTest
    @NullSource
    @MethodSource('gradleReleases')
    @DisplayName('task skips if no json file exists')
    void noJsonFiles(String gradleVersion) {
        createBuildFiles()

        def result = executeRunner(gradleVersion, testProjectDir)

        assertEquals(TaskOutcome.NO_SOURCE, result.task(COLON_TASK_NAME).outcome)
    }

    private void createBuildFiles() {
        Files.write(buildFile, """|plugins {
        |  id 'java'
        |  id 'org.jsonschema2dataclass' version '1.2.3'
        |}
        |repositories {
        |  mavenCentral()
        |}
        |dependencies {
        |  implementation 'com.fasterxml.jackson.core:jackson-annotations:2.11.2'
        |}
        |""".stripMargin().bytes)
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

    private static BuildResult executeRunner(String gradleVersion, Path testProjectDir, String task = COLON_TASK_NAME) {
        def arguments = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.toFile())
                .withArguments(task, "--info")

        if (gradleVersion) {
            arguments.withGradleVersion(gradleVersion)
        }
        return arguments.build()
    }

    static boolean gradleSupported(String gradleVersion, String javaSpecificationVersion) {
        def javaVersion = javaSpecificationVersion.toFloat()
        if ( javaVersion < 13) {  // this includes java '1.8' :)
            return true
        }

        def parts = gradleVersion.split(/\./)

        if (parts[0].toInteger() < 6) {
            return false
        }

        switch ((int)javaVersion) {
            case 13: return true
            case 14: return parts[1].toInteger() >= 3
            case 15: return parts[1].toInteger() >= 6
            default: return false // no official information on Gradle compatibility with further versions of Java
        }
    }

    static Stream<Arguments> gradleReleases() {
        String javaSpecificationVersion = System.getProperty('java.specification.version')
        return GRADLE_RELEASES.stream().filter {it -> gradleSupported(it, javaSpecificationVersion) }
                .map {it -> Arguments.of(it)} as Stream<Arguments>
    }
}
