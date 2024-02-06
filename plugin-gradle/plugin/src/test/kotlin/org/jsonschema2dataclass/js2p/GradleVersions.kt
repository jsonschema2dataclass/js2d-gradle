package org.jsonschema2dataclass.js2p

import org.gradle.api.JavaVersion
import org.gradle.util.GradleVersion
import org.junit.jupiter.params.provider.Arguments

const val ENV_TEST_MIN = "TEST_GRADLE_VER_MIN"
const val ENV_TEST_MAX = "TEST_GRADLE_VER_MAX"
const val ENV_TEST_EXACT = "TEST_GRADLE_VER_EXACT"

private val gradleReleases8x = arrayOf(
    "8.6",
    "8.5",
    "8.4",
    "8.3",
    "8.2.1",
    "8.1.1",
    "8.0.2",
)
private val gradleReleases7x = arrayOf(
    "7.6.4",
    "7.5.1",
    "7.4.2",
    "7.3.3",
    "7.2",
    "7.1.1",
    "7.0.2",
)
private val gradleReleases6x = arrayOf(
    "6.9.1", "6.8.3", "6.7.1", "6.6.1",
    "6.5.1", "6.4.1", "6.3",
    "6.2.2", "6.2.1", "6.1.1", "6.0.1",
)

private val gradleReleases = linkedSetOf(
    GradleVersion.current().version,
    *gradleReleases8x,
    *gradleReleases7x,
    *gradleReleases6x,
)

private val compatibleVersions = filterCompatibleVersions()

/**
 * Supported gradle versions per java:
 *
 * | Java version | Gradle version |
 * |--------------|----------------|
 * | 1.8 - 13     | >= 6.0         |
 * | 14           | >= 6.3         |
 * | 15           | >= 6.7         |
 * | 16           | >= 7.0         |
 * | 17           | >= 7.3         |
 * | 18           | >= 7.5         |
 * | 19           | >= 7.6         |
 * | 20           | >= 8.3         |
 * | 21           | >= 8.6         |
 * | 22           | >= ?.?         |
 * | other        | not supported  |
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
        JavaVersion.VERSION_20 -> gradleVersion >= 8 to 3
        JavaVersion.VERSION_21 -> gradleVersion >= 8 to 6
        else -> false // no official information on Gradle compatibility with further versions of Java
    }

private fun filterCompatibleVersions(): List<String> {
    val minVersion = System.getenv()[ENV_TEST_MIN]?.let { ComparableGradleVersion(it) }
    val maxVersion = System.getenv()[ENV_TEST_MAX]?.let { ComparableGradleVersion(it) }
    val exactVersion = System.getenv()[ENV_TEST_EXACT]?.let { ComparableGradleVersion(it) }

    return gradleReleases
        .filter { v ->
            val version = ComparableGradleVersion(v)

            val supported = gradleSupported(version)
            val isAboveMin = minVersion?.let { version >= it.gradleVersion } ?: true
            val isBelowMax = maxVersion?.let { version <= it.gradleVersion } ?: true
            val isExact = exactVersion?.let { version.gradleVersion == it.gradleVersion } ?: true

            supported && isAboveMin && isBelowMax && isExact
        }
        .toList()
}

/**
 * Holder class for gradle releases version to test against current java
 * version
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
    val gradleVersion: Pair<Int, Int>

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
