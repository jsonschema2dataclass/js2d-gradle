@file:Suppress("DEPRECATION")

package org.jsonschema2dataclass.js2p

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
// DEPRECATION: to support Gradle 6.0 - 7.0.2
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.util.GradleVersion

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

internal fun skipInputWhenEmpty(task: Task, sourceFiles: FileCollection) {
    val input = task.inputs.files(sourceFiles)
        .skipWhenEmpty()

    if (GradleVersion.current() >= GradleVersion.version("6.8")) {
        input.ignoreEmptyDirectories()
    }
}
