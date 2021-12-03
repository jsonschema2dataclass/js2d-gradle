package org.jsonschema2dataclass.js2p

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Locale

internal fun applyInternalAndroidHistorical(extension: Js2pExtension, project: Project) {

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
    ) { genTask, targetPath ->
        variant.registerJavaGeneratingTask(genTask, targetPath.get().asFile)
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
