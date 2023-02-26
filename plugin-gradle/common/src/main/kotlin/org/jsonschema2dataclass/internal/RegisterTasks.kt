package org.jsonschema2dataclass.internal

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.jsonschema2dataclass.internal.task.DEFAULT_SCHEMA_PATH
import java.nio.file.Path

typealias ProcessorRegistrationCallback = (
    suffixes: Map<String, String>,
    registerDependencies: (taskProvider: TaskProvider<out Task>, targetPath: Path?, dependsOn: Action<String>) -> Unit,
) -> Unit

interface GradlePluginRegistration {
    fun defaultSchemaPath(project: Project): Path {
        return defaultSchemaPathInternal(project) ?: project.file(DEFAULT_SCHEMA_PATH).toPath()
    }

    fun defaultSchemaPathInternal(project: Project): Path?
    fun registerPlugin(project: Project, callback: ProcessorRegistrationCallback)
}
