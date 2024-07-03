@file:Suppress("DEPRECATION")

package org.jsonschema2dataclass.internal.compat.android

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.LibraryVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.gradle.api.Action
import org.gradle.api.DomainObjectCollection
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.invoke
import org.jsonschema2dataclass.internal.GradlePluginRegistration
import org.jsonschema2dataclass.internal.ProcessorRegistrationCallback
import java.nio.file.Path

/**  Register a processor task for Android Gradle Plugin 7.x using AGP 3 API. */
@Suppress("unused")
class AGP347PluginRegistration : GradlePluginRegistration {
    override fun defaultSchemaPathInternal(project: Project): Path? =
        project.extensions
            .getByType(BaseExtension::class.java)
            .sourceSets.find { it.name.startsWith(SourceSet.MAIN_SOURCE_SET_NAME) }
            ?.resources
            ?.srcDirs
            ?.first()
            ?.toPath()

    override fun registerPlugin(project: Project, callback: ProcessorRegistrationCallback) {
        val androidComponents: AndroidComponentsExtension<*, *, *> =
            project.extensions.getByName<AndroidComponentsExtension<*, *, *>>("androidComponents")
        androidComponents.onVariants { variant ->
            variant.buildConfigFields
        }
        obtainAndroidLibraryVariants(project)?.all {
            createTasksForVariant(this, callback)
        }

        obtainAndroidApplicationVariants(project)?.all {
            createTasksForVariant(this, callback)
        }
    }
}

private fun <T : BaseVariant> createTasksForVariant(
    variant: T,
    callback: ProcessorRegistrationCallback,
) {
    callback.invoke(
        mapOf("variant" to variant.name),
    ) { taskProvider: TaskProvider<out Task>, targetPath: Path?, dependsOn: Action<String> ->
        taskProvider.dependsOn(variant.processJavaResourcesProvider)
        variant.javaCompileProvider.dependsOn(taskProvider)
        if (targetPath != null) {
            variant.registerJavaGeneratingTask(taskProvider, listOfNotNull(targetPath.toFile()))
            variant.addJavaSourceFoldersToModel(listOfNotNull(targetPath.toFile()))
        } else {
            variant.registerJavaGeneratingTask(taskProvider)
        }
        dependsOn("compile${variant.name.capitalized()}Kotlin")
    }
}

private fun obtainAndroidLibraryVariants(project: Project): DomainObjectCollection<LibraryVariant>? =
    project.extensions
        .findByType(LibraryExtension::class.java)
        ?.libraryVariants

private fun obtainAndroidApplicationVariants(project: Project): DomainObjectSet<ApplicationVariant>? =
    project.extensions
        .findByType(AppExtension::class.java)
        ?.applicationVariants
