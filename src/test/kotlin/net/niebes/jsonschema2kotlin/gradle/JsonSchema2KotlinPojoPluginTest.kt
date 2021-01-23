package net.niebes.jsonschema2kotlin.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertTrue
import org.junit.Test

class JsonSchema2KotlinPojoPluginTest {
    @Test
    internal fun `can add generateJsonSchema2KotlinPojo task`() {
        val project: Project = ProjectBuilder.builder().build()
        project.pluginManager.apply("net.niebes.jsonschema2kotlin")

        assertTrue(project.tasks.getByName("generateJsonSchema2KotlinPojo") is GenerateJsonSchemaKotlinTask)
    }

}