package org.jsonschema2dataclass.js2p

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.writeText

const val TARGET_FOLDER_BASE_CUSTOM = TARGET_FOLDER_BASE + "s"
val BUILD_FILE_HEADER = """
        |plugins {
        |  id "java"
        |  id "$PLUGIN_ID" version "1.2.3"
        |}
        |repositories {
        |  mavenCentral()
        |}
        |dependencies {
        |  implementation "com.fasterxml.jackson.core:jackson-annotations:2.11.2"
        |}
        """.trimMargin()
val ADDRESS_JSON = """
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

fun writeBuildFiles(testProjectDir: Path, shouldCopyAddressJSON: Boolean, suffix: String) {
    Files.write(testProjectDir.resolve("build.gradle"), (BUILD_FILE_HEADER + "\n" + suffix).trimIndent().toByteArray())
    Files.write(testProjectDir.resolve("settings.gradle"), ByteArray(0))
    if (shouldCopyAddressJSON) {
        copyAddressJSON(testProjectDir)
    }
}

fun createBuildFilesSingleSimple(testProjectDir: Path, shouldCopyAddressJSON: Boolean) {
    writeBuildFiles(
        testProjectDir, shouldCopyAddressJSON,
        """
            |jsonSchema2Pojo{
            |  targetPackage.set("com.example")
            |}
            """.trimMargin()
    )
}

fun createBuildFilesSingleNoExtension(testProjectDir: Path, shouldCopyAddressJSON: Boolean) {
    writeBuildFiles(testProjectDir, shouldCopyAddressJSON, "")
}

/**
 * Multiple executions
 */
fun createBuildFilesMultiple(testProjectDir: Path, shouldCopyAddressJSON: Boolean) {
    writeBuildFiles(
        testProjectDir, shouldCopyAddressJSON,
        """
            |jsonSchema2Pojo{
            |   targetDirectoryPrefix = project.file("${'$'}{buildDir}/$TARGET_FOLDER_BASE_CUSTOM")
            |   executions {
            |     com{
            |       targetPackage = "com.example"
            |     }
            |     org{
            |       targetPackage = "org.example"
            |     }
            |  }
            |}
            """.trimMargin()
    )
}

/**
 * Single with execution
 */
fun createBuildFilesSingle(testProjectDir: Path, shouldCopyAddressJSON: Boolean) {
    writeBuildFiles(
        testProjectDir, shouldCopyAddressJSON,
        """
         |jsonSchema2Pojo {
         |  executions {
         |    com {
         |      source.setFrom files ("${'$'}{project.rootDir}/src/main/resources/json")
         |      targetPackage = "com.example"
         |    }
         |  }
         |}
         """.trimMargin()
    )
}

/**
 * Single with execution, inherited
 */
fun createBuildFilesSingleSourceInherit(testProjectDir: Path, shouldCopyAddressJSON: Boolean) {
    writeBuildFiles(
        testProjectDir, shouldCopyAddressJSON,
        """
         |jsonSchema2Pojo {
         |  source.setFrom files ("${'$'}{project.rootDir}/src/main/resources/json")
         |  executions {
         |    com {
         |      targetPackage = "com.example"
         |    }
         |  }
         |}
    """.trimMargin()
    )
    Files.write(testProjectDir.resolve("settings.gradle"), ByteArray(0))
}

private fun copyAddressJSON(testProjectDir: Path) {
    val jsonDir = testProjectDir.resolve("src/main/resources/json")
    File(jsonDir.toString()).mkdirs()
    jsonDir.resolve("address.json").writeText(ADDRESS_JSON)
}
