package org.jsonschema2dataclass.js2p

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.util.GradleVersion
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Locale
import java.util.UUID

internal const val MINIMUM_GRADLE_VERSION = "6.0"
internal const val TARGET_FOLDER_BASE = "generated/sources/js2d"
internal const val DEFAULT_EXECUTION_NAME = "main"
internal const val TASK_NAME = "generateJsonSchema2DataClass"
internal const val PLUGIN_ID = "org.jsonschema2dataclass"

@Suppress("unused")
class Js2pPlugin : Plugin<Project> {
    private val javaPlugins = listOf(JavaPlugin::class.java, JavaLibraryPlugin::class.java)
    private val androidPlugins = listOf("com.android.application", "com.android.library")

    override fun apply(project: Project) {
        verifyGradleVersion()

        project.extensions.create("jsonSchema2Pojo", Js2pExtension::class.java)
        val pluginExtension = project.extensions.getByType(Js2pExtension::class.java)
        pluginExtension.targetDirectoryPrefix.convention(project.layout.buildDirectory.dir(TARGET_FOLDER_BASE))
        project.afterEvaluate {
            if (javaPlugins.any { project.plugins.hasPlugin(it) }) {
                applyInternalJava(pluginExtension, project)
            } else if (androidPlugins.any { project.plugins.hasPlugin(it) }) {
                applyInternalAndroid(pluginExtension, project)
            } else {
                throw ProjectConfigurationException("$TASK_NAME: Java or Android plugin required", listOf())
            }
        }
    }
}

private fun applyInternalJava(extension: Js2pExtension, project: Project) {

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
    ) { generationTask ->
        generationTask.dependsOn("processResources")
        javaSourceSet.srcDirs(generationTask.targetDirectory)
        javaSourceSet.sourceDirectories.plus(generationTask.targetDirectory)
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

private fun applyInternalAndroid(extension: Js2pExtension, project: Project) {

    setupConfigExecutions(
        extension,
        getAndroidJsonPath(project),
        System.getProperty("java.specification.version").toFloat() >= 9
    )

    obtainAndroidLibraryVariants(project).all {
        createTasksForVariant(project, extension, it)
    }

    obtainAndroidApplicationVariants(project).all {
        createTasksForVariant(project, extension, it)
    }
}

private fun createTasksForVariant(project: Project, extension: Js2pExtension, variant: BaseVariant): Boolean {
    val capitalized = variant.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    val task = createJS2DTask(
        project,
        extension,
        "For$capitalized",
        "${variant.flavorName}/${variant.buildType.name}/"
    ) { genTask ->
        @Suppress("DEPRECATION") // TODO: migrate to TaskGenerator
        variant.registerJavaGeneratingTask(genTask, genTask.targetDirectory.get().asFile)
    }
    @Suppress("DEPRECATION", "unused") // TODO: migrate to TaskGenerator
    variant.registerJavaGeneratingTask(task)
    return true
}

private fun getAndroidJsonPath(project: Project): Path =
    Paths.get(
        project.extensions
            .getByType(BaseExtension::class.java)
            .sourceSets.find { it.name.startsWith("main") }
            ?.resources
            ?.srcDirs
            ?.first()
            ?.toString()
            ?: "", // hmm. is it what we want?
        "json"
    )

@Suppress("DEPRECATION") // we have to support BaseVariant
private fun obtainAndroidLibraryVariants(project: Project): Set<BaseVariant> =
    project.extensions
        .findByType(LibraryExtension::class.java)
        ?.libraryVariants ?: setOf()

@Suppress("DEPRECATION") // we have to support BaseVariant
private fun obtainAndroidApplicationVariants(project: Project): Set<BaseVariant> =
    project.extensions
        .findByType(AppExtension::class.java)
        ?.applicationVariants ?: setOf()

private fun setupConfigExecutions(
    extension: Js2pExtension,
    defaultSourcePath: Path?,
    excludeGeneratedOption: Boolean,
) {
    if (extension.source.isEmpty) {
        extension.source.from(defaultSourcePath)
    }
    if (extension.executions.isEmpty()) {
        extension.executions.create(DEFAULT_EXECUTION_NAME)
    }
    extension.executions.forEach { config: Js2pConfiguration ->
        if (config.source.isEmpty) {
            config.source.from(extension.source)
        }

        // Temporary fixes #71 and upstream issue #1212 (used Generated annotation is not compatible with AGP 7+)
        if (excludeGeneratedOption) {
            config.includeGeneratedAnnotation.set(false)
        }
    }
}

private fun createJS2DTask(
    project: Project,
    extension: Js2pExtension,
    taskNameSuffix: String,
    targetDirectorySuffix: String,
    postConfigure: (task: Js2pGenerationTask) -> Unit,
): Task {
    val js2dTask = project.task(
        mapOf(
            Pair("description", "Generates Java classes from a json schema using JsonSchema2Pojo."),
            Pair("group", "Build"),
        ),
        "${TASK_NAME}$taskNameSuffix"
    )

    extension.executions.forEachIndexed { configurationId, configuration ->
        val task = createJS2DTaskExecution(
            project,
            configurationId,
            taskNameSuffix,
            configuration.name,
            configuration.source,
            extension.targetDirectoryPrefix,
            targetDirectorySuffix
        )
        postConfigure(task)
        js2dTask.dependsOn(task)
    }

    return js2dTask
}

// TODO: use TaskProvider, e.g. tasks.register() {}
private fun createJS2DTaskExecution(
    project: Project,
    configurationId: Int,
    taskNameSuffix: String,
    configurationName: String,
    source: ConfigurableFileCollection,
    targetDirectoryPrefix: DirectoryProperty,
    targetDirectorySuffix: String,

): Js2pGenerationTask {
    val task: Js2pGenerationTask = project.task(
        mapOf(
            Pair("type", Js2pGenerationTask::class.java),
            Pair("description", "Generates Java classes from a json schema using JsonSchema2Pojo. Configuration $configurationName"),
            Pair("group", "Build")
        ),
        "${TASK_NAME}${configurationId}$taskNameSuffix"
    ) as Js2pGenerationTask
    val targetPath = targetDirectoryPrefix.dir("${configurationName}$targetDirectorySuffix")

    task.sourceFiles.setFrom(source.filter { it.exists() })
    task.configurationName = configurationName
    task.uuid = UUID.randomUUID()
    task.targetDirectory.set(targetPath)

    skipInputWhenEmpty(task, task.sourceFiles)

    task.sourceFiles.forEach { it.mkdirs() }

    return task
}

private fun verifyGradleVersion() {
    if (GradleVersion.current() < GradleVersion.version(MINIMUM_GRADLE_VERSION)) {
        throw GradleException("Plugin $PLUGIN_ID requires at least Gradle $MINIMUM_GRADLE_VERSION, but you are using ${GradleVersion.current().version}")
    }
}
