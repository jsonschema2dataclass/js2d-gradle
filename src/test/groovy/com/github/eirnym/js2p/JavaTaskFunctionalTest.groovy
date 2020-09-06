package com.github.eirnym.js2p

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.io.FileMatchers.anExistingDirectory
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.io.FileMatchers.anExistingFile
import static org.junit.Assert.assertEquals

class JavaTaskFunctionalTest {
    @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder();
    private Path buildFile;

    @Before
    void setup() throws IOException {
        buildFile = testProjectDir.newFile("build.gradle").toPath()
    }

    @Test
    void withoutExtension() {
        Files.writeString(buildFile, """|plugins {
        |  id 'java'
        |  id 'com.github.eirnym.js2p'
        |}
        |repositories {
        |  mavenCentral()
        |}
        |dependencies {
        |  compile 'com.fasterxml.jackson.core:jackson-annotations:2.11.2'
        |}
        |""".stripMargin())

        def jsonDir = testProjectDir.newFolder("src", "main", "resources", "json")
        def addressJson = new File(jsonDir, "address.json")
        Files.copy(Paths.get("example", "java", "src", "main", "resources", "json", "address.json"), addressJson.toPath())

        def result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(testProjectDir.root)
            .withArguments("generateJsonSchema2Pojo", "--info")
            .build()

        def js2pDir = testProjectDir.root.toPath().resolve(Paths.get("build", "generated-sources", "js2p"))
        assertEquals(TaskOutcome.SUCCESS, result.task(":generateJsonSchema2Pojo").outcome)
        assertThat(js2pDir.toFile(), is(anExistingDirectory()))
        assertThat(js2pDir.resolve("Address.java").toFile(), is(anExistingFile()))
    }

    @Test
    void "compileJava task depends on generateJsonSchema2Pojo task even when project has no java code"() {
        Files.writeString(buildFile, """|plugins {
        |  id 'java'
        |  id 'com.github.eirnym.js2p'
        |}
        |repositories {
        |  mavenCentral()
        |}
        |dependencies {
        |  compile 'com.fasterxml.jackson.core:jackson-annotations:2.11.2'
        |}
        |""".stripMargin())

        def jsonDir = testProjectDir.newFolder("src", "main", "resources", "json")
        def addressJson = new File(jsonDir, "address.json")
        Files.copy(Paths.get("example", "java", "src", "main", "resources", "json", "address.json"), addressJson.toPath())

        def result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.root)
                .withArguments("compileJava", "--info")
                .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":generateJsonSchema2Pojo").outcome)
    }

    @Test
    void "generateJsonSchema2Pojo task is cache-able"() {
        Files.writeString(buildFile, """|plugins {
        |  id 'java'
        |  id 'com.github.eirnym.js2p'
        |}
        |repositories {
        |  mavenCentral()
        |}
        |dependencies {
        |  compile 'com.fasterxml.jackson.core:jackson-annotations:2.11.2'
        |}
        |""".stripMargin())

        def jsonDir = testProjectDir.newFolder("src", "main", "resources", "json")
        def addressJson = new File(jsonDir, "address.json")
        Files.copy(Paths.get("example", "java", "src", "main", "resources", "json", "address.json"), addressJson.toPath())

        GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.root)
                .withArguments("generateJsonSchema2Pojo")
                .build()
        def result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.root)
                .withArguments("generateJsonSchema2Pojo", "--info")
                .build()

        assertEquals(TaskOutcome.UP_TO_DATE, result.task(":generateJsonSchema2Pojo").outcome)
    }
}
