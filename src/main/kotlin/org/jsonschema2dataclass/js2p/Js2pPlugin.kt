package org.jsonschema2dataclass.js2p

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.the
import org.gradle.util.GradleVersion
import org.jsonschema2dataclass.js2p.support.applyInternalAndroid
import org.jsonschema2dataclass.js2p.support.applyInternalJava
import java.nio.file.Path
import java.util.UUID

internal const val MINIMUM_GRADLE_VERSION = "6.0"
internal const val TARGET_FOLDER_BASE = "generated/sources/js2d"
internal const val DEFAULT_EXECUTION_NAME = "main"
internal const val TASK_NAME = "generateJsonSchema2DataClass"
internal const val PLUGIN_ID = "org.jsonschema2dataclass"

@Suppress("unused")
class Js2pPlugin : Plugin<Project> {

    private val javaPlugins = listOf("java", "java-library")
    private val androidPlugins = listOf("com.android.application", "com.android.library")

    override fun apply(project: Project) {
        verifyGradleVersion()
        project.extensions.create("jsonSchema2Pojo", Js2pExtension::class.java)
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
        project.afterEvaluate {
            applyInternalJava(the(), project)
        }
    }
}

internal class Js2pAndroidPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.afterEvaluate {
            applyInternalAndroid(the(), project)
        }
    }
}

internal fun setupConfigExecutions(
        extension: Js2pExtension,
        defaultSourcePath: Path?,
        excludeGeneratedOption: Boolean,
) {
    if (extension.source.isEmpty && defaultSourcePath != null) {
        extension.source.setFrom(defaultSourcePath.toFile())
    }

    if (extension.executions.isEmpty()) {
        extension.executions.create(DEFAULT_EXECUTION_NAME)
    }

    extension.executions.forEach { configuration: Js2pConfiguration ->
        if (configuration.source.isEmpty) {
            configuration.source.setFrom(extension.source)
        }

        // Temporary fixes #71 and upstream issue #1212 (used Generated annotation is not compatible with AGP 7+)
        if (excludeGeneratedOption) {
            configuration.includeGeneratedAnnotation.set(false)
        }
    }
}

internal fun createJS2DTask(
        project: Project,
        extension: Js2pExtension,
        taskNameSuffix: String,
        targetDirectorySuffix: String,
        postConfigure: (
                task: TaskProvider<out Js2pGenerationTask>,
                DirectoryProperty
        ) -> Unit,
): TaskProvider<Task> {

    val js2dTask = project.tasks.register("${TASK_NAME}$taskNameSuffix", Task::class.java) {
        description = "Generates Java classes from a json schema using JsonSchema2Pojo."
        group = "Build"
    }

    extension.executions.forEachIndexed { configurationId, configuration ->
        val targetPath = project.objects.directoryProperty()
        targetPath.set(extension.targetDirectoryPrefix.dir("${configuration.name}$targetDirectorySuffix"))
        val taskProvider = createJS2DTaskExecution(
                project,
                configurationId,
                taskNameSuffix,
                configuration.name,
                configuration,
                extension,
                configuration.source.filter { it.exists() },
                targetPath,
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
        configurationId: Int,
        taskNameSuffix: String,
        configurationName: String,
        configuration: Js2pConfiguration,
        pluginExtension: Js2pConfiguration,
        source: FileCollection,
        targetPath: DirectoryProperty,
): TaskProvider<out Js2pGenerationTask> {
    val taskName = "${TASK_NAME}${configurationId}$taskNameSuffix"
    return project.tasks.register(taskName, Js2pGenerationTask::class.java) {
        this.description =
                "Generates Java classes from a json schema using JsonSchema2Pojo for configuration $configurationName"
        this.group = "Build"

        setTaskConfiguration(project, this, configuration, pluginExtension)
        this.uuid = UUID.randomUUID()
        this.targetDirectory.set(targetPath)

        skipInputWhenEmpty(this, source)
        source.forEach { it.mkdirs() }
    }
}

private fun verifyGradleVersion() {
    if (GradleVersion.current() < GradleVersion.version(MINIMUM_GRADLE_VERSION)) {
        throw GradleException("Plugin $PLUGIN_ID requires at least Gradle $MINIMUM_GRADLE_VERSION, but you are using ${GradleVersion.current().version}")
    }
}

private fun skipInputWhenEmpty(task: Task, sourceFiles: FileCollection) {
    val input = task.inputs.files(sourceFiles)
            .skipWhenEmpty()

    if (GradleVersion.current() >= GradleVersion.version("6.8")) {
        input.ignoreEmptyDirectories()
    }
}

private fun setTaskConfiguration(project: Project, task: Js2pGenerationTask, configuration: Js2pConfiguration, defaults: Js2pConfiguration) {
    task.source.setFrom(if (configuration.source.isEmpty) {
        defaults.source
    } else {
        configuration.source
    }
    )
    task.annotationStyle = maybeDefault(project, configuration.annotationStyle, defaults.annotationStyle)
    task.classNamePrefix = maybeDefault(project, configuration.classNamePrefix, defaults.classNamePrefix)
    task.classNameSuffix = maybeDefault(project, configuration.classNameSuffix, defaults.classNameSuffix)
    task.constructorsRequiredPropertiesOnly = maybeDefault(project, configuration.constructorsRequiredPropertiesOnly, defaults.constructorsRequiredPropertiesOnly)
    task.customAnnotator = maybeDefault(project, configuration.customAnnotator, defaults.customAnnotator)
    task.customDatePattern = maybeDefault(project, configuration.customDatePattern, defaults.customDatePattern)
    task.customDateTimePattern = maybeDefault(project, configuration.customDateTimePattern, defaults.customDateTimePattern)
    task.customRuleFactory = maybeDefault(project, configuration.customRuleFactory, defaults.customRuleFactory)
    task.customTimePattern = maybeDefault(project, configuration.customTimePattern, defaults.customTimePattern)
    task.dateTimeType = maybeDefault(project, configuration.dateTimeType, defaults.dateTimeType)
    task.dateType = maybeDefault(project, configuration.dateType, defaults.dateType)
    task.fileExtensions = maybeDefault(project, configuration.fileExtensions, defaults.fileExtensions)
    task.fileFilter = maybeDefault(project, configuration.fileFilter, defaults.fileFilter)
    task.formatDateTimes = maybeDefault(project, configuration.formatDateTimes, defaults.formatDateTimes)
    task.formatDates = maybeDefault(project, configuration.formatDates, defaults.formatDates)
    task.formatTimes = maybeDefault(project, configuration.formatTimes, defaults.formatTimes)
    task.formatTypeMapping = maybeDefault(project, configuration.formatTypeMapping, defaults.formatTypeMapping)
    task.generateBuilders = maybeDefault(project, configuration.generateBuilders, defaults.generateBuilders)
    task.includeAdditionalProperties = maybeDefault(project, configuration.includeAdditionalProperties, defaults.includeAdditionalProperties)
    task.includeAllPropertiesConstructor = maybeDefault(project, configuration.includeAllPropertiesConstructor, defaults.includeAllPropertiesConstructor)
    task.includeConstructorPropertiesAnnotation = maybeDefault(project, configuration.includeConstructorPropertiesAnnotation, defaults.includeConstructorPropertiesAnnotation)
    task.includeConstructors = maybeDefault(project, configuration.includeConstructors, defaults.includeConstructors)
    task.includeCopyConstructor = maybeDefault(project, configuration.includeCopyConstructor, defaults.includeCopyConstructor)
    task.includeDynamicAccessors = maybeDefault(project, configuration.includeDynamicAccessors, defaults.includeDynamicAccessors)
    task.includeDynamicBuilders = maybeDefault(project, configuration.includeDynamicBuilders, defaults.includeDynamicBuilders)
    task.includeDynamicGetters = maybeDefault(project, configuration.includeDynamicGetters, defaults.includeDynamicGetters)
    task.includeDynamicSetters = maybeDefault(project, configuration.includeDynamicSetters, defaults.includeDynamicSetters)
    task.includeGeneratedAnnotation = maybeDefault(project, configuration.includeGeneratedAnnotation, defaults.includeGeneratedAnnotation)
    task.includeGetters = maybeDefault(project, configuration.includeGetters, defaults.includeGetters)
    task.includeHashcodeAndEquals = maybeDefault(project, configuration.includeHashcodeAndEquals, defaults.includeHashcodeAndEquals)
    task.includeJsr303Annotations = maybeDefault(project, configuration.includeJsr303Annotations, defaults.includeJsr303Annotations)
    task.includeJsr305Annotations = maybeDefault(project, configuration.includeJsr305Annotations, defaults.includeJsr305Annotations)
    task.includeRequiredPropertiesConstructor = maybeDefault(project, configuration.includeRequiredPropertiesConstructor, defaults.includeRequiredPropertiesConstructor)
    task.includeSetters = maybeDefault(project, configuration.includeSetters, defaults.includeSetters)
    task.includeToString = maybeDefault(project, configuration.includeToString, defaults.includeToString)
    task.includeTypeInfo = maybeDefault(project, configuration.includeTypeInfo, defaults.includeTypeInfo)
    task.inclusionLevel = maybeDefault(project, configuration.inclusionLevel, defaults.inclusionLevel)
    task.initializeCollections = maybeDefault(project, configuration.initializeCollections, defaults.initializeCollections)
    task.outputEncoding = maybeDefault(project, configuration.outputEncoding, defaults.outputEncoding)
    task.parcelable = maybeDefault(project, configuration.parcelable, defaults.parcelable)
    task.propertyWordDelimiters = maybeDefault(project, configuration.propertyWordDelimiters, defaults.propertyWordDelimiters)
    task.refFragmentPathDelimiters = maybeDefault(project, configuration.refFragmentPathDelimiters, defaults.refFragmentPathDelimiters)
    task.removeOldOutput = maybeDefault(project, configuration.removeOldOutput, defaults.removeOldOutput)
    task.serializable = maybeDefault(project, configuration.serializable, defaults.serializable)
    task.sourceSortOrder = maybeDefault(project, configuration.sourceSortOrder, defaults.sourceSortOrder)
    task.sourceType = maybeDefault(project, configuration.sourceType, defaults.sourceType)
    task.targetPackage = maybeDefault(project, configuration.targetPackage, defaults.targetPackage)
    task.targetVersion = maybeDefault(project, configuration.targetVersion, defaults.targetVersion)
    task.timeType = maybeDefault(project, configuration.timeType, defaults.timeType)
    task.toStringExcludes = maybeDefault(project, configuration.toStringExcludes, defaults.toStringExcludes)
    task.useBigDecimals = maybeDefault(project, configuration.useBigDecimals, defaults.useBigDecimals)
    task.useBigIntegers = maybeDefault(project, configuration.useBigIntegers, defaults.useBigIntegers)
    task.useDoubleNumbers = maybeDefault(project, configuration.useDoubleNumbers, defaults.useDoubleNumbers)
    task.useInnerClassBuilders = maybeDefault(project, configuration.useInnerClassBuilders, defaults.useInnerClassBuilders)
    task.useJodaDates = maybeDefault(project, configuration.useJodaDates, defaults.useJodaDates)
    task.useJodaLocalDates = maybeDefault(project, configuration.useJodaLocalDates, defaults.useJodaLocalDates)
    task.useJodaLocalTimes = maybeDefault(project, configuration.useJodaLocalTimes, defaults.useJodaLocalTimes)
    task.useLongIntegers = maybeDefault(project, configuration.useLongIntegers, defaults.useLongIntegers)
    task.useOptionalForGetters = maybeDefault(project, configuration.useOptionalForGetters, defaults.useOptionalForGetters)
    task.usePrimitives = maybeDefault(project, configuration.usePrimitives, defaults.usePrimitives)
    task.useTitleAsClassname = maybeDefault(project, configuration.useTitleAsClassname, defaults.useTitleAsClassname)
    task.useJakartaValidation = maybeDefault(project, configuration.useJakartaValidation, defaults.useJakartaValidation)
}

private fun <V> maybeDefault(project: Project,value: Provider<V>, valueDefault: Provider<V>): Provider<V> {
    return project.provider{
            value.orNull ?: valueDefault.orNull
    }
}
