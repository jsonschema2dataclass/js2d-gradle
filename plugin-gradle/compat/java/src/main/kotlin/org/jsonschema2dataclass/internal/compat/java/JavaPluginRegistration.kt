package org.jsonschema2dataclass.internal.compat.java

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.util.GradleVersion
import org.jsonschema2dataclass.internal.GradlePluginRegistration
import org.jsonschema2dataclass.internal.ProcessorRegistrationCallback
import java.nio.file.Path

/**
 * Registers a processor task using AGP34 API
 */
class JavaPluginRegistration : GradlePluginRegistration {
    private fun mainSourceSet(project: Project): SourceSet =
        if (GradleVersion.current() < GradleVersion.version("7.1")) {
            obtainJavaSourceSetContainerV6(project)
        } else {
            obtainJavaSourceSetContainerV7(project)
        }

    override fun defaultSchemaPathInternal(project: Project): Path? =
        mainSourceSet(project)
            .output
            .resourcesDir
            ?.toPath()
            ?.resolve("json")

    override fun registerPlugin(project: Project, callback: ProcessorRegistrationCallback) {
        val javaSourceSet = mainSourceSet(project).java

        callback.invoke(
            mapOf(),
        ) { taskProvider: TaskProvider<out Task>, targetPath: Path?, dependsOn: Action<String> ->
            taskProvider.configure {
                dependsOn(project.tasks.named("processResources"))
            }

            dependsOn.execute("generateEffectiveLombokConfig")

            if (targetPath != null) {
                javaSourceSet.srcDirs(taskProvider)
            }
        }
    }
}

// TODO: split this out to have better compatibility
/**
 * Obtain java source sets in Gradle 6.0 - 7.0.2
 */
@Suppress("DEPRECATION")
private fun obtainJavaSourceSetContainerV6(project: Project): SourceSet =
    obtainSourceSetContainer(project.convention.plugins["java"]!!)
        .named("main")
        .get()

private fun obtainSourceSetContainer(value: Any): SourceSetContainer {
    val method = value::class.java.getDeclaredMethod("getSourceSets")
    return method.invoke(value) as SourceSetContainer
}

/**
 * Obtain java source sets in Gradle 7.3+.
 */
private fun obtainJavaSourceSetContainerV7(project: Project): SourceSet =
    project
        .extensions
        .getByType(JavaPluginExtension::class.java)
        .sourceSets
        .named("main")
        .get()
