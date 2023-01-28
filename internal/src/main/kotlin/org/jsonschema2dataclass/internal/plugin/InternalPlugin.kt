package org.jsonschema2dataclass.internal.plugin

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.extra.wtp.EclipseWtpFormatterStep
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

const val ktLintFormatVersion = "0.48.2"
const val googleJavaFormatVersion = "1.15.0"
private const val spotlessDisable = "org.jsonschema2dataclass.internal.spotless.disable"

@Suppress("unused")
class InternalPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withId("com.diffplug.spotless") {
            applySpotless(project)
        }
    }
}

private fun applySpotless(project: Project) {
    val spotlessDisable = project.extra.has(spotlessDisable) && project.extra[spotlessDisable].toString().toBoolean()
    project.extensions.configure(SpotlessExtension::class.java) {
        if (spotlessDisable) {
            this.isEnforceCheck = false
        }
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
