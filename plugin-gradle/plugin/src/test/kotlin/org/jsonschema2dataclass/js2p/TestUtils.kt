package org.jsonschema2dataclass.js2p

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.jsonschema2dataclass.internal.task.JS2D_TASK_NAME
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import java.nio.file.Path

internal const val COLON_TASK_NAME = ":$JS2D_TASK_NAME"
internal const val COLON_TASK_NAME_FOR_COM = ":${JS2D_TASK_NAME}ConfigCom"
internal const val COLON_TASK_NAME_FOR_ORG = ":${JS2D_TASK_NAME}ConfigOrg"

fun createRunner(
    gradleVersion: String?,
    testProjectDir: Path,
    task: String = COLON_TASK_NAME,
): GradleRunner =
    GradleRunner.create()
        .withDebug(true)
        .withPluginClasspath()
        .withProjectDir(testProjectDir.toFile())
        .withArguments(task).apply {
            if (gradleVersion != null) {
                withGradleVersion(gradleVersion)
            }
        }

fun executeRunner(
    gradleVersion: String?,
    testProjectDir: Path,
    task: String = COLON_TASK_NAME,
    shouldFail: Boolean = false,
): BuildResult {
    val runner = createRunner(gradleVersion, testProjectDir, task)

    return if (shouldFail) {
        runner.buildAndFail()
    } else {
        runner.build()
    }
}

private val taskToExecution = mapOf(
    COLON_TASK_NAME_FOR_COM to EXECUTION_NAME_COM,
    COLON_TASK_NAME_FOR_ORG to EXECUTION_NAME_ORG,
)

private val taskToPackage = mapOf(
    COLON_TASK_NAME_FOR_COM to PACKAGE_COM_EXAMPLE,
    COLON_TASK_NAME_FOR_ORG to PACKAGE_ORG_EXAMPLE,
)

fun checkResultAndGeneratedClass(
    result: BuildResult,
    testProjectDir: Path,
    taskName: String,
    targetFolder: String = TARGET_FOLDER_DEFAULT,
) {
    Assertions.assertEquals(TaskOutcome.SUCCESS, result.task(taskName)?.outcome, "task $taskName is successful")

    addressJavaExists(testProjectDir, targetFolder, taskToExecution[taskName]!!, taskToPackage[taskName]!!)
}

fun addressJavaExists(testProjectDir: Path, targetDirectoryPrefix: String, executionName: String, subfolder: String) {
    val js2pDir = testProjectDir.resolve(targetDirectoryPrefix).resolve(executionName).resolve(subfolder)
    assertTrue(js2pDir.toFile().exists())
    assertTrue(js2pDir.resolve("Address.java").toFile().exists())
}
