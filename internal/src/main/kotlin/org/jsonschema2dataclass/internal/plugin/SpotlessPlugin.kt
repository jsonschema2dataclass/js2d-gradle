package org.jsonschema2dataclass.internal.plugin

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.extra.wtp.EclipseWtpFormatterStep
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

const val ktLintFormatVersion = "0.48.2"
const val googleJavaFormatVersion = "1.15.0"
private const val EXTRA_SPOTLESS_DISABLE = "org.jsonschema2dataclass.internal.spotless.disable"

class SpotlessPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withId("com.diffplug.spotless") {
            applySpotless(project)
        }
    }
}

private fun applySpotless(project: Project) {
    val spotlessDisable = project.extra.has(EXTRA_SPOTLESS_DISABLE) &&
        project.extra[EXTRA_SPOTLESS_DISABLE].toString().toBoolean()
    project.extensions.configure(SpotlessExtension::class.java) {
        if (spotlessDisable) {
            this.isEnforceCheck = false
        }
        kotlin {
            targetExclude(".idea/**", "**/.idea/**", "**/build")
            target("**/*.kt")
            ktlint(ktLintFormatVersion)
            endWithNewline()
        }
        kotlinGradle {
            targetExclude(".idea/**", "**/.idea/**", "**/build")
            target("**/*.kts")
            ktlint(ktLintFormatVersion)
            endWithNewline()
        }
        json {
            targetExclude(".idea/**", "**/.idea/**", "**/build")
            target("**/*.json")
            jackson()
            endWithNewline()
        }
        yaml {
            targetExclude(".idea/**", "**/.idea/**", "**/build")
            target("**/*.yaml")
            jackson()
            endWithNewline()
        }
        format("xml") {
            targetExclude(".idea/**", "**/.idea/**", "**/build")
            target("**/*.xml")
            eclipseWtp(EclipseWtpFormatterStep.XML)
        }
        java {
            targetExclude(".idea/**", "**/.idea/**", "**/build")
            target("**/*.java")
            googleJavaFormat(googleJavaFormatVersion)
        }
    }
}
