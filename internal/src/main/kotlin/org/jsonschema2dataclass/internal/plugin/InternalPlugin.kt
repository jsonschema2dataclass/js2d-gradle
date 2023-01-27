package org.jsonschema2dataclass.internal.plugin

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.extra.wtp.EclipseWtpFormatterStep
import org.gradle.api.Plugin
import org.gradle.api.Project

const val ktLintFormatVersion = "0.48.2"
const val googleJavaFormatVersion = "1.15.0"

@Suppress("unused")
class InternalPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withId("com.diffplug.spotless") {
            applySpotless(project)
        }
    }
}

private fun applySpotless(project: Project) {
    project.extensions.configure(SpotlessExtension::class.java) {
        kotlin {
            targetExclude(".idea", "**/.idea", "plugin/build", "**/build")
            target("**/*.kt")
            ktlint(ktLintFormatVersion)
            endWithNewline()
        }
        kotlinGradle {
            targetExclude(".idea", "**/.idea", "plugin/build", "**/build")
            target("*.gradle.kts")
            ktlint(ktLintFormatVersion)
            endWithNewline()
        }
        json {
            targetExclude(".idea", "**/.idea", "plugin/build", "**/build")
            target("demo/**/*.json", "src/**/*.json")
            simple()
            endWithNewline()
        }
        format("xml") {
            targetExclude(".idea", "**/.idea", "plugin/build", "**/build")
            target("demo/**src/**/*.xml", "src/**/*.xml")
            eclipseWtp(EclipseWtpFormatterStep.XML)
        }
        java {
            targetExclude(".idea", "**/.idea", "plugin/build", "**/build")
            target("demo/**src/**/*.java", "src/**/*.java")
            googleJavaFormat(googleJavaFormatVersion)
        }
    }
}
