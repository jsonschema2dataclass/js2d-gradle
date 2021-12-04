package org.jsonschema2dataclass.js2p

import com.android.build.gradle.options.BooleanOption
import com.android.build.gradle.options.ProjectOptionService
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.util.GradleVersion
import java.nio.file.Path
import java.util.UUID

internal const val MINIMUM_GRADLE_VERSION = "6.0"
internal const val TARGET_FOLDER_BASE = "generated/sources/js2d"
internal const val DEFAULT_EXECUTION_NAME = "main"
internal const val TASK_NAME = "generateJsonSchema2DataClass"
internal const val PLUGIN_ID = "org.jsonschema2dataclass"

@Suppress("unused")
class Js2pPlugin : Plugin<Project> {
    private val javaPlugins = listOf(JavaPlugin::class.java, JavaLibraryPlugin::class.java)
    private val androidPlugins = listOf("com.android.application", "com.android.library")

    override fun apply(project: Project) {
        verifyGradleVersion()

        project.extensions.create("jsonSchema2Pojo", Js2pExtension::class.java)
        val pluginExtension = project.extensions.getByType(Js2pExtension::class.java)
        pluginExtension.targetDirectoryPrefix.convention(project.layout.buildDirectory.dir(TARGET_FOLDER_BASE))
        if (javaPlugins.any { project.plugins.hasPlugin(it) }) {
            project.afterEvaluate {
                applyInternalJava(pluginExtension, project)
            }
        } else if (androidPlugins.any { project.plugins.hasPlugin(it) }) {
            applyInternalAndroid(pluginExtension, project)
        } else {
            throw ProjectConfigurationException("$TASK_NAME: Java or Android plugin required", listOf())
        }
    }
}

internal fun setupConfigExecutions(
    extension: Js2pExtension,
    defaultSourcePath: Path?,
    excludeGeneratedOption: Boolean,
) {
    if (extension.source.isEmpty) {
        extension.source.from(defaultSourcePath)
    }
    if (extension.executions.isEmpty()) {
        extension.executions.create(DEFAULT_EXECUTION_NAME)
    }
    extension.executions.forEach { config: Js2pConfiguration ->
        if (config.source.isEmpty) {
            config.source.from(extension.source)
        }

        // Temporary fixes #71 and upstream issue #1212 (used Generated annotation is not compatible with AGP 7+)
        if (excludeGeneratedOption) {
            config.includeGeneratedAnnotation.set(false)
        }
    }
}

internal fun applyInternalAndroid(extension: Js2pExtension, project: Project) {
    val optionService = ProjectOptionService.RegistrationAction(project).execute().get()

    if (optionService.projectOptions.get(BooleanOption.USE_NEW_DSL_INTERFACES)) {
        applyInternalAndroidNewDSL(extension, project)
    } else {
        applyInternalAndroidHistorical(extension, project)
    }
}

internal fun createJS2DTask(
    project: Project,
    extension: Js2pExtension,
    taskNameSuffix: String,
    targetDirectorySuffix: String,
    postConfigure: (task: TaskProvider<out Js2pGenerationTask>, Provider<Directory>) -> Unit,
): TaskProvider<Task> {

    val js2dTask = project.tasks.register("${TASK_NAME}$taskNameSuffix", Task::class.java) {
        it.description = "Generates Java classes from a json schema using JsonSchema2Pojo."
        it.group = "Build"
    }

    extension.executions.forEachIndexed { configurationId, configuration ->
        val targetPath = extension.targetDirectoryPrefix.dir("${configuration.name}$targetDirectorySuffix")
        val taskProvider = createJS2DTaskExecution(
            project,
            configurationId,
            taskNameSuffix,
            configuration.name,
            configuration.source,
            targetPath,
        )

        postConfigure(taskProvider, targetPath)
        js2dTask.configure {
            it.dependsOn(taskProvider)
        }
    }

    return js2dTask
}

// TODO: use TaskProvider, e.g. tasks.register() {}
private fun createJS2DTaskExecution(
    project: Project,
    configurationId: Int,
    taskNameSuffix: String,
    configurationName: String,
    source: ConfigurableFileCollection,
    targetPath: Provider<Directory>,
): TaskProvider<out Js2pGenerationTask> {
    val taskName = "${TASK_NAME}${configurationId}$taskNameSuffix"
    return project.tasks.register(taskName, Js2pGenerationTask::class.java) { task ->
        task.description = "Generates Java classes from a json schema using JsonSchema2Pojo for configuration $configurationName"
        task.group = "Build"

        task.sourceFiles.setFrom(source.filter { it.exists() })
        task.configurationName = configurationName
        task.uuid = UUID.randomUUID()
        task.targetDirectory.set(targetPath)

        skipInputWhenEmpty(task, task.sourceFiles)

        task.sourceFiles.forEach { it.mkdirs() }
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
