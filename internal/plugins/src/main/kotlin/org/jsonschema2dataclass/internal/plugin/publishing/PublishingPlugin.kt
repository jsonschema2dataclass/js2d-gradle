package org.jsonschema2dataclass.internal.plugin.publishing

import javaPluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension

/**
 * Heavily inspired by
 * [net.twisterrob.gradle](https://github.com/TWiStErRob/net.twisterrob.gradle])
 * publishing code
 *
 * Disable automated publishing [completed feature
 * request](https://github.com/gradle/gradle/issues/11611) -
 * not working with gradle-publish plugin version 1.0 and above
 */
class PublishingPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val signing = applySigning(project)

        val javaPluginExtension = project.javaPluginExtension
        javaPluginExtension.withSourcesJar()

        applyPublishing(project, signing)

        project.plugins.withId("java-gradle-plugin") {
            project.configure<GradlePluginDevelopmentExtension> {
                isAutomatedPublishing = false
            }
        }
    }
}
