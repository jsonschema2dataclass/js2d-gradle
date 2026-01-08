package org.jsonschema2dataclass.js2p

import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.jsonschema2dataclass.internal.task.JS2D_TASK_NAME
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import java.nio.file.Path
import org.gradle.testkit.runner.BuildResult as BuildResultGradle

internal const val COLON_TASK_NAME = ":$JS2D_TASK_NAME"
internal const val COLON_TASK_NAME_FOR_COM = ":${JS2D_TASK_NAME}ConfigCom"
internal const val COLON_TASK_NAME_FOR_ORG = ":${JS2D_TASK_NAME}ConfigOrg"

fun createRunner(
    gradleVersion: String?,
    testProjectDir: Path,
    debug: Boolean = false,
    task: String = COLON_TASK_NAME,
    vararg arguments: String,
): GradleRunner =
    GradleRunner
        .create()
        .withDebug(debug)
        .withPluginClasspath()
        .withProjectDir(testProjectDir.toFile())
        .withArguments(task, *arguments)
        .apply {
            if (gradleVersion != null) {
                withGradleVersion(gradleVersion)
            }
        }

fun GradleRunner.execute(shouldFail: Boolean = false): BuildResult =
    BuildResult(
        projectDir = this.projectDir.toPath(),
        delegate = when (shouldFail) {
            true -> buildAndFail()
            false -> build()
        },
    )

class BuildResult(
    val projectDir: Path,
    private val delegate: BuildResultGradle,
) : BuildResultGradle {
    override fun getOutput(): String = delegate.output

    override fun getTasks(): MutableList<BuildTask> = delegate.tasks

    override fun tasks(outcome: TaskOutcome?): MutableList<BuildTask> = delegate.tasks(outcome)

    override fun taskPaths(outcome: TaskOutcome?): MutableList<String> = delegate.taskPaths(outcome)

    override fun task(taskPath: String?): BuildTask? = delegate.task(taskPath)
}

fun BuildResult.assertResultAndGeneratedClass(
    taskName: String = COLON_TASK_NAME_FOR_COM,
    targetFolder: String = TARGET_FOLDER_DEFAULT,
): BuildResult {
    Assertions.assertEquals(TaskOutcome.SUCCESS, task(taskName)?.outcome, "task $taskName is successful")

    addressJavaExists(projectDir, targetFolder, taskToExecution[taskName]!!, taskToPackage[taskName]!!)

    return this
}

private val taskToExecution = mapOf(
    COLON_TASK_NAME_FOR_COM to EXECUTION_NAME_COM,
    COLON_TASK_NAME_FOR_ORG to EXECUTION_NAME_ORG,
)

private val taskToPackage = mapOf(
    COLON_TASK_NAME_FOR_COM to PACKAGE_COM_EXAMPLE,
    COLON_TASK_NAME_FOR_ORG to PACKAGE_ORG_EXAMPLE,
)

fun addressJavaExists(testProjectDir: Path, targetDirectoryPrefix: String, executionName: String, subfolder: String) {
    val js2pDir = testProjectDir.resolve(targetDirectoryPrefix).resolve(executionName).resolve(subfolder)
    assertTrue(js2pDir.toFile().exists())
    assertTrue(js2pDir.resolve("Address.java").toFile().exists())
}
