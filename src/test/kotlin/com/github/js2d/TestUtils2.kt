package com.github.js2d

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.File
import java.nio.file.Path
import java.util.stream.Collectors

class TestUtils2 {
    companion object {
        val COLON_TASK_NAME: String = ":" + Constants.TASK_NAME
        val GRADLE_SUPPORTED_RELEASES = supportedReleases()

        fun assertExists(file: File) {
            assertNotNull(file)
            assertTrue(file.exists())
        }

        fun addressJavaExists(testProjectDir: Path, targetDirectoryPrefix: String, executionName: String, subfolder: String) {
            val js2pDir = testProjectDir.resolve(targetDirectoryPrefix).resolve(executionName).resolve(subfolder)
            assertExists(js2pDir.toFile())
            assertExists(js2pDir.resolve("Address.java").toFile())
        }

        fun executeRunner(gradleVersion: String, testProjectDir: Path, task: String = COLON_TASK_NAME, shouldFail: Boolean = false): BuildResult {
            val arguments = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.toFile())
                .withArguments(task, "-S", "--info")
                .withGradleVersion(gradleVersion)

            if (shouldFail) {
                return arguments.buildAndFail()
            }
            return arguments.build()
        }

        private val GRADLE_RELEASES: List<String> = listOf(
            // "7.0",
            "6.8.3", "6.8", "6.7.1", "6.6.1", "6.5.1", "6.4.1", "6.3", "6.2.2", "6.2.1", "6.1.1", "6.0.1",  // 6.x
        )

        private fun supportedReleases(): List<String> {
            val javaSpecificationVersion: String = System.getProperty("java.specification.version")
            return GRADLE_RELEASES.filter { gradleSupported(it, javaSpecificationVersion.toInt()) }
        }

        private fun gradleSupported(gradleVersion: String, javaVersion: Int): Boolean {
            if (javaVersion <= 13) {  // this includes java "1.8" :)
                return true
            }

            val parts = gradleVersion.split(".")
            val gradleMajor = parts[0].toInt()
            val gradleMinor = parts[1].toInt()

            if (gradleMajor == 7) {
                return javaVersion < 17
            }

            if (gradleMajor <= 5) {
                return false
            }

            return when (javaVersion) {
                14 -> gradleMinor >= 3
                16 -> gradleMinor >= 6
                else -> false
            }
        }
    }
}