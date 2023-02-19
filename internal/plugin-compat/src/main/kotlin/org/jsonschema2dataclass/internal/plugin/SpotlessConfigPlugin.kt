package org.jsonschema2dataclass.internal.plugin

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.extra.wtp.EclipseWtpFormatterStep
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.getValue
import org.gradle.util.GradleVersion

private const val EXTRA_SPOTLESS_DISABLE = "org.jsonschema2dataclass.internal.spotless.disable"

class SpotlessConfigPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val styleCheckers: Configuration by project.configurations.creating {
            if (GradleVersion.current() >= GradleVersion.version("6.8")) {
                @Suppress("UnstableApiUsage")
                disableConsistentResolution()
            }
            isCanBeConsumed = false
            isCanBeResolved = false
            isVisible = false
        }

        project.plugins.withId("com.diffplug.spotless") {
            applySpotless(project, styleCheckers)
        }
    }
}

private fun applySpotless(project: Project, styleCheckers: Configuration) {
    fun applyVersion(dependency: String): String {
        project.dependencies {
            styleCheckers(dependency)
        }
        return dependency.substring(dependency.lastIndexOf(":") + 1)
    }

    val ktLintFormatVersion = applyVersion("com.pinterest:ktlint:0.48.2")
    val palantirJavaFormatVersion = applyVersion("com.palantir.javaformat:palantir-java-format:2.28.0")

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