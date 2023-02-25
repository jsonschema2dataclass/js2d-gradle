package org.jsonschema2dataclass.internal.plugin

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

private const val EXTRA_SPOTLESS_DISABLE = "org.jsonschema2dataclass.internal.spotless.disable"
private const val EXTENSION_SPOTLESS_EXTENSION = "jsonschema2dataclassSpotless"

class SpotlessConfigPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create<SpotlessPluginExtension>(EXTENSION_SPOTLESS_EXTENSION)
        project.plugins.withId("com.diffplug.spotless") {
            applySpotless(project, extension)
        }
    }
}

open class SpotlessPluginExtension(var ktlintVersion: String? = null, var palantirVersion: String? = null)

private fun applySpotless(project: Project, styleCheckers: SpotlessPluginExtension) {
    val spotlessDisable = project.extra.has(EXTRA_SPOTLESS_DISABLE) &&
        project.extra[EXTRA_SPOTLESS_DISABLE].toString().toBoolean()
    val excludes = arrayOf(
        ".idea/**",
        "**/.idea/**",
        "**/build/**",
        "**/.gradle/**",
    )
    project.extensions.configure(SpotlessExtension::class.java) {
        if (spotlessDisable) {
            this.isEnforceCheck = false
        }
        kotlin {
            targetExclude(*excludes)
            target("**/*.kt")
            ktlint(styleCheckers.ktlintVersion)
            endWithNewline()
        }
        kotlinGradle {
            targetExclude(*excludes)
            target("**/*.kts")
            ktlint(styleCheckers.ktlintVersion)
            endWithNewline()
        }
        json {
            targetExclude(*excludes)
            target("**/*.json")
            jackson()
            endWithNewline()
        }
        yaml {
            targetExclude(*excludes)
            target("**/*.yaml")
            jackson()
            endWithNewline()
        }
        java {
            targetExclude(*excludes)
            target("**/*.java")
            palantirJavaFormat(styleCheckers.palantirVersion)
        }
    }
}
