package net.niebes.jsonschema2kotlin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project


@Suppress("unused")
class JsonSchema2KotlinPojoPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Add the 'greeting' extension object
        val extension = project.extensions.create("jsonSchema2KotlinPojo", JsonSchemaExtension::class.java)

        project.tasks.create("generateJsonSchema2KotlinPojo", GenerateJsonSchemaKotlinTask::class.java).apply {
            configuration = extension
        }
    }

}