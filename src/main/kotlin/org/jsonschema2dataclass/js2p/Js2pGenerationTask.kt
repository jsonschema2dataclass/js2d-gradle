package org.jsonschema2dataclass.js2p

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import org.jsonschema2pojo.Jsonschema2Pojo
import java.util.UUID

@DisableCachingByDefault
internal abstract class Js2pGenerationTask : DefaultTask() {
    @get:OutputDirectory
    abstract val targetDirectory: DirectoryProperty

    @get:InputFiles
    abstract val sourceFiles: ConfigurableFileCollection

    @get: Input
    abstract var configurationName: String

    @get: Input
    abstract var uuid: UUID

    @TaskAction
    fun action() {
        sourceFiles.forEach { it.mkdir() }
        val pluginExtension = project.extensions.getByType(Js2pExtension::class.java)
        val configuration = pluginExtension.executions.named(configurationName).get()
        val config = Js2dConfig.fromConfig(configuration, pluginExtension, targetDirectory.asFile.get())
        logger.trace("Using this configuration:\n{}", config)
        Jsonschema2Pojo.generate(config, GradleRuleLogWrapper(logger))
    }
}
