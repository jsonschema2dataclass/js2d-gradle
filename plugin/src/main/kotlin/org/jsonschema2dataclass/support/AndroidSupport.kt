package org.jsonschema2dataclass.support

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.tasks.SourceSet
import org.jsonschema2dataclass.js2p.Js2pExtension
import org.jsonschema2dataclass.TASK_NAME
import org.jsonschema2dataclass.support.android.applyInternalAndroidAgp3
import org.jsonschema2dataclass.support.android.applyInternalAndroidAgp7
import java.nio.file.Path
import java.nio.file.Paths

internal fun applyInternalAndroid(extension: Js2pExtension, project: Project) {
    val experimental = project.properties["org.jsonschema2dataclass.agp7.experimental"]?.toString().toBoolean()
    when (com.android.Version.ANDROID_GRADLE_PLUGIN_VERSION.split(".")[0].toInt()) {
        3, 4 -> applyInternalAndroidAgp3(extension, project)
        7 -> if (experimental) {
            applyInternalAndroidAgp7(extension, project)
        } else {
            applyInternalAndroidAgp3(extension, project)
        }

        else -> throw ProjectConfigurationException("$TASK_NAME: Plugin supports AGP 3.x, 4.x or 7.x", listOf())
    }
}

internal fun getAndroidJsonPath(project: Project): Path =
    Paths.get(
        project.extensions
            .getByType(BaseExtension::class.java)
            .sourceSets.find { it.name.startsWith(SourceSet.MAIN_SOURCE_SET_NAME) }
            ?.resources
            ?.srcDirs
            ?.first()
            ?.toString()
            ?: "", // TODO: hmm. is it what we want?
        "json"
    )

// Made to support String Capitalization on Java 1.8 to 17
fun String.capitalization(): String =
    if (Character.isLowerCase(this[0])) {
        Character.toTitleCase(this[0]) + this.substring(1)
    } else {
        this
    }
