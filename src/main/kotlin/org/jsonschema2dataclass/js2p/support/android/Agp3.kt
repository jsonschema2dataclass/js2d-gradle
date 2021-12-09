@file:Suppress("DEPRECATION")

package org.jsonschema2dataclass.js2p.support.android

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project
import org.jsonschema2dataclass.js2p.Js2pExtension
import org.jsonschema2dataclass.js2p.createJS2DTask

internal fun applyInternalAndroidAgp3(extension: Js2pExtension, project: Project) {
    project.afterEvaluate {
        listOf(
            obtainAndroidLibraryVariants(project).asSequence(),
            obtainAndroidApplicationVariants(project).asSequence(),
        ).asSequence().flatten().forEach {
            createTasksForVariant(project, extension, it)
        }
    }
}

private fun createTasksForVariant(project: Project, extension: Js2pExtension, variant: BaseVariant): Boolean {
    val capitalizedName = variant.name.capitalize()

    val task = createJS2DTask(
        project,
        extension,
        "For$capitalizedName",
        "${variant.flavorName}/${variant.buildType.name}/"
    ) { genTask, targetPath ->
        variant.registerJavaGeneratingTask(genTask.get(), targetPath.get().asFile)
    }
    variant.registerJavaGeneratingTask(task.get())
    return true
}

private fun obtainAndroidLibraryVariants(project: Project): Set<BaseVariant> =
    project.extensions
        .findByType(LibraryExtension::class.java)
        ?.libraryVariants ?: setOf()

private fun obtainAndroidApplicationVariants(project: Project): Set<BaseVariant> =
    project.extensions
        .findByType(AppExtension::class.java)
        ?.applicationVariants ?: setOf()
