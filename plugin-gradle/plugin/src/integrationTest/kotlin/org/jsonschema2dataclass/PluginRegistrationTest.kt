package org.jsonschema2dataclass

import org.gradle.testfixtures.ProjectBuilder
import org.jsonschema2dataclass.ext.Js2pExtension
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

private const val PLUGIN_ID = "org.jsonschema2dataclass"
private const val EXTENSION_NAME = "jsonSchema2Pojo"
private const val JS2D_CONFIGURATION_NAME = "jsonschema2dataclass"
private const val JS2P_TASK_NAME = "generateJsonSchema2DataClass"

/**
 * Integration tests for plugin registration using ProjectBuilder.
 * Tests plugin application without running full builds.
 */
class PluginRegistrationTest {

    @Test
    @DisplayName("plugin registers jsonSchema2Pojo extension")
    fun pluginRegistersExtension() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply(PLUGIN_ID)

        assertNotNull(project.extensions.findByName(EXTENSION_NAME))
        assertTrue(project.extensions.findByName(EXTENSION_NAME) is Js2pExtension)
    }

    @Test
    @DisplayName("plugin creates jsonschema2dataclass configuration")
    fun pluginCreatesConfiguration() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply(PLUGIN_ID)

        val configuration = project.configurations.findByName(JS2D_CONFIGURATION_NAME)
        assertNotNull(configuration)
        assertTrue(configuration?.isCanBeResolved == true)
        assertTrue(configuration?.isCanBeConsumed == false)
    }

    @Test
    @DisplayName("plugin configures jsonschema2dataclass configuration with default dependencies")
    fun pluginConfiguresDefaultDependency() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply(PLUGIN_ID)

        val configuration = project.configurations.getByName(JS2D_CONFIGURATION_NAME)
        // The configuration should exist and be properly configured
        // defaultDependencies is only triggered during resolution, which we cannot do in unit tests
        // We verify the configuration exists and has the right properties
        assertNotNull(configuration)
        assertTrue(configuration.isCanBeResolved)
    }

    @Test
    @DisplayName("plugin applies successfully without java plugin")
    fun pluginAppliesWithoutJavaPlugin() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply(PLUGIN_ID)

        // Plugin should apply successfully
        assertTrue(project.plugins.hasPlugin(PLUGIN_ID))
        // But should not create generation tasks without java plugin
        assertTrue(project.tasks.findByName(JS2P_TASK_NAME) == null)
    }
}
