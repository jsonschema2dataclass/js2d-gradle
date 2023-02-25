package org.jsonschema2dataclass.internal.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.CacheableRule
import org.gradle.api.artifacts.ComponentMetadataContext
import org.gradle.api.artifacts.ComponentMetadataRule
import org.gradle.api.attributes.java.TargetJvmVersion
import org.gradle.kotlin.dsl.withModule
import javax.inject.Inject

private val compatComponentsDefault = listOf(
    "com.android.tools.build:gradle",
    "com.android.tools.build:gradle-api",
    "com.android.tools.build:builder",
    "com.android.tools.build:builder-model",
    "com.android.tools.build:manifest-merger",
)

@Suppress("unused")
class AGPCompat8Plugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.afterEvaluate {
            dependencies.components {
                // This project still uses Java 8, so let's rewrite the metadata,
                // so that the produced jars can still be used with Java 8.
                // https://docs.gradle.org/current/userguide/component_metadata_rules.html
                for (component in compatComponentsDefault) {
                    withModule<TargetJvmVersionRule>(component) { params(8) }
                }
            }
        }
    }
}

/**
 * taken from https://github.com/TWiStErRob/net.twisterrob.gradle
 * licence: MIT licence
 */
@CacheableRule
abstract class TargetJvmVersionRule @Inject constructor(
    private val jvmVersionOverride: Int,
) : ComponentMetadataRule {

    override fun execute(context: ComponentMetadataContext) {
        context.details.allVariants {
            attributes {
                attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, jvmVersionOverride)
            }
        }
    }
}
