package org.jsonschema2dataclass.js2p

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import java.nio.file.Path

internal const val PLUGIN_ID = "org.jsonschema2dataclass"
internal const val JS2P_TASK_NAME = "generateJsonSchema2DataClass"

internal val ADDRESS_SCHEMA = """
    |{
    |  "description": "An Address following the convention of http://microformats.org/wiki/hcard",
    |  "type": "object",
    |  "properties": {
    |    "post_office_box": { "type": "string" },
    |    "extended_address": { "type": "string" },
    |    "street_address": { "type": "string" },
    |    "locality":{ "type": "string", "required": true },
    |    "region": { "type": "string", "required": true },
    |    "postal_code": { "type": "string" },
    |    "country_name": { "type": "string", "required": true},
    |    "address": {"type": "array", "items": "string"}
    |  },
    |  "dependencies": {
    |    "post_office_box": "street_address",
    |    "extended_address": "street_address"
    |  }
    |}
""".trimMargin()

internal fun setupBasicProject(
    projectDir: Path,
    buildGradleContent: String,
    schemaContent: String = ADDRESS_SCHEMA,
) {
    // Create build.gradle
    projectDir.resolve("build.gradle").toFile().writeText(buildGradleContent)

    // Create empty settings.gradle
    projectDir.resolve("settings.gradle").toFile().writeText("")

    // Create schema directory and file
    val schemaDir = projectDir.resolve("src/main/resources/json").toFile()
    schemaDir.mkdirs()
    schemaDir.resolve("address.json").writeText(schemaContent)
}

internal fun runGradle(
    projectDir: Path,
    vararg arguments: String,
    shouldFail: Boolean = false,
): BuildResult {
    val runner = GradleRunner
        .create()
        .withPluginClasspath()
        .withProjectDir(projectDir.toFile())
        .withArguments(*arguments, "--stacktrace")

    return if (shouldFail) runner.buildAndFail() else runner.build()
}

internal fun buildGradle(pluginConfig: String): String = """
    |plugins {
    |  id "java-library"
    |  id "$PLUGIN_ID"
    |}
    |repositories {
    |  mavenCentral()
    |}
    |dependencies {
    |  implementation "com.fasterxml.jackson.core:jackson-annotations:2.11.2"
    |}
    |$pluginConfig
    """.trimMargin()

internal fun File.containsText(text: String): Boolean = this.readText().contains(text)
