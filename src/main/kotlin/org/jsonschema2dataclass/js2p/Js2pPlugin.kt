package org.jsonschema2dataclass.js2p

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
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

@Suppress("unused")
class Js2pPlugin : Plugin<Project> {
    private val javaPlugins = listOf("java", "java-library")
    private val androidPlugins = listOf("com.android.application", "com.android.library")

    override fun apply(project: Project) {
        verifyGradleVersion()
        project.extensions.create(EXTENSION_NAME, Js2pExtension::class.java)
        val pluginExtension = project.extensions.getByType(Js2pExtension::class.java)
        pluginExtension.targetDirectoryPrefix.convention(project.layout.buildDirectory.dir(TARGET_FOLDER_BASE))

        for (pluginId in listOf("java", "java-library")) {
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

    copyConfiguration(pluginExtension, configuration, excludeGeneratedOption, defaultSourcePath)
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
                    "Please rename to match regex \"$configurationNameRegex\""
        )
    }
}

private fun skipInputWhenEmpty(task: Task, sourceFiles: FileCollection) {
    val input = task.inputs.files(sourceFiles)
        .skipWhenEmpty()

    if (GradleVersion.current() >= GradleVersion.version("6.8")) {
        input.ignoreEmptyDirectories()
    }
}

private fun <V> copyProperty(
    left: Property<V>,
    right: Property<V>
) {
    if (!left.isPresent && right.isPresent) {
        left.set(right)
    }
}

private fun <V> copyProperty(
    left: SetProperty<V>,
    right: SetProperty<V>
) {
    if (!left.isPresent && right.isPresent) {
        left.set(right)
    }
}

private fun <K, V> copyProperty(
    left: MapProperty<K, V>,
    right: MapProperty<K, V>
) {
    if (!left.isPresent && right.isPresent) {
        left.set(right)
    }
}

internal fun copyConfiguration(
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
        }
    }
    copyProperty(configuration.annotationStyle, extension.annotationStyle)
    copyProperty(configuration.classNamePrefix, extension.classNamePrefix)
    copyProperty(configuration.classNameSuffix, extension.classNameSuffix)
    copyProperty(configuration.customAnnotator, extension.customAnnotator)
    copyProperty(configuration.customDatePattern, extension.customDatePattern)
    copyProperty(configuration.customDateTimePattern, extension.customDateTimePattern)
    copyProperty(configuration.customRuleFactory, extension.customRuleFactory)
    copyProperty(configuration.customTimePattern, extension.customTimePattern)
    copyProperty(configuration.dateTimeType, extension.dateTimeType)
    copyProperty(configuration.dateType, extension.dateType)
    copyProperty(configuration.fileExtensions, extension.fileExtensions)
    copyProperty(configuration.fileFilter, extension.fileFilter)
    copyProperty(configuration.formatDateTimes, extension.formatDateTimes)
    copyProperty(configuration.formatDates, extension.formatDates)
    copyProperty(configuration.formatTimes, extension.formatTimes)
    copyProperty(configuration.formatTypeMapping, extension.formatTypeMapping)
    copyProperty(configuration.generateBuilders, extension.generateBuilders)
    copyProperty(configuration.includeAdditionalProperties, extension.includeAdditionalProperties)
    copyProperty(configuration.includeAllPropertiesConstructor, extension.includeAllPropertiesConstructor)
    copyProperty(configuration.includeConstructorPropertiesAnnotation, extension.includeConstructorPropertiesAnnotation)
    copyProperty(configuration.includeConstructors, extension.includeConstructors)
    copyProperty(configuration.includeCopyConstructor, extension.includeCopyConstructor)
    copyProperty(configuration.includeDynamicAccessors, extension.includeDynamicAccessors)
    copyProperty(configuration.includeDynamicBuilders, extension.includeDynamicBuilders)
    copyProperty(configuration.includeDynamicGetters, extension.includeDynamicGetters)
    copyProperty(configuration.includeDynamicSetters, extension.includeDynamicSetters)
    if (excludeGeneratedOption) {
        // Temporary fixes #71 and upstream issue #1212 (used Generated annotation is not compatible with AGP 7+)
        configuration.includeGeneratedAnnotation.set(false)
    } else {
        copyProperty(configuration.includeGeneratedAnnotation, extension.includeGeneratedAnnotation)
    }
    copyProperty(configuration.includeGetters, extension.includeGetters)
    copyProperty(configuration.includeHashcodeAndEquals, extension.includeHashcodeAndEquals)
    copyProperty(configuration.includeJsr303Annotations, extension.includeJsr303Annotations)
    copyProperty(configuration.includeJsr305Annotations, extension.includeJsr305Annotations)
    copyProperty(configuration.includeRequiredPropertiesConstructor, extension.includeRequiredPropertiesConstructor)
    copyProperty(configuration.includeSetters, extension.includeSetters)
    copyProperty(configuration.includeToString, extension.includeToString)
    copyProperty(configuration.includeTypeInfo, extension.includeTypeInfo)
    copyProperty(configuration.inclusionLevel, extension.inclusionLevel)
    copyProperty(configuration.initializeCollections, extension.initializeCollections)
    copyProperty(configuration.outputEncoding, extension.outputEncoding)
    copyProperty(configuration.parcelable, extension.parcelable)
    copyProperty(configuration.propertyWordDelimiters, extension.propertyWordDelimiters)
    copyProperty(configuration.refFragmentPathDelimiters, extension.refFragmentPathDelimiters)
    copyProperty(configuration.removeOldOutput, extension.removeOldOutput)
    copyProperty(configuration.serializable, extension.serializable)
    copyProperty(configuration.sourceSortOrder, extension.sourceSortOrder)
    copyProperty(configuration.sourceType, extension.sourceType)
    copyProperty(configuration.targetPackage, extension.targetPackage)
    copyProperty(configuration.targetVersion, extension.targetVersion)
    copyProperty(configuration.timeType, extension.timeType)
    copyProperty(configuration.toStringExcludes, extension.toStringExcludes)
    copyProperty(configuration.useBigDecimals, extension.useBigDecimals)
    copyProperty(configuration.useBigIntegers, extension.useBigIntegers)
    copyProperty(configuration.useDoubleNumbers, extension.useDoubleNumbers)
    copyProperty(configuration.useInnerClassBuilders, extension.useInnerClassBuilders)
    copyProperty(configuration.useJodaDates, extension.useJodaDates)
    copyProperty(configuration.useJodaLocalDates, extension.useJodaLocalDates)
    copyProperty(configuration.useJodaLocalTimes, extension.useJodaLocalTimes)
    copyProperty(configuration.useLongIntegers, extension.useLongIntegers)
    copyProperty(configuration.useOptionalForGetters, extension.useOptionalForGetters)
    copyProperty(configuration.usePrimitives, extension.usePrimitives)
    copyProperty(configuration.useTitleAsClassname, extension.useTitleAsClassname)
    copyProperty(configuration.useJakartaValidation, extension.useJakartaValidation)
}
