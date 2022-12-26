@file:Suppress("DEPRECATION")

package org.jsonschema2dataclass.js2p.support.android

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.LibraryVariant
import org.gradle.api.*
import org.jsonschema2dataclass.js2p.Js2pExtension
import org.jsonschema2dataclass.js2p.createJS2DTask
import org.jsonschema2dataclass.js2p.support.getAndroidJsonPath

internal fun applyInternalAndroidAgp3(extension: Js2pExtension, project: Project) {
    obtainAndroidLibraryVariants(project)?.all {
        createTasksForVariant(project, extension, this)
    }

    obtainAndroidApplicationVariants(project)?.all {
        createTasksForVariant(project, extension, this)
    }
}

private fun <T: BaseVariant> createTasksForVariant(project: Project, extension: Js2pExtension, variant: T): Boolean {
    val task = createJS2DTask(
        project,
        extension,
        getAndroidJsonPath(project),
        variant.name,
        "${variant.flavorName}/${variant.buildType.name}/",
        JavaVersion.current() >= JavaVersion.VERSION_1_9
    ) { genTask, targetPath ->
        variant.registerJavaGeneratingTask(genTask.get(), targetPath.get().asFile)
        try {
            project.tasks.named("compile${variant.name.capitalize()}Kotlin") {
                this.dependsOn(genTask)
            }
        } catch (_: UnknownTaskException) {
        }
    }
    variant.registerJavaGeneratingTask(task.get())
    return true
}

private fun obtainAndroidLibraryVariants(project: Project): DomainObjectCollection<LibraryVariant>? =
    project.extensions
        .findByType(LibraryExtension::class.java)
        ?.libraryVariants

private fun obtainAndroidApplicationVariants(project: Project): DomainObjectSet<ApplicationVariant>? =
    project.extensions
        .findByType(AppExtension::class.java)
        ?.applicationVariants
