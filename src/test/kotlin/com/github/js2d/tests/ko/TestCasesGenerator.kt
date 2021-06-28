package com.github.js2d.tests.ko

import com.github.js2d.ko.plugin.Constants.Companion.PLUGIN_ID
import com.github.js2d.ko.plugin.Constants.Companion.TARGET_FOLDER_BASE
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class TestCasesGenerator {
    companion object {
        val TARGET_FOLDER_BASE_CUSTOM: String = TARGET_FOLDER_BASE + "s"

        val BUILD_FILE_HEADER: String = """
        |plugins {
        |  id 'java'
        |  id '${PLUGIN_ID}' version '1.2.3'
        |}
        |repositories {
        |  mavenCentral()
        |}
        |dependencies {
        |  implementation 'com.fasterxml.jackson.core:jackson-annotations:2.11.2'
        |}
        """

        private fun writeBuildFiles(testProjectDir: Path, shouldCopyAddressJSON: Boolean, suffix: String) {
            Files.write(testProjectDir.resolve("build.gradle"), (BUILD_FILE_HEADER + suffix).trimMargin().toByteArray(StandardCharsets.UTF_8))
            Files.write(testProjectDir.resolve("settings.gradle"), "".toByteArray(StandardCharsets.UTF_8))
            if (shouldCopyAddressJSON) {
                copyAddressJSON(testProjectDir)
            }
        }

        fun createBuildFilesSingleSimple(testProjectDir: Path, shouldCopyAddressJSON: Boolean) {
            writeBuildFiles(
                testProjectDir, shouldCopyAddressJSON, """
                |jsonSchema2Pojo{
                |  targetPackage = "com.example"
                |}
                """
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
                testProjectDir, shouldCopyAddressJSON, """
                |jsonSchema2Pojo{
                |   targetDirectoryPrefix = project.file("$\{buildDir}/$TARGET_FOLDER_BASE_CUSTOM")
                |   executions {
                |     com{
                |       targetPackage = "com.example"
                |     }
                |     org{
                |       targetPackage = "org.example"
                |     }
                |  }
                |}
                """
            )
        }

        /**
         * Single with execution
         */
        fun createBuildFilesSingle(testProjectDir: Path, shouldCopyAddressJSON: Boolean) {
            writeBuildFiles(
                testProjectDir, shouldCopyAddressJSON, """
                |jsonSchema2Pojo{
                |   executions {
                |     com{
                |       source.setFrom files("$\{project.rootDir}/src/main/resources/json")
                |       targetPackage = "com.example"
                |     }
                |  }
                |}
                """
            )
        }

        /**
         * Single with execution, inherited
         */
        fun createBuildFilesSingleSourceInherit(testProjectDir: Path, shouldCopyAddressJSON: Boolean) {
            writeBuildFiles(
                testProjectDir, shouldCopyAddressJSON, """
                |jsonSchema2Pojo{
                |   source.setFrom files("$\{project.rootDir}/src/main/resources/json")
                |   executions {
                |     com{
                |       targetPackage = "com.example"
                |     }
                |  }
                |}
                """
            )
            Files.write(testProjectDir.resolve("settings.gradle"), "".toByteArray())
        }

        private fun copyAddressJSON(testProjectDir: Path) {
            val jsonDir: Path = testProjectDir.resolve("src/main/resources/json")
            File(jsonDir.toString()).mkdirs()
            val addressJson = jsonDir.resolve("address.json")
            Files.copy(Paths.get("demo", "java", "src", "main", "resources", "json", "address.json"), addressJson)
        }
    }
}