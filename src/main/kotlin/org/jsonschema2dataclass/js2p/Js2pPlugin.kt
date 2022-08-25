package org.jsonschema2dataclass.js2p

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskProvider
import org.gradle.util.GradleVersion
import org.jsonschema2dataclass.js2p.support.applyInternalAndroid
import org.jsonschema2dataclass.js2p.support.applyInternalJava
import java.nio.file.Path
import java.util.UUID

internal const val MINIMUM_GRADLE_VERSION = "6.0"
internal const val TARGET_FOLDER_BASE = "generated/sources/js2d"
internal const val DEFAULT_EXECUTION_NAME = "main"
internal const val TASK_NAME = "generateJsonSchema2DataClass"
internal const val PLUGIN_ID = "org.jsonschema2dataclass"

@Suppress("unused")
class Js2pPlugin : Plugin<Project> {

    private val javaPlugins = listOf("java", "java-library")
    private val androidPlugins = listOf("com.android.application", "com.android.library")

    override fun apply(project: Project) {
        verifyGradleVersion()
        project.extensions.create("jsonSchema2Pojo", Js2pExtension::class.java)
        val pluginExtension = project.extensions.getByType(Js2pExtension::class.java)
        pluginExtension.targetDirectoryPrefix.convention(project.layout.buildDirectory.dir(TARGET_FOLDER_BASE))

        for (pluginId in listOf("java", "java-library")) {
            project.plugins.withId(pluginId) {
                project.afterEvaluate {
                    applyInternalJava(pluginExtension, project)
                }
            }
        }
        for (pluginId in androidPlugins) {
            project.plugins.withId(pluginId) {
                project.afterEvaluate {
                    applyInternalAndroid(pluginExtension, project)
                }
            }
        }
    }
}

internal fun setupConfigExecutions(
    extension: Js2pExtension,
    defaultSourcePath: Path?,
    excludeGeneratedOption: Boolean,
) {
    if (extension.source.isEmpty && defaultSourcePath != null) {
        extension.source.setFrom(defaultSourcePath.toFile())
    }

    if (extension.executions.isEmpty()) {
        extension.executions.create(DEFAULT_EXECUTION_NAME)
    }

    extension.executions.forEach { configuration: Js2pConfiguration ->
        if (configuration.source.isEmpty) {
            configuration.source.setFrom(extension.source)
        }

        // Temporary fixes #71 and upstream issue #1212 (used Generated annotation is not compatible with AGP 7+)
        if (excludeGeneratedOption) {
            configuration.includeGeneratedAnnotation.set(false)
        }
    }
}

internal fun createJS2DTask(
    project: Project,
    extension: Js2pExtension,
    taskNameSuffix: String,
    targetDirectorySuffix: String,
    postConfigure: (
        task: TaskProvider<out Js2pGenerationTask>,
        DirectoryProperty
    ) -> Unit,
): TaskProvider<Task> {

    val js2dTask = project.tasks.register("${TASK_NAME}$taskNameSuffix", Task::class.java) {
        description = "Generates Java classes from a json schema using JsonSchema2Pojo."
        group = "Build"
    }

    extension.executions.forEachIndexed { configurationId, configuration ->
        val targetPath = project.objects.directoryProperty()
        targetPath.set(extension.targetDirectoryPrefix.dir("${configuration.name}$targetDirectorySuffix"))
        val taskProvider = createJS2DTaskExecution(
            project,
            configurationId,
            taskNameSuffix,
            configuration.name,
            configuration.source.filter { it.exists() },
            targetPath,
        )

        postConfigure(taskProvider, targetPath)
        js2dTask.configure {
            dependsOn(taskProvider)
        }
    }

    return js2dTask
}

private fun createJS2DTaskExecution(
    project: Project,
    configurationId: Int,
    taskNameSuffix: String,
    configurationName: String,
    source: FileCollection,
    targetPath: DirectoryProperty,
): TaskProvider<out Js2pGenerationTask> {
    val taskName = "${TASK_NAME}${configurationId}$taskNameSuffix"
    return project.tasks.register(taskName, Js2pGenerationTask::class.java) {
        this.description =
            "Generates Java classes from a json schema using JsonSchema2Pojo for configuration $configurationName"
        this.group = "Build"

        this.configurationName = configurationName
        this.uuid = UUID.randomUUID()
        this.targetDirectory.set(targetPath)

        skipInputWhenEmpty(this, source)
        source.forEach { it.mkdirs() }
    }
}

private fun verifyGradleVersion() {
    if (GradleVersion.current() < GradleVersion.version(MINIMUM_GRADLE_VERSION)) {
        throw GradleException("Plugin $PLUGIN_ID requires at least Gradle $MINIMUM_GRADLE_VERSION, but you are using ${GradleVersion.current().version}")
    }
}

private fun skipInputWhenEmpty(task: Task, sourceFiles: FileCollection) {
    val input = task.inputs.files(sourceFiles)
        .skipWhenEmpty()

    if (GradleVersion.current() >= GradleVersion.version("6.8")) {
        input.ignoreEmptyDirectories()
    }
}
