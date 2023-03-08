package org.jsonschema2dataclass.internal.plugin.lib

import EXTRA_LOCAL_PUBLISH
import isExtraEnabled
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import pluginIds

class GradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply(KotlinToolchain::class)

        if (!project.isExtraEnabled(EXTRA_LOCAL_PUBLISH)) {
            project.plugins.apply(pluginIds["gradle-publish"]!!)
        }
    }
}
