package org.jsonschema2dataclass.js2p

import org.gradle.kotlin.dsl.apply
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class PluginDslTest {
    @Test
    fun `plugin applies even without java plugin`() {
        ProjectBuilder.builder().build().run {
            apply(plugin = "org.jsonschema2dataclass")
        }
    }
}
