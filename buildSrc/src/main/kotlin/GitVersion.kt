import org.gradle.api.Project
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

private val regex = Regex("""^v?([0-9.]*(?:-rc\d+)?)-(\d+)-g([0-9a-f]+)(-dirty)?$""")
private const val defaultVersion = "-.-.--0-g00000000-dirty"

fun gitVersion(project: Project): String =
    processVersionString(commandVersion(project) ?: defaultVersion)

private fun processVersionString(value: String): String {
    if (!regex.matches(value)) {
        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd.HH-mm-ss", Locale.US))
        return "0.0.0-${now}"
    }
    val match = regex.find(value)!!.groupValues

    val version = match[1]
    val commitsAfterTag = match[2]
    val revision  = match[3]
    val dirty = match[4]

    if (commitsAfterTag == "0") {
        return if (dirty.isEmpty()) {
            version
        } else {
            "$version$dirty"
        }
    }

    return "$version-$commitsAfterTag-$revision$dirty"
}

private fun commandVersion(project: Project): String? {
    val process = try {
        ProcessBuilder()
            .command("git", "describe", "--tags", "--long", "--dirty")
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
    } catch (e: UnsupportedOperationException) {
        project.logger.error("Can't execute git", e)
        return null
    } catch (e: IOException) {
        project.logger.error("Can't execute git", e)
        return null
    }

    try {
        process.waitFor(60, TimeUnit.SECONDS)
    } catch (e: InterruptedException) {
        project.logger.error("process timed out", e)
        return null
    }
    if (process.exitValue() == 0) {
        return try {
            process
                .inputStream
                .bufferedReader(StandardCharsets.UTF_8)
                .readText()
                .trim()
        } catch (e: IOException) {
            project.logger.error("process timed out", e)
            null
        }
    } else {
        val output = process
            .errorStream
            .bufferedReader(StandardCharsets.UTF_8)
            .readText()
            .trim()
        project.logger.error("Process exited with code ${process.exitValue()}: $output")
        return null
    }
}
