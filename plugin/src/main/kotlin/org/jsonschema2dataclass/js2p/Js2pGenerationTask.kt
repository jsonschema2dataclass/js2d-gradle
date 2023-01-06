package org.jsonschema2dataclass.js2p

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.*
import org.gradle.workers.WorkerExecutor
import org.jsonschema2dataclass.JS2D_CONFIGURATION_NAME
import org.jsonschema2pojo.Jsonschema2Pojo
import org.jsonschema2pojo.RuleLogger
import java.util.*
import javax.inject.Inject

@CacheableTask
internal abstract class Js2pGenerationTask @Inject constructor(
    private val workerExecutor: WorkerExecutor,
): DefaultTask() {
    @get:OutputDirectory
    abstract val targetDirectory: DirectoryProperty

    @get:Nested
    abstract var configuration: Js2pConfiguration?

    @get: Internal
    abstract var uuid: UUID

    @get:Classpath
    val js2dConfiguration: NamedDomainObjectProvider<Configuration> = project.configurations.named(JS2D_CONFIGURATION_NAME)

    @TaskAction
    fun action() {
        if (configuration == null) {
            throw GradleException("Invalid task setup")
        }

        val workQueue = workerExecutor.processIsolation {
            // Set encoding (work-around for https://github.com/gradle/gradle/issues/13843)
            forkOptions.environment("LANG", System.getenv("LANG") ?: "C.UTF-8")

            classpath.from(js2dConfiguration.get().resolve())
        }
        val js2pConfig = Js2pWorkerConfig.fromConfig(targetDirectory, configuration!!)

        workQueue.submit(Js2pWorker::class.java) {
            config = js2pConfig
        }

    }
}
