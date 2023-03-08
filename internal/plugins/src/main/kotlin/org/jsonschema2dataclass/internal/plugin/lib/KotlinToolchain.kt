
package org.jsonschema2dataclass.internal.plugin.lib

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure

@Suppress("unused")
class KotlinToolchain : Plugin<Project> {
    override fun apply(project: Project) {
        // Configure Java 8 toolchain for the latest JVM
        if (JavaVersion.current() >= JavaVersion.VERSION_11) {
            project.extensions.configure<JavaPluginExtension> {
                withSourcesJar()
                toolchain.languageVersion.set(JavaLanguageVersion.of(8))
            }
        }
    }
}
