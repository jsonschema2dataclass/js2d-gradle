package org.jsonschema2dataclass.internal.plugin.base

import com.gradle.develocity.agent.gradle.DevelocityConfiguration
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.findByType

/** Configure Gradle Develocity plugin (former Gralde Enterprise) in settings.gradle.kts */
@Suppress("unused")
class SettingEnterpriseAccept : Plugin<Settings> {
    override fun apply(settings: Settings) {
        if (settings.plugins.hasPlugin("com.gradle.develocity")) {
            settings.extensions.findByType<DevelocityConfiguration>()?.apply {
                buildScan {
                    termsOfUseUrl.set("https://gradle.com/terms-of-service")
                    termsOfUseAgree.set("yes")
                    publishing.onlyIf {
                        it.buildResult.failures.isNotEmpty()
                    }
                }
            }
        }
    }
}
