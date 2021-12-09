package org.jsonschema2dataclass.js2p.support.android

import com.android.build.api.artifact.Artifact
import com.android.build.api.artifact.ArtifactKind
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.FileSystemLocation
import org.jsonschema2dataclass.js2p.Js2pExtension
import org.jsonschema2dataclass.js2p.createJS2DTask
import org.jsonschema2dataclass.js2p.support.capitalization

internal fun applyInternalAndroidAgp7(extension: Js2pExtension, project: Project) {
    val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
    androidComponents.onVariants { variant ->
        createTasksForVariant7(project, extension, variant)
    }
}

private fun createTasksForVariant7(project: Project, extension: Js2pExtension, variant: Variant) {
    val capitalizedName = variant.name.capitalization()
    KotlinVersion.CURRENT
    createJS2DTask(
        project,
        extension,
        "For$capitalizedName",
        "${variant.flavorName}/${variant.buildType}/"
    ) { genTask, _ ->
        variant.artifacts.use(genTask).wiredWith { it.targetDirectory }
            .toCreate(ArtifactType7.SINGLE_DIRECTORY_ARTIFACT)
    }
}

private sealed class ArtifactType7<T : FileSystemLocation>(kind: ArtifactKind<T>, category: Category) :
    Artifact.Single<T>(kind, category) {
    @Suppress("ClassName")
    object SINGLE_DIRECTORY_ARTIFACT :
        ArtifactType7<Directory>(ArtifactKind.DIRECTORY, Category.GENERATED), Replaceable
}