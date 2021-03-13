package com.github.eirnym.js2p

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import static com.github.eirnym.js2p.JsonSchemaPlugin.COLON_TASK_NAME
import static com.github.eirnym.js2p.JsonSchemaPlugin.TASK_NAME
import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.io.FileMatchers.anExistingDirectory
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.io.FileMatchers.anExistingFile
import static org.junit.jupiter.api.Assertions.assertEquals

class JavaTaskFunctionalTest {
    @TempDir
    public Path testProjectDir
    private Path buildFile

    @BeforeEach
    void setup() throws IOException {
        buildFile = testProjectDir.resolve("build.gradle")
    }

    @Test
    void withoutExtension() {
        createBuildFiles()
        copyAddressJSON()

        def result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(testProjectDir.toFile())
            .withArguments(TASK_NAME, "--info")
            .build()

        def js2pDir = testProjectDir.resolve("build/generated/sources/js2d")
        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME).outcome)
        assertThat(js2pDir.toFile(), is(anExistingDirectory()))
        assertThat(js2pDir.resolve("Address.java").toFile(), is(anExistingFile()))
    }

    @Test
    @DisplayName("compileJava task depends task even when project has no java code")
    void noJavaCode() {
        createBuildFiles()
        copyAddressJSON()

        def result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.toFile())
                .withArguments("compileJava", "--info")
                .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(COLON_TASK_NAME).outcome)
    }

    @Test
    @DisplayName('task is cache-able')
    void taskIsCachable() {
        createBuildFiles()
        copyAddressJSON()

        GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.toFile())
                .withArguments(TASK_NAME)
                .build()
        def result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.toFile())
                .withArguments(TASK_NAME, "--info")
                .build()

        assertEquals(TaskOutcome.UP_TO_DATE, result.task(COLON_TASK_NAME).outcome)
    }

    @Test
    @DisplayName('task skips if no json file exists')
    void noJsonFiles() {
        createBuildFiles()

        def result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.toFile())
                .withArguments(COLON_TASK_NAME, "--info")
                .build()

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

}
