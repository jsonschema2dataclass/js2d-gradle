package org.jsonschema2dataclass.js2p

// DEPRECATION: to support Gradle 6.0 - 7.0.2
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.util.GradleVersion
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

        project.tasks.withType(ProcessResources::class.java) {
            generationTaskProvider.configure { generationTask ->

                generationTask.dependsOn(it)
            }
        }

        javaSourceSet.srcDirs(targetPath)
        javaSourceSet.sourceDirectories.plus(targetPath)
    }
    project.tasks.withType(JavaCompile::class.java) {
        it.dependsOn(js2pTask)
    }
}

private fun getJavaJsonPath(sourceSet: SourceSet): Path? {
    return sourceSet
        .output
        .resourcesDir
        ?.toPath()
        ?.resolve("json")
}

internal fun obtainJavaSourceSet(project: Project): SourceSet =
    if (GradleVersion.current() < GradleVersion.version("7.1")) {
        obtainJavaSourceSetContainerV6(project)
    } else {
        obtainJavaSourceSetContainerV7(project)
    }

/**
 * Obtain java source sets in Gradle 6.0 - 7.0.2
 */
@Suppress("DEPRECATION") // Deprecation is supposed to be here
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
