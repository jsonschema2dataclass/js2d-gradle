package org.jsonschema2dataclass.internal.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkQueue
import org.gradle.workers.WorkerExecutor
import java.util.UUID
import javax.inject.Inject

abstract class Js2dGeneratorTaskBase<ConfigType> @Inject constructor(
    private val workerExecutor: WorkerExecutor,
) : DefaultTask() {
    @get:Internal // Cannot use @Nested on generic type - causes reflection NPE in Gradle 9 / IntelliJ sync
    abstract var configuration: ConfigType

    @get:OutputDirectory
    abstract val targetDirectory: DirectoryProperty

    @get:Internal
    abstract var uuid: UUID

    @get:Classpath
    val js2dConfiguration: Provider<out FileCollection> =
        project.configurations.named(JS2D_CONFIGURATION_NAME)

    @get:Classpath
    val js2dConfigurationPlugins: Provider<out FileCollection> = project.configurations.named(
        JS2D_PLUGINS_CONFIGURATION_NAME,
    )

    abstract fun submit(workQueue: WorkQueue)

    @TaskAction
    fun action() {
        val workerClassPath = js2dConfiguration.get() + js2dConfigurationPlugins
        val workQueue = workerExecutor.processIsolation {
            // Set encoding (work-around for https://github.com/gradle/gradle/issues/13843)
            // TODO: fixed in Gradle 8.3
            forkOptions.environment("LANG", System.getenv("LANG") ?: "C.UTF-8")

            classpath.from(workerClassPath)
        }
        submit(workQueue)
    }
}
