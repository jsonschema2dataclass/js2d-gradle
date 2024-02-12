package org.jsonschema2dataclass.internal.js2p

import org.gradle.testfixtures.ProjectBuilder
import org.jsonschema2dataclass.ext.Js2pConfiguration
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.assertAll
import java.io.File
import java.util.UUID

private const val CONFIGURATION_NAME = "random_configuration"

class WorkerConvertTest {
    @RepeatedTest(100)
    @DisplayName("single execution")
    fun testExtensionConversions() {
        // create and randomize extension,
        val project = ProjectBuilder.builder().build()
        val configuration = randomize(
            project.extensions.create(
                CONFIGURATION_NAME,
                Js2pConfiguration::class.java,
                "name",
            ),
        )
        // convert to an intermediate configuration
        val newConfiguration = Js2pWorkerConfig.fromConfig(UUID.randomUUID(), File("/"), configuration)

        // generate "default" config
        val simpleConfig = randomizeGenerationConfig()
        // create a target configuration
        val js2pConfig = Js2pConfig(
            File("/"),
            newConfiguration.io,
            newConfiguration.klass,
            newConfiguration.constructors,
            newConfiguration.methods,
            newConfiguration.fields,
            newConfiguration.dateTime,
            simpleConfig,
        )
        // test if all conversions are correct
        assertAll(
            { checkIfEqual(configuration, newConfiguration) },
            { checkIfEqual(configuration, js2pConfig, simpleConfig) },
        )
    }
}
