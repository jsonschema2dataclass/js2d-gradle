package org.jsonschema2dataclass.js2p

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException

internal fun applyInternalAndroidNewDSLx(extension: Js2pExtension, project: Project) {
    val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
//    androidComponents.onVariants { variant ->
//        variant.artifacts.use(createJS2DTask(
//            project,
//            extension,
//        ))
//    }
//    androidComponents.onVariants {
//        it.
//    }
    throw ProjectConfigurationException("$TASK_NAME: Android new DSL is not supported...yet", listOf())
}

/*private fun createTasksForVariant(project: Project, extension: Js2pExtension, variant: Variant): Boolean {
    val capitalized = variant.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    val task = createJS2DTask(
        project,
        extension,
        "For$capitalized",
        "${variant.flavorName}/${variant.buildType}/"
    ) { genTask ->
        variant.
        project.androidExtensionOrThrow
            .sourceSets
            .getByName(name)
            .kotlinSourceSet!!
            .srcDir(outputDir)
        @Suppress("DEPRECATION") // TODO: migrate to TaskGenerator
        variant.registerJavaGeneratingTask(genTask, genTask.targetDirectory.get().asFile)
    }
    @Suppress("DEPRECATION", "unused") // TODO: migrate to TaskGenerator
    variant.registerJavaGeneratingTask(task)
    return true
}*/
