package com.github.js2d.plugin

import om.github.js2d.plugin.JsonSchemaExtension
import om.github.js2d.plugin.SetupConfigExecutions
import om.github.js2d.plugin.TaskCreator
import org.gradle.api.Project

import java.nio.file.Path
import java.nio.file.Paths

class PluginRegisterAndroid {
    static void applyAndroid(JsonSchemaExtension extension, Project project,
                             SetupConfigExecutions setupConfigExecutions, TaskCreator taskCreator) {
        setupConfigExecutions.apply(
                project.objects,
                extension,
                getAndroidJsonPath(project),
                System.getProperty('java.specification.version').toFloat() >= 9
        )

        def variants = obtainAndroidVariants(project)
        variants.all { variant ->
            def task = taskCreator.apply(
                    project,
                    extension,
                    "For${variant.name.capitalize()}",
                    "${variant.flavorName}/${variant.buildType.name}/",
                    { execution ->
                        variant.registerJavaGeneratingTask(execution, execution.targetDirectory.get().asFile)
                    }
            )
            variant.registerJavaGeneratingTask(task)
        }
    }

    private static Path getAndroidJsonPath(Project project) {
        def sets
        if (project.android.hasProperty('sourceSets')) {
            sets = project.android.sourceSets
        } else {
            sets = project.android.sourceSetsContainer
        }

        def main = sets.find { it.name.startsWith("main") }
        String mainSource = main.resources.source[0]
        return Paths.get(mainSource, 'json')
    }

    private static def obtainAndroidVariants(Project project) {
        if (project.android.hasProperty('applicationVariants')) {
            return project.android.applicationVariants
        } else if (project.android.hasProperty('libraryVariants')) {
            return project.android.libraryVariants
        } else {
            throw new IllegalStateException('Android project must have applicationVariants or libraryVariants!')
        }
    }

}
