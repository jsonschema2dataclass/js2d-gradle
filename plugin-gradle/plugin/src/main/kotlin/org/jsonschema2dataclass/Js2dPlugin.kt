package org.jsonschema2dataclass

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.kotlin.dsl.apply
import org.jsonschema2dataclass.ext.Js2pExtension
import org.jsonschema2dataclass.internal.*
import org.jsonschema2dataclass.internal.compat.android.androidProcessorRegistrationSelector
import org.jsonschema2dataclass.internal.compat.java.JavaPluginRegistration
import org.jsonschema2dataclass.internal.defaultConfigurationSettings
import org.jsonschema2dataclass.internal.js2p.Js2pProcessor
import org.jsonschema2dataclass.internal.registrationTasksMachinery
import org.jsonschema2dataclass.internal.task.*
import org.jsonschema2dataclass.internal.verifyExecutionNames
import org.jsonschema2dataclass.internal.verifyExecutions
import org.jsonschema2dataclass.internal.verifyGradleVersion
import java.util.*

private val processors: MutableList<Js2dProcessor<*>> = mutableListOf()

@Suppress("unused")
class Js2dPlugin : Plugin<Project> {
    private val javaPlugins = listOf("java", "java-library")
    private val androidPlugins = listOf("com.android.application", "com.android.library")

    override fun apply(project: Project) {
        verifyGradleVersion()

        val js2pExtension = project.extensions.create(JS2P_EXTENSION_NAME, Js2pExtension::class.java)
        js2pExtension.targetDirectoryPrefix.convention(project.layout.buildDirectory.dir(DEFAULT_TARGET_FOLDER_BASE))

        val js2dConfiguration = createConfiguration(project, JS2D_CONFIGURATION_NAME)
        val js2dConfigurationPlugins = createConfiguration(project, JS2D_PLUGINS_CONFIGURATION_NAME)

        verifyExecutionNames(js2pExtension.executions)
        verifyExecutions(project, js2pExtension.executions)

        val processor = obtainProcessor()
        processors.add(processor)

        processor.toolingMinimalDependencies(project, js2dConfiguration)
        processor.toolingMinimalDependencies(project, js2dConfigurationPlugins)

        for (pluginId in javaPlugins) {
            project.plugins.withId(pluginId) {
                project.apply<Js2pJavaPlugin>()
            }
        }
        for (pluginId in androidPlugins) {
            project.plugins.withId(pluginId) {
                project.apply<Js2pAndroidPlugin>()
            }
        }
    }
}

internal class Js2pJavaPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        applyPlugin(project, JavaPluginRegistration(), false)
    }
}

internal class Js2pAndroidPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        applyPlugin(project, androidProcessorRegistrationSelector(), true)
    }
}

private fun applyPlugin(project: Project, registration: GradlePluginRegistration, disableGeneratedAnnotation: Boolean) {
    val js2pExtension = project.extensions.getByType(Js2pExtension::class.java)

    defaultConfigurationSettings(
        js2pExtension.executions,
        registration.defaultSchemaPath(project),
        disableGeneratedAnnotation,
    )

    val js2pProcessor: Js2pProcessor = processors.first() as Js2pProcessor

    registrationTasksMachinery(
        project,
        registration,
        js2pExtension.targetDirectoryPrefix,
        js2pExtension.executions,
        js2pProcessor,
    )
}

private fun createConfiguration(project: Project, name: String): Configuration {
    return project.configurations.maybeCreate(name).apply {
        isCanBeConsumed = false
        isCanBeResolved = true
        isVisible = false
    }
}

private fun obtainProcessor(): Js2dProcessor<*> = Js2pProcessor()
