package org.jsonschema2dataclass.js2p

import org.gradle.api.JavaVersion
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.util.GradleVersion
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.provider.Arguments
import java.io.File
import java.nio.file.Path
import java.util.stream.Stream

const val COLON_TASK_NAME = ":$TASK_NAME"
val GRADLE_RELEASES = listOf(
    "7.6", "7.5.1",   // 7.5 - 7.6
    "7.4.2", "7.3.3", "7.2", "7.1.1", "7.0.2", // 7.0 - 7.4
    "6.9.1", "6.8.3", "6.7.1", "6.6.1", // 6.6 - 6.9
    "6.5.1", "6.4.1", "6.3",           //   6.3 - 6.5
    "6.2.2", "6.2.1", "6.1.1", "6.0.1", // 6.0 - 6.2
)

fun assertExists(file: File) {
    assertNotNull(file)
    assertTrue(file.exists())
}

fun addressJavaExists(testProjectDir: Path, targetDirectoryPrefix: String, executionName: String, subfolder: String) {
    val js2pDir = testProjectDir.resolve(targetDirectoryPrefix).resolve(executionName).resolve(subfolder)
    assertExists(js2pDir.toFile())
    assertExists(js2pDir.resolve("Address.java").toFile())
}

fun executeRunner(
    gradleVersion: String?,
    testProjectDir: Path,
    task: String = COLON_TASK_NAME,
    shouldFail: Boolean = false,
): BuildResult {
    val arguments = GradleRunner.create()
        .withDebug(true)
        .withPluginClasspath()
        .withProjectDir(testProjectDir.toFile())
        .withArguments(task, "-S", "--debug")

    if (gradleVersion != null) {
        arguments.withGradleVersion(gradleVersion)
    }
    if (shouldFail) {
        return arguments.buildAndFail()
    }
    return arguments.build()
}

/**
 * Supported gradle versions per java:
 *
 * | Java version | Gradle version |
 * |--------------|----------------|
 * |   1.8 - 13   |    >= 6.0      |
 * |    14        |    >= 6.3      |
 * |    15        |    >= 6.3      |
 * |    16        |    >= 7.0      |
 * |    17        |    >= 7.3      |
 * |    18        |    >= 7.5      |
 * |    19        |    >= 7.6      |
 * |   other      | not supported  |
 */
fun gradleSupported(gradleVersion: Pair<Int, Int>): Boolean =
    when (JavaVersion.current()) {
        in JavaVersion.VERSION_1_8..JavaVersion.VERSION_13 -> gradleVersion.first >= 6
        JavaVersion.VERSION_14 -> gradleVersion.first >= 7 || (gradleVersion.first == 6 && gradleVersion.second >= 3)
        JavaVersion.VERSION_15 -> gradleVersion.first >= 7 || (gradleVersion.first == 6 && gradleVersion.second >= 6)
        JavaVersion.VERSION_16 -> gradleVersion.first >= 7
        JavaVersion.VERSION_17 -> gradleVersion.first >= 8 || (gradleVersion.first == 7 && gradleVersion.second >= 3)
        JavaVersion.VERSION_18 -> gradleVersion.first >= 8 || (gradleVersion.first == 7 && gradleVersion.second >= 5)
        JavaVersion.VERSION_19 -> gradleVersion.first >= 8 || (gradleVersion.first == 7 && gradleVersion.second >= 6)
        else -> false // no official information on Gradle compatibility with further versions of Java
    }

/**
 * Holder class for gradle releases version to test against current java version
 */
class TestGradleVersionHolder {
    companion object {
        @JvmStatic
        fun gradleReleasesForTests(): Stream<Arguments> {
/*
            if (System.getProperties()["Hello"] == null) {
                return Stream.of()
            }
*/

            return GRADLE_RELEASES.stream()
                .filter{
                    GradleVersion.current().version != it // don't test on the same gradle version twice
                }
                .filter {
                    val gradleVersionParts = it.split(".")
                    gradleSupported(Pair(gradleVersionParts[0].toInt(), gradleVersionParts[1].toInt()))
                }
                .map { Arguments.of(it) } as Stream<Arguments>
        }
    }
}
