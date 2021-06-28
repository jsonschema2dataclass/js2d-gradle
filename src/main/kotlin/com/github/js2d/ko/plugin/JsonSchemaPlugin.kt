package com.github.js2d.ko.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException

import com.github.js2d.ko.plugin.Constants.Companion.TARGET_FOLDER_BASE
import com.github.js2d.ko.plugin.Constants.Companion.TASK_NAME

class JsonSchemaPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        verifyGradleVersion()

        project.extensions.create("jsonSchema2Pojo", JsonSchemaExtension::class.java)
        val extension:JsonSchemaExtension = project.extensions.getByType(JsonSchemaExtension::class.java)
        extension.targetDirectoryPrefix.convention(project.layout.buildDirectory.dir(TARGET_FOLDER_BASE))
        project.afterEvaluate {
            if (project.plugins.hasPlugin("java")) {
                applyJava(extension, project)
            } else if (project.plugins.hasPlugin("com.android.application") || project.plugins.hasPlugin("com.android.library")) {
                applyAndroid(extension, project)
            } else {
                project.plugins.forEach{
                    project.logger.error(it.javaClass.name)
                }
                throw ProjectConfigurationException("${TASK_NAME}: Java or Android plugin required", List.of())
            }
        }
    }
}