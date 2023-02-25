package org.jsonschema2dataclass.internal.plugin.lib

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.jsonschema2dataclass.internal.plugin.publishing.PublishingPlugin

/** Supporting library basic configuration */
@Suppress("unused")
class LibraryPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply(KotlinToolchain::class)
        project.plugins.apply(PublishingPlugin::class)
    }
}
