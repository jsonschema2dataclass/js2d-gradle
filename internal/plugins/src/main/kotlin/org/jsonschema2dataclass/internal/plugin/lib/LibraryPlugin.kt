package org.jsonschema2dataclass.internal.plugin.lib

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.jsonschema2dataclass.internal.plugin.publishing.PublishingPlugin

/**
 * Support library basic configuration.
 *
 * Plugin configures Kotlin toolchain (Java version) and publishing to Maven Central
 */
@Suppress("unused")
class LibraryPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply(KotlinToolchain::class)
        project.plugins.apply(PublishingPlugin::class)
    }
}
