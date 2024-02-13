package org.jsonschema2dataclass.internal.plugin.lib

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.apply
import org.gradle.util.GradleVersion
import versionCatalogs
import java.io.File
import java.util.Properties

private const val PROCESSOR_VERSION_EXTENSION = "processorVersion"
private const val PROCESSOR_VERSION_CATALOG = "processors"
private const val DEFAULT_TARGET_FOLDER_BASE = "generated/sources/processorVersion"
private const val DEFAULT_TARGET_FILENAME = "processor.properties"

private val javaPlugins = listOf("java", "java-library")

/**
 * Plugin to include processor path to properties file
 */
@Suppress("unused")
class ProcessorVersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        for (pluginId in javaPlugins) {
            project.plugins.withId(pluginId) {
                project.apply<ProcessorVersionPluginImpl>()
            }
        }
    }
}

class ProcessorVersionPluginImpl : Plugin<Project> {
    private fun mainSourceSet(project: Project): SourceSet =
        if (GradleVersion.current() < GradleVersion.version("7.1")) {
            obtainJavaSourceSetContainerV6(project)
        } else {
            obtainJavaSourceSetContainerV7(project)
        }

    override fun apply(project: Project) {
        val extension = project.extensions.create(PROCESSOR_VERSION_EXTENSION, ProcessorVersionPExtension::class.java)
        extension.fieldName.convention("processor")
        extension.outputFolder.convention(project.layout.buildDirectory.dir(DEFAULT_TARGET_FOLDER_BASE))
        extension.filename.convention(DEFAULT_TARGET_FILENAME)

        val task = project.tasks.register("processorVersion", ProcessorVersionGeneratorTask::class.java) {
            this.library.set(extension.library)
            this.fieldName.set(extension.fieldName)
            this.filename.set(extension.filename)
            this.outputFolder.set(extension.outputFolder)
        }

        project.tasks.named("processResources").configure {
            dependsOn(task)
        }
        project.tasks.named("compileKotlin").configure {
            dependsOn(task)
        }

        task.configure {
            val javaSourceSet = mainSourceSet(project).resources
            javaSourceSet.srcDirs(task)
        }
    }
}

abstract class ProcessorVersionPExtension {
    @get:Input
    abstract val library: Property<String>

    @get:Input
    @get:Optional
    abstract val fieldName: Property<String>

    @get:Input
    @get:Optional
    abstract val filename: Property<String>

    @get:OutputFile
    @get:Optional
    abstract val outputFolder: DirectoryProperty
}

abstract class ProcessorVersionGeneratorTask : DefaultTask() {
    @get:Input
    abstract val library: Property<String>

    @get:Input
    abstract val fieldName: Property<String>

    @get:Input
    @get:Optional
    abstract val filename: Property<String>

    @get:OutputDirectory
    abstract val outputFolder: DirectoryProperty

    @TaskAction
    fun action() {
        if (filename.get().contains("..")) {
            throw GradleException("filename path must not contain `..`")
        }

        val dependency: MinimalExternalModuleDependency = project
            .versionCatalogs
            .named(PROCESSOR_VERSION_CATALOG)
            .findLibrary(library.get())
            .orElseThrow {
                GradleException(
                    "Unable resolve library for ${library.get()} in catalog $PROCESSOR_VERSION_CATALOG",
                )
            }
            .get()

        val requiredVersion = dependency.versionConstraint.requiredVersion
        val group = dependency.module.group
        val name = dependency.module.name

        val identifier = "$group:$name:$requiredVersion"

        val outputFile = this.outputFolder.get().asFile.resolve(filename.get())

        if (project.logger.isDebugEnabled) {
            project.logger.debug("Found library with identifier `$identifier`, writing to ${outputFile.absolutePath}")
        }
        outputFile.ensureParentDirsCreated()

        outputFile.writer().apply {
            val p = Properties()
            p[fieldName.get()] = identifier
            p.store(this, null)
        }
    }
}

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

private fun File.ensureParentDirsCreated() {
    val parentFile = parentFile
    if (!parentFile.exists()) {
        check(parentFile.mkdirs()) {
            "Cannot create parent directories for $this"
        }
    }
}
