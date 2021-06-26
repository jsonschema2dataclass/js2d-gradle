package com.github.js2d.ko.plugin

import com.github.js2d.Constants
import com.github.js2d.JsonSchema2dPluginConfiguration
import com.github.js2d.JsonSchemaExtension
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import java.nio.file.Path

class TaskUtils {
    companion object {
        private fun createJS2DTask(
            project: Project,
            extension: JsonSchemaExtension,
            taskNameSuffix: String,
            targetDirectorySuffix: String,
        ): Task {

            val js2dTask: Task = project.task(
                mapOf(
                    Pair("description", "Generates Java classes from a json schema using JsonSchema2Pojo."),
                    Pair("group", "Build"),
                ),
                "${Constants.TASK_NAME}${taskNameSuffix}"
            )
/*
            extension.executions.eachWithIndex { execution, execId ->
                GenerateFromJsonSchemaTask task = createJS2DTaskExecution (
                        project,
                taskNameSuffix,
                execId,
                execution.name,
                execution.source,
                extension.targetDirectoryPrefix,
                targetDirectorySuffix
                )
                postConfigure(task)
                js2dTask.dependsOn(task)
            }
            */
            return js2dTask
        }

        private fun createJS2DTaskExecution(
            project: Project,
            taskNameSuffix: String,
            executionId: Int,
            executionName: String,
            source: ConfigurableFileCollection,
            targetDirectoryPrefix: DirectoryProperty,
            targetDirectorySuffix: String
        ): GenerateFromJsonSchemaTask {

            val task: GenerateFromJsonSchemaTask = project.task(
                mapOf(
        //                    Pair("type", GenerateFromJsonSchemaTask.javaClass),
                    Pair("description", "Generates Java classes from a json schema using JsonSchema2Pojo. Execution ${executionId}"),
                    Pair("group", "build")
                ),
                "${Constants.TASK_NAME}${executionId}${taskNameSuffix}"
            ) as GenerateFromJsonSchemaTask

            task.execution = executionId
            task.sourceFiles.setFrom(source)
            task.targetDirectory.set(
                targetDirectoryPrefix.dir("${executionName}${targetDirectorySuffix}")
            )
            task.inputs.files({ task.sourceFiles.filter({ it.exists() }) })
                .skipWhenEmpty()
            task.outputs.dir(task.targetDirectory)
            return task
        }

        private fun setupConfigExecutions(
            objectFactory: ObjectFactory, config: JsonSchemaExtension, path: Path, excludeGenerated: Boolean
        ) {
            if (config.source.isEmpty()) {
                config.source.from(path)
            }
            if (config.executions.isEmpty()) {
                config.executions.add(JsonSchema2dPluginConfiguration("main", objectFactory))
            }

            for (execution: JsonSchema2dPluginConfiguration in config.executions) {
                if (execution.source.isEmpty()) {
                    execution.source.from(config.source)
                }
                if (excludeGenerated) {
                    execution.includeGeneratedAnnotation = false
                    // Temporary fixes #71 and upstream issue #1212 (used Generated annotation is not compatible with AGP 7+)
                }
            }
        }
    }
}