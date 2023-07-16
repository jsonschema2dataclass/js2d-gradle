package org.jsonschema2dataclass.js2p

import org.gradle.api.JavaVersion
import org.gradle.util.GradleVersion
import org.junit.jupiter.params.provider.Arguments

private val gradleReleases = linkedSetOf(
    GradleVersion.current().version,
    "8.0.2", // 8.0.x
    "7.6.1", "7.5.1", // 7.5 - 7.6
    "7.4.2", "7.3.3", "7.2", "7.1.1", "7.0.2", // 7.0 - 7.4
    "6.9.1", "6.8.3", "6.7.1", "6.6.1", // 6.6 - 6.9
    "6.5.1", "6.4.1", "6.3", //   6.3 - 6.5
    "6.2.2", "6.2.1", "6.1.1", "6.0.1", // 6.0 - 6.2
)
private val compatibleVersions = filterCompatibleVersions()

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
private fun gradleSupported(gradleVersion: ComparableGradleVersion): Boolean =
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

private fun filterCompatibleVersions(): List<String> =
    gradleReleases.filter { gradleSupported(ComparableGradleVersion(it)) }

/**
 * Holder class for gradle releases version to test against current java version
 */
@Suppress("unused")
class TestGradleVersionHolder {
    companion object {
        @JvmStatic
        fun gradleReleasesForTests(): List<Arguments> = compatibleVersions.map { Arguments.of(it) }

        @JvmStatic
        fun configurationCacheCompatibleGradleReleasesForTests(): List<Arguments> = compatibleVersions
            .filter {
                ComparableGradleVersion(it) >= 6 to 6
            }.map {
                Arguments.of(it)
            }
    }
}

private class ComparableGradleVersion(
    gradleVersionString: String,
) : Comparable<Pair<Int, Int>> {
    private val gradleVersion: Pair<Int, Int>

    init {
        val gradleVersionParts = gradleVersionString.split(".")
        gradleVersion = gradleVersionParts[0].toInt() to gradleVersionParts[1].toInt()
    }

    override fun compareTo(other: Pair<Int, Int>): Int {
        val resultFirst = this.gradleVersion.first.compareTo(other.first)
        if (resultFirst == 0) {
            return this.gradleVersion.second.compareTo(other.second)
        }
        return resultFirst
    }
}
