package org.jsonschema2dataclass

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.apply
import org.gradle.util.GradleVersion
import org.jsonschema2dataclass.js2p.Js2pConfiguration
import org.jsonschema2dataclass.js2p.Js2pExtension
import org.jsonschema2dataclass.js2p.Js2pGenerationTask
import org.jsonschema2dataclass.js2p.Js2pWrapperTask
import org.jsonschema2dataclass.support.applyInternalAndroid
import org.jsonschema2dataclass.support.applyInternalJava
import org.jsonschema2dataclass.support.capitalized
import java.nio.file.Path
import java.util.*

internal const val MINIMUM_GRADLE_VERSION = "6.0"
internal const val TARGET_FOLDER_BASE = "generated/sources/js2d"
internal const val PLUGIN_ID = "org.jsonschema2dataclass"

internal const val EXTENSION_NAME = "jsonSchema2Pojo"
internal const val JS2D_CONFIGURATION_NAME = "jsonschema2dataclass"
internal const val JS2D_PLUGINS_CONFIGURATION_NAME = "jsonschema2dataclassPlugins"

internal const val JS2P_TASK_NAME = "generateJsonSchema2DataClass"
internal const val JS2P_GENERATOR_VERSION = "1.1.2"

private val configurationNameRegex = "[a-z][A-Za-z0-9_]*".toRegex()

private const val DEPRECATION_NO_EXECUTION =
    "No executions defined, behavior to with default execution has been deprecated and will be removed " +
        "in plugin $PLUGIN_ID version 5.0. " +
        "Please, consider follow migration guide to upgrade plugin properly"

@Suppress("unused")
class Js2dPlugin : Plugin<Project> {
    private val javaPlugins = listOf("java", "java-library")
    private val androidPlugins = listOf("com.android.application", "com.android.library")

    override fun apply(project: Project) {
        verifyGradleVersion()
        project.extensions.create(EXTENSION_NAME, Js2pExtension::class.java)
        val pluginExtension = project.extensions.getByType(Js2pExtension::class.java)
        pluginExtension.targetDirectoryPrefix.convention(project.layout.buildDirectory.dir(TARGET_FOLDER_BASE))
        val js2dConfiguration = createConfiguration(project, JS2D_CONFIGURATION_NAME)
        val js2dConfigurationPlugins = createConfiguration(project, JS2D_PLUGINS_CONFIGURATION_NAME)

        // TODO: refactor this
        js2dConfiguration.defaultDependencies {
            add(project.dependencies.create("org.jsonschema2pojo:jsonschema2pojo-core:$JS2P_GENERATOR_VERSION"))
        }

        // TODO: refactor this
        js2dConfigurationPlugins.defaultDependencies {
            add(project.dependencies.create("org.jsonschema2pojo:jsonschema2pojo-core:$JS2P_GENERATOR_VERSION"))
        }

        for (pluginId in javaPlugins) {
            project.plugins.withId(pluginId) {
                project.apply<Js2pJavaPlugin>()
            }
        }
        for (pluginId in androidPlugins) {
            project.plugins.withId(pluginId) {
                project.apply<Js2pAndroidPlugin>()
            }
        }

        project.afterEvaluate { // this can be reported only after evaluation
            if (pluginExtension.executions.size == 0) {
                project.logger.warn(DEPRECATION_NO_EXECUTION)
            }
        }
    }
}

internal class Js2pJavaPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val pluginExtension = project.extensions.getByType(Js2pExtension::class.java)
        applyInternalJava(pluginExtension, project)
    }
}

internal class Js2pAndroidPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val pluginExtension = project.extensions.getByType(Js2pExtension::class.java)
        applyInternalAndroid(pluginExtension, project)
    }
}

private fun createConfiguration(project: Project, name: String): Configuration {
    return project.configurations.maybeCreate(name).apply {
        isCanBeConsumed = false
        isCanBeResolved = true
        isVisible = false
    }
}

private const val TASK_DESCRIPTION_PREFIX = "Generates Java classes from a json schema using JsonSchema2Pojo"

private fun createTaskNameDescription(
    androidVariant: String?,
    configurationName: String?,
): Pair<String, String> {
    val androidVariantCapitalized = androidVariant?.capitalized()
    val configurationNameCapitalized = configurationName?.capitalized()
    val androidVariantSuffix = if (androidVariant.isNullOrEmpty()) "" else "For$androidVariantCapitalized"
    val androidVariantMessage = if (androidVariant.isNullOrEmpty()) "" else " for variant $androidVariantCapitalized"
    val configurationNameSuffix = if (configurationName.isNullOrEmpty()) "" else "Config$configurationNameCapitalized"
    val configurationNameMessage = if (configurationName.isNullOrEmpty()) "" else "for configuration $configurationName"

    return "$JS2P_TASK_NAME$androidVariantSuffix$configurationNameSuffix" to
        "$TASK_DESCRIPTION_PREFIX$configurationNameMessage$androidVariantMessage."
}

internal fun createJS2DTask(
    project: Project,
    extension: Js2pExtension,
    defaultSourcePath: Path?,
    androidVariant: String?,
    targetDirectorySuffix: String,
    excludeGeneratedOption: Boolean,
    postConfigure: (
        task: TaskProvider<out Js2pGenerationTask>,
        DirectoryProperty,
    ) -> Unit,
): TaskProvider<Js2pWrapperTask> {
    val (taskName, taskDescription) = createTaskNameDescription(androidVariant, null)
    val js2dTask = project.tasks.register(taskName, Js2pWrapperTask::class.java) {
        description = taskDescription
        group = "Build"
    }
    extension.executions.all {
        val configuration = this
        verifyConfigurationName(configuration.name)
        val targetPath = project.objects.directoryProperty()
        targetPath.set(extension.targetDirectoryPrefix.dir("${configuration.name}$targetDirectorySuffix"))
        val taskProvider = createJS2DTaskExecution(
            project,
            androidVariant,
            configuration,
            configuration.io.source.filter { it.exists() },
            targetPath,
            defaultSourcePath,
            excludeGeneratedOption,
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
    androidVariant: String?,
    configuration: Js2pConfiguration,
    source: FileCollection,
    targetPath: DirectoryProperty,
    defaultSourcePath: Path?,
    excludeGeneratedOption: Boolean,
): TaskProvider<out Js2pGenerationTask> {
    val (taskName, taskDescription) = createTaskNameDescription(androidVariant, configuration.name)

    copyConfiguration(configuration, excludeGeneratedOption, defaultSourcePath)
    return project.tasks.register(taskName, Js2pGenerationTask::class.java) {
        this.description = taskDescription
        this.group = "Build"
        this.configuration = configuration
        this.uuid = UUID.randomUUID()
        this.targetDirectory.set(targetPath)

        skipInputWhenEmpty(this, source)
        source.forEach { it.mkdirs() }
    }
}

private fun skipInputWhenEmpty(task: Task, sourceFiles: FileCollection) {
    val input = task.inputs.files(sourceFiles)
        .skipWhenEmpty()

    if (GradleVersion.current() >= GradleVersion.version("6.8")) {
        input.ignoreEmptyDirectories()
    }
}

private fun verifyGradleVersion() {
    if (GradleVersion.current() < GradleVersion.version(MINIMUM_GRADLE_VERSION)) {
        throw GradleException(
            "Plugin $PLUGIN_ID requires at least Gradle $MINIMUM_GRADLE_VERSION, " +
                "but you are using ${GradleVersion.current().version}",
        )
    }
}

private fun verifyConfigurationName(configurationName: String) {
    if (!configurationNameRegex.matches(configurationName)) {
        throw GradleException(
            "Plugin $PLUGIN_ID doesn't support configuration name \"$configurationName\" provided. " +
                "Please, rename to match regex \"$configurationNameRegex\"",
        )
    }
}

internal fun copyConfiguration(
    configuration: Js2pConfiguration,
    excludeGeneratedOption: Boolean,
    defaultSourcePath: Path?,
) {
    if (configuration.io.source.isEmpty) {
        configuration.io.source.setFrom(defaultSourcePath)
    }
    if (excludeGeneratedOption) {
        // Temporary fixes #71 and upstream issue #1212 by overriding Generated annotation.
        // Java 1.9+ Generated annotation is not compatible with AGP 7+
        configuration.klass.annotateGenerated.set(false)
    }
}
