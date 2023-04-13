package org.jsonschema2dataclass.internal.plugin.publishing

import basePluginExtension
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.attributes.DocsType
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.internal.JvmPluginsHelper
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.named
import org.jetbrains.dokka.gradle.DokkaTask

private const val DOKKA_TASK_NAME: String = "dokkaJavadoc"

fun applyDokka(project: Project, javaPluginExtension: JavaPluginExtension) {
    project.plugins.apply("org.jetbrains.dokka")

    setupDoc(project)
    javaPluginExtension.withDokkaJar(
        project,
        project.tasks.named(
            DOKKA_TASK_NAME,
        ),
    )
}

private fun setupDoc(project: Project) {
    project.tasks.named<DokkaTask>(DOKKA_TASK_NAME) {
        moduleName.set(this.project.basePluginExtension.archivesName)
        // TODO https://github.com/Kotlin/dokka/issues/1894
        dokkaSourceSets.configureEach {
            reportUndocumented.set(false)
        }
    }
}

/**
 * Attaches Dokka as "JavaDoc" provider
 *
 * @see [org.gradle.api.plugins.JavaPluginExtension.withJavadocJar]
 *     implementation for details
 */
private fun JavaPluginExtension.withDokkaJar(project: Project, artifactTask: TaskProvider<out Task>) {
    JvmPluginsHelper.createDocumentationVariantWithArtifact(
        JavaPlugin.JAVADOC_ELEMENTS_CONFIGURATION_NAME,
        null,
        DocsType.JAVADOC,
        emptyList(),
        sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).javadocJarTaskName,
        artifactTask,
        project as ProjectInternal,
    )
}
