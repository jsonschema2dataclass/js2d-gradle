package org.jsonschema2dataclass.internal

import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownTaskException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskProvider
import org.gradle.util.GradleVersion
import org.jsonschema2dataclass.ext.Js2pConfiguration
import org.jsonschema2dataclass.internal.compat.kotlin.capitalized
import org.jsonschema2dataclass.internal.task.JS2D_TASK_NAME
import org.jsonschema2dataclass.internal.task.JS2P_TOOL_NAME
import java.nio.file.Path
import java.util.UUID

private const val BASE_TASK_DESCRIPTION = "Generates Java models from a schema"

internal fun defaultConfigurationSettings(
    configurations: NamedDomainObjectCollection<Js2pConfiguration>,
    defaultSourceFiles: Path,
    disableAnnotateGenerated: Boolean,
) {
    configurations.configureEach {
        if (io.source.isEmpty) {
//            registration.defaultSchemaPath(project) ?: project.files(DEFAULT_SCHEMA_PATH),
            io.source.setFrom(defaultSourceFiles)
        }
        if (disableAnnotateGenerated) {
            klass.annotateGenerated.set(false)
        }
    }
}

private typealias RegisterDependenciesCallback = (
    taskProvider: TaskProvider<out Task>,
    targetPath: Path?,
    dependsOn: Action<String>,
) -> Unit

internal fun <ConfigClass : Named> registrationTasksMachinery(
    project: Project,
    registration: GradlePluginRegistration,
    targetDirectoryPrefix: DirectoryProperty,
    configurations: NamedDomainObjectCollection<ConfigClass>,
    processor: Js2dProcessor<ConfigClass>,
) {
    generateTasks(
        project,
        registration,
        configurations,
    ) { configuration, suffixes, callback ->
        // create a task for a configuration
        val (taskName, taskDescription) = createTaskNameDescription(
            configuration.name,
            suffixes,
            true,
            processor.toolNameForTask(),
        )
        val task = project.tasks.register(taskName, processor.generatorTaskClass()) {
            description = taskDescription
            this.description = taskDescription
            this.group = "Build"
            this.configuration = configuration
            this.uuid = UUID.randomUUID()
            this.targetDirectory.set(targetDirectoryPrefix.dir(configuration.name))

            // TODO: ask processor to extract source file collection from configuration
            val source = (configuration as Js2pConfiguration).io.source

            val newSource = source.filter { it.exists() && (!it.isDirectory || it.list()?.isNotEmpty() == true) }
            newSource.forEach { it.mkdirs() }
            skipInputWhenEmpty(this, newSource)
        }

        callback(task, targetDirectoryPrefix.asFile.get().toPath()) {
            dependsOn(project, task, this)
        }

        task
    }
}

@Suppress("SameParameterValue")
private fun createPrimaryTask(taskName: String, taskDescription: String, project: Project): TaskProvider<out Task> =
    project.tasks.register(taskName) {
        description = taskDescription
        group = "Build"
    }

private typealias SubtaskGeneratorFunction<T> = (
    configuration: T,
    suffixes: Map<String, String>,
    callback: RegisterDependenciesCallback,
) -> TaskProvider<out Task>

private fun <T : Named> generateTasks(
    project: Project,
    registration: GradlePluginRegistration,
    configurations: NamedDomainObjectCollection<T>,
    createSubTask: SubtaskGeneratorFunction<T>,
) {
    // create primary task first to declare task dependencies
    val primaryTask = createPrimaryTask(JS2D_TASK_NAME, BASE_TASK_DESCRIPTION, project)

    registration.registerPlugin(project) { suffixes: Map<String, String>, callback: RegisterDependenciesCallback ->
        callback(primaryTask, null) { // register the main task
            dependsOn(project, primaryTask, this)
        }
        configurations.configureEach {
            val taskForConfiguration = createSubTask(this, suffixes, callback)
            primaryTask.configure { dependsOn(taskForConfiguration) }
        }
    }
}

private fun skipInputWhenEmpty(task: Task, sourceFiles: FileCollection) {
    val input = task.inputs.files(sourceFiles)
        .skipWhenEmpty()

    if (GradleVersion.current() >= GradleVersion.version("6.8")) {
        input.ignoreEmptyDirectories()
    }
}

internal fun createTaskNameDescription(
    executionName: String,
    suffixes: Map<String, String>,
    taskNameCompatibleWith5: Boolean,
    processorName: Pair<String, String>,
): Pair<String, String> {
    val parts = generateSuffixes(processorName, executionName, suffixes, taskNameCompatibleWith5)

    val name = JS2D_TASK_NAME.plus(parts.joinToString("") { it.first })
    val description = parts.joinToString(" ") { it.second }

    return name to description
}

private fun generateSuffixes(
    processorName: Pair<String, String>,
    executionName: String,
    suffixes: Map<String, String>,
    taskNameCompatibleWith5: Boolean,
) = sequence {
    val toolNameCompat5x = processorName.first == JS2P_TOOL_NAME && taskNameCompatibleWith5
    val toolNamePart = if (toolNameCompat5x) "" else processorName.first.capitalized()

    yield(toolNamePart to processorName.second)

    val partNameCompat5x = suffixes.size < 2 && taskNameCompatibleWith5 // maintain backward compatibility
    yieldAll(suffixes.asSequence().map { generatePart(it.toPair(), partNameCompat5x) })
    yield("Config${executionName.capitalized()}" to "for configuration $executionName")
}

internal fun generatePart(
    // "variant" to "release"
    // "flavor" to "sweet"
    suffixes: Pair<String, String>,
    taskNameCompatibleWith5: Boolean,
): Pair<String, String> {
    val keyCapitalized = if (taskNameCompatibleWith5) { // maintain backward compatibility with 5.0.0
        ""
    } else {
        suffixes.first.capitalized()
    }
    val valueCapitalized = suffixes.second.capitalized()

    return "For$keyCapitalized$valueCapitalized" to "for ${suffixes.first} ${suffixes.second}"
}

private fun dependsOn(project: Project, task: TaskProvider<out Task>, taskName: String) {
    try {
        project.tasks.named(taskName).configure { dependsOn(task) }
    } catch (_: UnknownTaskException) {
        project.tasks.whenObjectAdded {
            // add it later
            if (this.name == taskName) {
                this.dependsOn(task)
            }
        }
    }
}
