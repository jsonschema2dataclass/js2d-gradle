package org.jsonschema2dataclass.internal.plugin.lib

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.jsonschema2dataclass.internal.plugin.publishing.applySigning
import pluginIds

@Suppress("unused")
class GradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply(KotlinToolchain::class)

        applySigning(project)
        project.plugins.apply(pluginIds["gradle-publish"]!!)
    }
}
