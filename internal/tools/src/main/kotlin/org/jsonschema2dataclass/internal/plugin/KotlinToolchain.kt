package org.jsonschema2dataclass.internal.plugin

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure

class KotlinToolchain : Plugin<Project> {
    override fun apply(project: Project) {
        project.repositories.mavenCentral()

        // Configure Java 8 toolchain for the latest JVM
        if (JavaVersion.current() >= JavaVersion.VERSION_11) {
            project.extensions.configure<JavaPluginExtension> {
                toolchain.languageVersion.set(JavaLanguageVersion.of(8))
            }
        }
    }
}
