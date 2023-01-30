package org.jsonschema2dataclass.internal.plugin

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.extra.wtp.EclipseWtpFormatterStep
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

const val ktLintFormatVersion = "0.48.2"
const val palantirJavaFormatVersion = "2.21.0"
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
    val excludes = arrayOf(
        ".idea/**",
        "**/.idea/**",
        "**/build/**",
        "**/.gradle/**",
    )
    project.extensions.configure(SpotlessExtension::class.java) {
        if (spotlessDisable) {
            this.isEnforceCheck = false
        }
        kotlin {
            targetExclude(*excludes)
            target("**/*.kt")
            ktlint(ktLintFormatVersion)
            endWithNewline()
        }
        kotlinGradle {
            targetExclude(*excludes)
            target("**/*.kts")
            ktlint(ktLintFormatVersion)
            endWithNewline()
        }
        json {
            targetExclude(*excludes)
            target("**/*.json")
            jackson()
            endWithNewline()
        }
        yaml {
            targetExclude(*excludes)
            target("**/*.yaml")
            jackson()
            endWithNewline()
        }
        format("xml") {
            targetExclude(*excludes)
            target("**/*.xml")
            eclipseWtp(EclipseWtpFormatterStep.XML)
        }
        java {
            targetExclude(*excludes)
            target("**/*.java")
            palantirJavaFormat(palantirJavaFormatVersion)
        }
    }
}
