@file:Suppress("DEPRECATION")

package org.jsonschema2dataclass.js2p.support

// DEPRECATION: to support Gradle 6.0 - 7.0.2
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
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

    val js2pTask = createJS2DTask(
        project,
        extension,
        "",
        ""
    ) { generationTaskProvider, targetPath ->

        @Suppress("UnstableApiUsage")
        project.tasks.withType(ProcessResources::class.java) {
            generationTaskProvider.configure { generationTask ->
                generationTask.dependsOn(it)
            }
        }
        mainSourceSet.configure {
            it.java.srcDir(targetPath)
            it.java.sourceDirectories.plus(targetPath)
        }
    }
    project.tasks.withType(JavaCompile::class.java) {
        it.dependsOn(js2pTask)
    }
}

// TODO: reimplement this in another way
private fun getJavaJsonPath(@Suppress("UNUSED_PARAMETER") sourceSet: NamedDomainObjectProvider<SourceSet>): Path? {
    return null
 /*   return sourceSet.configure {
            return@configure it.output
                .resourcesDir
                ?.toPath()
                ?.resolve("json")
        }*/
}

internal fun obtainJavaSourceSet(project: Project): NamedDomainObjectProvider<SourceSet> =
    if (GradleVersion.current() < GradleVersion.version("7.1")) {
        obtainJavaSourceSetContainerV6(project)
    } else {
        obtainJavaSourceSetContainerV7(project)
    }.named(SourceSet.MAIN_SOURCE_SET_NAME)

/**
 * Obtain java source sets in Gradle 6.0 - 7.0.2
 */
private fun obtainJavaSourceSetContainerV6(project: Project): SourceSetContainer =
    (project.convention.plugins["java"]!! as JavaPluginConvention).sourceSets

/**
 * Obtain java source sets in Gradle 7.3+.
 */
private fun obtainJavaSourceSetContainerV7(project: Project): SourceSetContainer =
    project
        .extensions
        .getByType(JavaPluginExtension::class.java)
        .sourceSets
