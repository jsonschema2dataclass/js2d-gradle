package org.jsonschema2dataclass.internal.task

import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.*
import org.gradle.workers.WorkQueue
import org.gradle.workers.WorkerExecutor
import java.util.*
import javax.inject.Inject

abstract class Js2dGeneratorTaskBase<ConfigType> @Inject constructor(
    private val workerExecutor: WorkerExecutor,
) : DefaultTask() {
    @get:Nested
    abstract var configuration: ConfigType // Should not be

    @get:OutputDirectory
    abstract val targetDirectory: DirectoryProperty

    @get: Internal
    abstract var uuid: UUID

    @get:Classpath
    val js2dConfiguration: NamedDomainObjectProvider<Configuration> =
        project.configurations.named(JS2D_CONFIGURATION_NAME)

    @get:Classpath
    val js2dConfigurationPlugins: NamedDomainObjectProvider<Configuration> = project.configurations.named(
        JS2D_PLUGINS_CONFIGURATION_NAME,
    )

    abstract fun submit(workQueue: WorkQueue)

    @TaskAction
    fun action() {
        val workerClassPath = js2dConfiguration.get() + js2dConfigurationPlugins
        val workQueue = workerExecutor.processIsolation {
            // Set encoding (work-around for https://github.com/gradle/gradle/issues/13843)
            forkOptions.environment("LANG", System.getenv("LANG") ?: "C.UTF-8")

            classpath.from(workerClassPath)
        }
        submit(workQueue)
    }
}
