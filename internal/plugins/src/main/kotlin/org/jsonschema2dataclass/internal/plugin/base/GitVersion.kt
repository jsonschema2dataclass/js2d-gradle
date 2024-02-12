package org.jsonschema2dataclass.internal.plugin.base

import EXTRA_GIT_VERSION_ENABLE
import EXTRA_GIT_VERSION_OVERRIDE
import extraValue
import isExtraEnabled
import org.gradle.api.Project
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

private val regex = Regex("""^v?([0-9.]*(?:-rc\d+)?)-(\d+)-g([0-9a-f]+)(-dirty)?$""")
private const val DEFAULT_VERSION = "-.-.--0-g00000000-dirty"

fun gitVersion(project: Project): String {
    val versionOverride = project.extraValue(EXTRA_GIT_VERSION_OVERRIDE)
    if (versionOverride != null) {
        return versionOverride
    }

    val commandVersion = when (project.isExtraEnabled(EXTRA_GIT_VERSION_ENABLE)) {
        true -> commandVersion(project)
        false -> null
    }
    return processVersionString(commandVersion ?: DEFAULT_VERSION)
}

private fun processVersionString(value: String): String {
    val match = regex.find(value)
    if (match == null) {
        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd.HH-mm-ss", Locale.US))
        return "0.0.0-$now"
    }

    val (version, commitsAfterTag, revision, dirty) = match.destructured

    return when (commitsAfterTag) {
        "0" -> if (dirty.isEmpty()) {
            version
        } else {
            "$version$dirty"
        }

        else -> "$version-$commitsAfterTag-$revision$dirty"
    }
}

private const val ONE_MINUTE = 60L

private fun commandVersion(project: Project): String? {
    try {
        val process = ProcessBuilder()
            .command("git", "describe", "--tags", "--long", "--dirty")
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
        process.waitFor(ONE_MINUTE, TimeUnit.SECONDS)
        return if (process.exitValue() == 0) {
            process
                .inputStream
                .bufferedReader(StandardCharsets.UTF_8)
                .readText()
                .trim()
        } else {
            val output = process
                .errorStream
                .bufferedReader(StandardCharsets.UTF_8)
                .readText()
                .trim()
            project.logger.error("Process exited with code ${process.exitValue()}: $output")
            null
        }
    } catch (e: UnsupportedOperationException) {
        project.logger.error("Can't execute git", e)
    } catch (e: IOException) {
        project.logger.error("Can't execute git", e)
    } catch (e: InterruptedException) {
        project.logger.error("process timed out", e)
    }
    return null
}
