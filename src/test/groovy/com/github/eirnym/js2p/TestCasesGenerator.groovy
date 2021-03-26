package com.github.eirnym.js2p

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import static com.github.eirnym.js2p.JsonSchemaPlugin.PLUGIN_ID
import static com.github.eirnym.js2p.JsonSchemaPlugin.TARGET_FOLDER_BASE

class TestCasesGenerator {
    public static final String TARGET_FOLDER_BASE_CUSTOM = TARGET_FOLDER_BASE + 's'

    public static final String BUILD_FILE_HEADER = """|plugins {
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

    private static void writeBuildFiles(Path testProjectDir, boolean shouldCopyAddressJSON, String suffix) {
        Files.write(testProjectDir.resolve("build.gradle"), (BUILD_FILE_HEADER + suffix).stripMargin().bytes)
        Files.write(testProjectDir.resolve('settings.gradle'), ''.bytes)
        if (shouldCopyAddressJSON) {
            copyAddressJSON(testProjectDir)
        }
    }

    static void createBuildFilesSingleSimple(Path testProjectDir, boolean shouldCopyAddressJSON) {
        writeBuildFiles(testProjectDir, shouldCopyAddressJSON, """
            |jsonSchema2Pojo{
            |  targetPackage = 'com.example'
            |}
            |""")
    }

    static void createBuildFilesSingleNoExtension(Path testProjectDir, boolean shouldCopyAddressJSON) {
        writeBuildFiles(testProjectDir, shouldCopyAddressJSON, '')
    }

    /**
     * Multiple executions
     */
    static void createBuildFilesMultiple(Path testProjectDir, boolean shouldCopyAddressJSON) {
        writeBuildFiles(testProjectDir, shouldCopyAddressJSON, """
            |jsonSchema2Pojo{
            |   targetDirectoryPrefix = project.file("\${buildDir}/${TARGET_FOLDER_BASE_CUSTOM}")
            |   executions {
            |     com{
            |       targetPackage = 'com.example'
            |     }
            |     org{
            |       targetPackage = 'org.example'
            |     }
            |  }
            |}
            |""")
    }

    /**
     * Single with execution
     */
    static void createBuildFilesSingle(Path testProjectDir, boolean shouldCopyAddressJSON) {
        writeBuildFiles(testProjectDir, shouldCopyAddressJSON, '''
            |jsonSchema2Pojo{
            |   executions {
            |     com{
            |       source.setFrom files("${project.rootDir}/src/main/resources/json")
            |       targetPackage = 'com.example'
            |     }
            |  }
            |}
            |''')
    }
    /**
     * Single with execution, inherited
     */
    static void createBuildFilesSingleSourceInherit(Path testProjectDir, boolean shouldCopyAddressJSON) {
        writeBuildFiles(testProjectDir, shouldCopyAddressJSON, '''
            |jsonSchema2Pojo{
            |   source.setFrom files("${project.rootDir}/src/main/resources/json")
            |   executions {
            |     com{
            |       targetPackage = 'com.example'
            |     }
            |  }
            |}
            |''')
        Files.write(testProjectDir.resolve('settings.gradle'), ''.bytes)
    }

    private static void copyAddressJSON(testProjectDir) {
        Path jsonDir = testProjectDir.resolve('src/main/resources/json')
        new File(jsonDir.toString()).mkdirs()
        def addressJson = jsonDir.resolve("address.json")
        Files.copy(Paths.get("demo", "java", "src", "main", "resources", "json", "address.json"), addressJson)
    }
}
