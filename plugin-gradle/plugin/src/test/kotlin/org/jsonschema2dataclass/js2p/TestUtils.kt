package org.jsonschema2dataclass.js2p

import org.gradle.api.JavaVersion
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.util.GradleVersion
import org.jsonschema2dataclass.internal.task.JS2D_TASK_NAME
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.provider.Arguments
import java.nio.file.Path
import java.util.stream.Stream

internal const val COLON_TASK_NAME = ":$JS2D_TASK_NAME"
internal const val COLON_TASK_NAME_FOR_COM = ":${JS2D_TASK_NAME}ConfigCom"
internal const val COLON_TASK_NAME_FOR_ORG = ":${JS2D_TASK_NAME}ConfigOrg"
private val gradleReleases = listOf<String>(
    "8.0.1", // 8.x
    "7.6.1", "7.5.1", // 7.5 - 7.6
    "7.4.2", "7.3.3", "7.2", "7.1.1", "7.0.2", // 7.0 - 7.4
    "6.9.1", "6.8.3", "6.7.1", "6.6.1", // 6.6 - 6.9
    "6.5.1", "6.4.1", "6.3", //   6.3 - 6.5
    "6.2.2", "6.2.1", "6.1.1", "6.0.1", // 6.0 - 6.2
)

fun addressJavaExists(testProjectDir: Path, targetDirectoryPrefix: String, executionName: String, subfolder: String) {
    val js2pDir = testProjectDir.resolve(targetDirectoryPrefix).resolve(executionName).resolve(subfolder)
    assertTrue(js2pDir.toFile().exists())
    assertTrue(js2pDir.resolve("Address.java").toFile().exists())
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
        .withArguments(task)

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
 * |    15        |    >= 6.7      |
 * |    16        |    >= 7.0      |
 * |    17        |    >= 7.3      |
 * |    18        |    >= 7.5      |
 * |    19        |    >= 7.6      |
 * |   other      | not supported  |
 */
fun gradleSupported(gradleVersion: ComparableGradleVersion): Boolean =
    when (JavaVersion.current()) {
        in JavaVersion.VERSION_1_8..JavaVersion.VERSION_13 -> gradleVersion >= 6 to 0
        JavaVersion.VERSION_14 -> gradleVersion >= 6 to 3
        JavaVersion.VERSION_15 -> gradleVersion >= 6 to 7
        JavaVersion.VERSION_16 -> gradleVersion >= 7 to 0
        JavaVersion.VERSION_17 -> gradleVersion >= 7 to 3
        JavaVersion.VERSION_18 -> gradleVersion >= 7 to 5
        JavaVersion.VERSION_19 -> gradleVersion >= 7 to 6
        else -> false // no official information on Gradle compatibility with further versions of Java
    }

/**
 * Holder class for gradle releases version to test against current java version
 */
@Suppress("unused")
class TestGradleVersionHolder {
    companion object {
        @JvmStatic
        fun gradleReleasesForTests(): Stream<Arguments> {
            /* TODO: have a way to exclude any other of gradle versions except the current
                     when needed without modifications or rebuild

                if (System.getProperties()["Hello"] == null) {
                    return Stream.of()
                }
            */

            return gradleReleases.stream()
                .filter {
                    GradleVersion.current().version != it // don't test on the same gradle version twice
                }
                .filter {
                    val gradleVersionParts = it.split(".")
                    gradleSupported(
                        ComparableGradleVersion(
                            gradleVersionParts[0].toInt() to gradleVersionParts[1].toInt(),
                        ),
                    )
                }
                .map { Arguments.of(it) } as Stream<Arguments>
        }
    }
}

class ComparableGradleVersion(private val pair: Pair<Int, Int>) : Comparable<Pair<Int, Int>> {
    override fun compareTo(other: Pair<Int, Int>): Int {
        val resultFirst = this.pair.first.compareTo(other.first)
        if (resultFirst == 0) {
            return this.pair.second.compareTo(other.second)
        }
        return resultFirst
    }
}
