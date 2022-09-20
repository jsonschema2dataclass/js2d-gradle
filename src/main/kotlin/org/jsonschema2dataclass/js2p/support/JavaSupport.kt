@file:Suppress("DEPRECATION")

package org.jsonschema2dataclass.js2p.support

// DEPRECATION: to support Gradle 6.0 - 7.0.2
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownTaskException
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.util.GradleVersion
import org.jsonschema2dataclass.js2p.Js2pExtension
import org.jsonschema2dataclass.js2p.createJS2DTask
import org.jsonschema2dataclass.js2p.setupConfigExecutions
import java.nio.file.Path

internal fun applyInternalJava(extension: Js2pExtension, project: Project) {

    val mainSourceSet = obtainJavaSourceSet(project)

    setupConfigExecutions(
        extension,
        getJavaJsonPath(mainSourceSet),
        false
    )

    val javaSourceSet = mainSourceSet.java
    val js2pTask = createJS2DTask(
        project,
        extension,
        "",
        ""
    ) { generationTaskProvider, targetPath ->

        @Suppress("UnstableApiUsage")
        project.tasks.withType(ProcessResources::class.java) {
            val processResources = this
            generationTaskProvider.configure {
                this.dependsOn(processResources)
            }
        }

        javaSourceSet.srcDirs(targetPath)
        javaSourceSet.sourceDirectories.plus(targetPath)
    }

    project.tasks.withType(JavaCompile::class.java) {
        this.dependsOn(js2pTask)
    }

    /* attach to Kotlin compilation task if exists */

    dependsOnJs2p(project, "compileKotlin", js2pTask)
    dependsOnJs2p(project, "generateEffectiveLombokConfig", js2pTask)
}

private fun dependsOnJs2p(project:Project, name: String, js2pTask: TaskProvider<Task>) {
    try {
        project.tasks.named(name) {
            this.dependsOn(js2pTask)
        }
    } catch (_: UnknownTaskException) {
    }
}

private fun getJavaJsonPath(sourceSet: SourceSet): Path? {
    return sourceSet
        .output
        .resourcesDir
        ?.toPath()
        ?.resolve("json")
}

private fun obtainJavaSourceSet(project: Project): SourceSet =
    if (GradleVersion.current() < GradleVersion.version("7.1")) {
        obtainJavaSourceSetContainerV6(project)
    } else {
        obtainJavaSourceSetContainerV7(project)
    }

/**
 * Obtain java source sets in Gradle 6.0 - 7.0.2
 */
private fun obtainJavaSourceSetContainerV6(project: Project): SourceSet =
    (project.convention.plugins["java"]!! as JavaPluginConvention)
        .sourceSets.named("main")
        .get()

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
