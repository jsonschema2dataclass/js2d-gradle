package org.jsonschema2dataclass.internal.plugin

import EXTRA_SPOTLESS_DISABLE
import com.diffplug.gradle.spotless.SpotlessExtension
import isExtraEnabled
import org.gradle.api.Plugin
import org.gradle.api.Project
import pluginIds
import versionCatalogs

class SpotlessConfigPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withId(pluginIds["spotless"]!!) {
            applySpotless(project)
        }
    }
}

private fun applySpotless(project: Project) {
    fun version(name: String): String =
        project
            .versionCatalogs
            .named("libs")
            .findVersion(name)
            .get()
            .requiredVersion

    val ktlintVersion: String = version("spotless-ktlint")
    val palantirVersion: String = version("spotless-palantir")

    val excludes = arrayOf(
        ".idea/**",
        "**/.idea/**",
        "**/build/**",
        "**/.gradle/**",
    )
    project.extensions.configure(SpotlessExtension::class.java) {
        if (project.isExtraEnabled(EXTRA_SPOTLESS_DISABLE)) {
            this.isEnforceCheck = false
        }
        kotlin {
            targetExclude(*excludes)
            target("**/*.kt")
            ktlint(ktlintVersion)
            endWithNewline()
        }
        kotlinGradle {
            targetExclude(*excludes)
            target("**/*.kts")
            ktlint(ktlintVersion)
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
            palantirJavaFormat(palantirVersion)
        }
    }
}
