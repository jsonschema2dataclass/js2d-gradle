package org.jsonschema2dataclass

import org.gradle.testfixtures.ProjectBuilder
import org.jsonschema2dataclass.ext.Js2pExtension
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

private const val PLUGIN_ID = "org.jsonschema2dataclass"
private const val JS2P_TASK_NAME = "generateJsonSchema2DataClass"

/**
 * Integration tests for task wiring using ProjectBuilder.
 * Tests task registration and dependencies without running full builds.
 */
class TaskWiringTest {

    @Test
    @DisplayName("java plugin triggers task registration")
    fun javaPluginTriggersTaskRegistration() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("java")
        project.plugins.apply(PLUGIN_ID)

        val ext = project.extensions.getByType(Js2pExtension::class.java)
        ext.executions.create("main")

        // Force task creation by evaluating
        project.evaluate()

        assertNotNull(project.tasks.findByName("${JS2P_TASK_NAME}ConfigMain"))
        assertNotNull(project.tasks.findByName(JS2P_TASK_NAME))
    }

    @Test
    @DisplayName("wrapper task depends on all execution tasks")
    fun wrapperTaskDependsOnAllExecutionTasks() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("java")
        project.plugins.apply(PLUGIN_ID)

        val ext = project.extensions.getByType(Js2pExtension::class.java)
        ext.executions.create("first")
        ext.executions.create("second")

        project.evaluate()

        val wrapperTask = project.tasks.findByName(JS2P_TASK_NAME)
        assertNotNull(wrapperTask)

        val dependencies = wrapperTask?.taskDependencies?.getDependencies(wrapperTask)
        assertTrue(dependencies?.any { it.name == "${JS2P_TASK_NAME}ConfigFirst" } == true)
        assertTrue(dependencies?.any { it.name == "${JS2P_TASK_NAME}ConfigSecond" } == true)
    }

    @Test
    @DisplayName("plugin without java plugin does not create tasks")
    fun pluginWithoutJavaPluginDoesNotCreateTasks() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply(PLUGIN_ID)

        val ext = project.extensions.getByType(Js2pExtension::class.java)
        ext.executions.create("main")

        // Even with executions defined, no tasks without java plugin
        assertNull(project.tasks.findByName(JS2P_TASK_NAME))
        assertNull(project.tasks.findByName("${JS2P_TASK_NAME}ConfigMain"))
    }

    @Test
    @DisplayName("java-library applied after org.jsonschema2dataclass")
    fun lazyApplicationOrderWorks() {
        val project = ProjectBuilder.builder().build()

        // Apply our plugin first
        project.plugins.apply(PLUGIN_ID)

        val ext = project.extensions.getByType(Js2pExtension::class.java)
        ext.executions.create("main")

        // Then apply java-library later (lazy trigger)
        project.plugins.apply("java-library")
        project.evaluate()

        // Tasks should still be created due to withPlugin callback
        assertNotNull(project.tasks.findByName(JS2P_TASK_NAME))
        assertNotNull(project.tasks.findByName("${JS2P_TASK_NAME}ConfigMain"))
    }
}

// Extension function to evaluate project
private fun org.gradle.api.Project.evaluate() {
    (this as org.gradle.api.internal.project.ProjectInternal).evaluate()
}
