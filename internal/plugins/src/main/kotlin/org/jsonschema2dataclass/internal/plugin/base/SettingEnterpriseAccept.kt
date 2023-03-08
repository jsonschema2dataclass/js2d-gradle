package org.jsonschema2dataclass.internal.plugin.base

import com.gradle.enterprise.gradleplugin.GradleEnterpriseExtension
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.findByType

@Suppress("unused")
class SettingEnterpriseAccept : Plugin<Settings> {
    override fun apply(settings: Settings) {
        if (settings.plugins.hasPlugin("com.gradle.enterprise")) {
            settings.extensions.findByType<GradleEnterpriseExtension>()?.apply {
                buildScan {
                    termsOfServiceUrl = "https://gradle.com/terms-of-service"
                    termsOfServiceAgree = "yes"
                }
            }
        }
    }
}
