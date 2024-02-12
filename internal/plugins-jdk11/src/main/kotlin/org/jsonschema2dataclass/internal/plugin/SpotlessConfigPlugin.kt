package org.jsonschema2dataclass.internal.plugin

import EXTRA_SPOTLESS_ENABLE
import com.diffplug.gradle.spotless.SpotlessExtension
import isExtraEnabled
import org.gradle.api.GradleException
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
    if (!project.isExtraEnabled(EXTRA_SPOTLESS_ENABLE)) {
        project.extensions.configure(SpotlessExtension::class.java) {
            this.isEnforceCheck = false
        }
        return
    }

    fun version(name: String, catalog: String = "quality"): String =
        project
            .versionCatalogs
            .named(catalog)
            .findVersion(name)
            .orElseThrow { GradleException("Unable resolve version for $name in catalog $catalog") }
            .requiredVersion

    val palantirVersion: String = version("spotless-palantir")

    val excludes = arrayOf(
        ".idea/**",
        "**/.idea/**",
        "**/build/**",
        "**/.gradle/**",
    )
    project.extensions.configure(SpotlessExtension::class.java) {
        java {
            targetExclude(excludes)
            target("**/*.java")
            palantirJavaFormat(palantirVersion)
        }
    }
}
