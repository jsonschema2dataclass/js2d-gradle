package net.niebes.jsonschema2kotlin.gradle

import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.junit.Test
import java.io.File
import java.net.URI

class JsonSchemaPluginSpec {

    @Test
    fun java() {
        build("example/java");
    }

    private fun build(projectDir: String) {
        val connector = GradleConnector.newConnector().apply {
            useDistribution(URI("https://services.gradle.org/distributions/gradle-6.8-bin.zip"))
            forProjectDirectory(File(projectDir))
        }

        val connection: ProjectConnection = connector.connect()
        connection.use {
            connection.newBuild().apply {
                forTasks("build")
                run()
            }
        }
    }
}