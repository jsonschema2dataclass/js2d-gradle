package org.jsonschema2dataclass.js2p

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.Logger
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.apply
import org.gradle.util.GradleVersion
import org.jsonschema2dataclass.js2p.support.applyInternalAndroid
import org.jsonschema2dataclass.js2p.support.applyInternalJava
import java.nio.file.Path
import java.util.*

internal const val EXTENSION_NAME = "jsonSchema2Pojo"
internal const val MINIMUM_GRADLE_VERSION = "6.0"
internal const val TARGET_FOLDER_BASE = "generated/sources/js2d"
internal const val TASK_NAME = "generateJsonSchema2DataClass"
internal const val PLUGIN_ID = "org.jsonschema2dataclass"
private val configurationNameRegex = "[a-z][A-Za-z0-9_]*".toRegex()
private const val DEPRECATION_PROPERTY_USAGE_MESSAGE =
    "Using property settings from the extension $EXTENSION_NAME is deprecated and will be removed " +
            "in plugin $PLUGIN_ID version 6.x. " +
            "Please, move all properties to defined executions."

private const val DEPRECATION_NO_EXECUTION =
    "No executions defined, behavior to with default execution has been deprecated and removed " +
            "in plugin $PLUGIN_ID version 5.0. " +
            "Please, consider follow migration guide to upgrade plugin properly"

@Suppress("unused")
class Js2pPlugin : Plugin<Project> {
    private val javaPlugins = listOf("java", "java-library")
    private val androidPlugins = listOf("com.android.application", "com.android.library")

    override fun apply(project: Project) {
        verifyGradleVersion()
        project.extensions.create(EXTENSION_NAME, Js2pExtension::class.java)
        val pluginExtension = project.extensions.getByType(Js2pExtension::class.java)
        pluginExtension.targetDirectoryPrefix.convention(project.layout.buildDirectory.dir(TARGET_FOLDER_BASE))

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

        project.afterEvaluate { // this can be reported only after evaluation
            if (pluginExtension.executions.size == 0) {
                project.logger.warn(DEPRECATION_NO_EXECUTION)
            }
        }
    }
}

internal class Js2pJavaPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val pluginExtension = project.extensions.getByType(Js2pExtension::class.java)
        applyInternalJava(pluginExtension, project)
    }
}

internal class Js2pAndroidPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val pluginExtension = project.extensions.getByType(Js2pExtension::class.java)
        applyInternalAndroid(pluginExtension, project)
    }
}

internal fun createTaskNameDescription(
    androidVariant: String?,
    configurationName: String?
): Pair<String, String> {

    val androidVariantSuffix = if (androidVariant == null) "" else "For${androidVariant.capitalize()}"
    val androidVariantMessage = if (androidVariant == null) "" else " for variant ${androidVariant.capitalize()}"
    val configurationNameSuffix = if (configurationName == null) "" else "Config${configurationName.capitalize()}"
    val configurationNameMessage = if (configurationName == null) "" else "for configuration $configurationName"

    return "$TASK_NAME$androidVariantSuffix$configurationNameSuffix" to
            "Generates Java classes from a json schema using JsonSchema2Pojo$configurationNameMessage$androidVariantMessage."
}

internal fun createJS2DTask(
    project: Project,
    extension: Js2pExtension,
    defaultSourcePath: Path?,
    androidVariant: String?,
    targetDirectorySuffix: String,
    excludeGeneratedOption: Boolean,
    postConfigure: (
        task: TaskProvider<out Js2pGenerationTask>,
        DirectoryProperty
    ) -> Unit,
): TaskProvider<Js2pWrapperTask> {
    val (taskName, taskDescription) = createTaskNameDescription(androidVariant, null)
    val js2dTask = project.tasks.register(taskName, Js2pWrapperTask::class.java) {
        description = taskDescription
        group = "Build"
    }
    extension.executions.all {
        val configuration = this
        verifyConfigurationName(configuration.name)
        val targetPath = project.objects.directoryProperty()
        targetPath.set(extension.targetDirectoryPrefix.dir("${configuration.name}$targetDirectorySuffix"))
        val taskProvider = createJS2DTaskExecution(
            project,
            androidVariant,
            configuration,
            extension,
            configuration.source.filter { it.exists() },
            targetPath,
            defaultSourcePath,
            excludeGeneratedOption
        )

        postConfigure(taskProvider, targetPath)
        js2dTask.configure {
            dependsOn(taskProvider)
        }
    }

    return js2dTask
}

private fun createJS2DTaskExecution(
    project: Project,
    androidVariant: String?,
    configuration: Js2pConfiguration,
    pluginExtension: Js2pExtension,
    source: FileCollection,
    targetPath: DirectoryProperty,
    defaultSourcePath: Path?,
    excludeGeneratedOption: Boolean,
): TaskProvider<out Js2pGenerationTask> {
    val (taskName, taskDescription) = createTaskNameDescription(androidVariant, configuration.name)

    copyConfiguration(project, pluginExtension, configuration, excludeGeneratedOption, defaultSourcePath)
    return project.tasks.register(taskName, Js2pGenerationTask::class.java) {
        this.description = taskDescription
        this.group = "Build"
        this.configuration = configuration
        this.uuid = UUID.randomUUID()
        this.targetDirectory.set(targetPath)

        skipInputWhenEmpty(this, source)
        source.forEach { it.mkdirs() }
    }
}

private fun skipInputWhenEmpty(task: Task, sourceFiles: FileCollection) {
    val input = task.inputs.files(sourceFiles)
        .skipWhenEmpty()

    if (GradleVersion.current() >= GradleVersion.version("6.8")) {
        input.ignoreEmptyDirectories()
    }
}

private fun verifyGradleVersion() {
    if (GradleVersion.current() < GradleVersion.version(MINIMUM_GRADLE_VERSION)) {
        throw GradleException(
            "Plugin $PLUGIN_ID requires at least Gradle $MINIMUM_GRADLE_VERSION, but you are using ${GradleVersion.current().version}"
        )
    }
}

private fun verifyConfigurationName(configurationName: String) {
    if (!configurationNameRegex.matches(configurationName)) {
        throw GradleException(
            "Plugin $PLUGIN_ID doesn't support configuration name \"$configurationName\" provided. " +
                    "Please, rename to match regex \"$configurationNameRegex\""
        )
    }
}

private fun <V> copyProperty(
    logger: Logger,
    left: Property<V>,
    right: Property<V>
) {
    if (!left.isPresent && right.isPresent) {
        left.set(right)
        logger.warn(DEPRECATION_PROPERTY_USAGE_MESSAGE)
    }
}

private fun <V> copyProperty(
    logger: Logger,
    left: SetProperty<V>,
    right: SetProperty<V>
) {
    if (!left.isPresent && right.isPresent) {
        left.set(right)
        logger.warn(DEPRECATION_PROPERTY_USAGE_MESSAGE)
    }
}

private fun <K, V> copyProperty(
    logger: Logger,
    left: MapProperty<K, V>,
    right: MapProperty<K, V>
) {
    if (!left.isPresent && right.isPresent) {
        left.set(right)
        logger.warn(DEPRECATION_PROPERTY_USAGE_MESSAGE)
    }
}

internal fun copyConfiguration(
    project: Project,
    extension: Js2pExtension,
    configuration: Js2pConfiguration,
    excludeGeneratedOption: Boolean,
    defaultSourcePath: Path?
) {
    if (configuration.source.isEmpty) {
        if (extension.source.isEmpty) {
            configuration.source.setFrom(defaultSourcePath)
        } else {
            configuration.source.setFrom(extension.source)
            project.logger.warn(DEPRECATION_PROPERTY_USAGE_MESSAGE)
        }
    }
    copyProperty(project.logger, configuration.annotationStyle, extension.annotationStyle)
    copyProperty(project.logger, configuration.classNamePrefix, extension.classNamePrefix)
    copyProperty(project.logger, configuration.classNameSuffix, extension.classNameSuffix)
    copyProperty(project.logger, configuration.customAnnotator, extension.customAnnotator)
    copyProperty(project.logger, configuration.customDatePattern, extension.customDatePattern)
    copyProperty(project.logger, configuration.customDateTimePattern, extension.customDateTimePattern)
    copyProperty(project.logger, configuration.customRuleFactory, extension.customRuleFactory)
    copyProperty(project.logger, configuration.customTimePattern, extension.customTimePattern)
    copyProperty(project.logger, configuration.dateTimeType, extension.dateTimeType)
    copyProperty(project.logger, configuration.dateType, extension.dateType)
    copyProperty(project.logger, configuration.fileExtensions, extension.fileExtensions)
    copyProperty(project.logger, configuration.fileFilter, extension.fileFilter)
    copyProperty(project.logger, configuration.formatDateTimes, extension.formatDateTimes)
    copyProperty(project.logger, configuration.formatDates, extension.formatDates)
    copyProperty(project.logger, configuration.formatTimes, extension.formatTimes)
    copyProperty(project.logger, configuration.formatTypeMapping, extension.formatTypeMapping)
    copyProperty(project.logger, configuration.generateBuilders, extension.generateBuilders)
    copyProperty(project.logger, configuration.includeAdditionalProperties, extension.includeAdditionalProperties)
    copyProperty(
        project.logger,
        configuration.includeAllPropertiesConstructor,
        extension.includeAllPropertiesConstructor
    )
    copyProperty(
        project.logger,
        configuration.includeConstructorPropertiesAnnotation,
        extension.includeConstructorPropertiesAnnotation
    )
    copyProperty(project.logger, configuration.includeConstructors, extension.includeConstructors)
    copyProperty(project.logger, configuration.includeCopyConstructor, extension.includeCopyConstructor)
    copyProperty(project.logger, configuration.includeDynamicAccessors, extension.includeDynamicAccessors)
    copyProperty(project.logger, configuration.includeDynamicBuilders, extension.includeDynamicBuilders)
    copyProperty(project.logger, configuration.includeDynamicGetters, extension.includeDynamicGetters)
    copyProperty(project.logger, configuration.includeDynamicSetters, extension.includeDynamicSetters)
    if (excludeGeneratedOption) {
        // Temporary fixes #71 and upstream issue #1212 (used Generated annotation is not compatible with AGP 7+)
        configuration.includeGeneratedAnnotation.set(false)
    } else {
        copyProperty(project.logger, configuration.includeGeneratedAnnotation, extension.includeGeneratedAnnotation)
    }
    copyProperty(project.logger, configuration.includeGetters, extension.includeGetters)
    copyProperty(project.logger, configuration.includeHashcodeAndEquals, extension.includeHashcodeAndEquals)
    copyProperty(project.logger, configuration.includeJsr303Annotations, extension.includeJsr303Annotations)
    copyProperty(project.logger, configuration.includeJsr305Annotations, extension.includeJsr305Annotations)
    copyProperty(
        project.logger,
        configuration.includeRequiredPropertiesConstructor,
        extension.includeRequiredPropertiesConstructor
    )
    copyProperty(project.logger, configuration.includeSetters, extension.includeSetters)
    copyProperty(project.logger, configuration.includeToString, extension.includeToString)
    copyProperty(project.logger, configuration.includeTypeInfo, extension.includeTypeInfo)
    copyProperty(project.logger, configuration.inclusionLevel, extension.inclusionLevel)
    copyProperty(project.logger, configuration.initializeCollections, extension.initializeCollections)
    copyProperty(project.logger, configuration.outputEncoding, extension.outputEncoding)
    copyProperty(project.logger, configuration.parcelable, extension.parcelable)
    copyProperty(project.logger, configuration.propertyWordDelimiters, extension.propertyWordDelimiters)
    copyProperty(project.logger, configuration.refFragmentPathDelimiters, extension.refFragmentPathDelimiters)
    copyProperty(project.logger, configuration.removeOldOutput, extension.removeOldOutput)
    copyProperty(project.logger, configuration.serializable, extension.serializable)
    copyProperty(project.logger, configuration.sourceSortOrder, extension.sourceSortOrder)
    copyProperty(project.logger, configuration.sourceType, extension.sourceType)
    copyProperty(project.logger, configuration.targetPackage, extension.targetPackage)
    copyProperty(project.logger, configuration.targetVersion, extension.targetVersion)
    copyProperty(project.logger, configuration.timeType, extension.timeType)
    copyProperty(project.logger, configuration.toStringExcludes, extension.toStringExcludes)
    copyProperty(project.logger, configuration.useBigDecimals, extension.useBigDecimals)
    copyProperty(project.logger, configuration.useBigIntegers, extension.useBigIntegers)
    copyProperty(project.logger, configuration.useDoubleNumbers, extension.useDoubleNumbers)
    copyProperty(project.logger, configuration.useInnerClassBuilders, extension.useInnerClassBuilders)
    copyProperty(project.logger, configuration.useJodaDates, extension.useJodaDates)
    copyProperty(project.logger, configuration.useJodaLocalDates, extension.useJodaLocalDates)
    copyProperty(project.logger, configuration.useJodaLocalTimes, extension.useJodaLocalTimes)
    copyProperty(project.logger, configuration.useLongIntegers, extension.useLongIntegers)
    copyProperty(project.logger, configuration.useOptionalForGetters, extension.useOptionalForGetters)
    copyProperty(project.logger, configuration.usePrimitives, extension.usePrimitives)
    copyProperty(project.logger, configuration.useTitleAsClassname, extension.useTitleAsClassname)
    copyProperty(project.logger, configuration.useJakartaValidation, extension.useJakartaValidation)
}
