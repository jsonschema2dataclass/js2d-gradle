package org.jsonschema2dataclass.js2p

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import org.jsonschema2pojo.Jsonschema2Pojo
import org.jsonschema2pojo.RuleLogger
import java.util.UUID
import javax.inject.Inject

@DisableCachingByDefault
internal abstract class Js2pGenerationTask : DefaultTask() {
    @get:OutputDirectory
    abstract val targetDirectory: DirectoryProperty

    @get: Input
    abstract var configurationName: String

    @get: Input
    abstract var uuid: UUID

    @TaskAction
    fun action() {
        val pluginExtension = project.extensions.getByType(Js2pExtension::class.java)
        val configuration = pluginExtension.executions.named(configurationName).get()
        val config = Js2dConfig.fromConfig(configuration, pluginExtension, targetDirectory.asFile.get())
        logger.trace("Using this configuration:\n{}", config)
        Jsonschema2Pojo.generate(config, GradleRuleLogWrapper(logger))
    }
}

private class GradleRuleLogWrapper @Inject constructor(
    private val logger: Logger
) : RuleLogger {
    override fun isDebugEnabled(): Boolean =
        logger.isDebugEnabled

    override fun isErrorEnabled(): Boolean =
        logger.isErrorEnabled

    override fun isInfoEnabled(): Boolean =
        logger.isInfoEnabled

    override fun isTraceEnabled(): Boolean =
        logger.isTraceEnabled

    override fun isWarnEnabled(): Boolean =
        logger.isWarnEnabled

    override fun debug(msg: String?) {
        logger.debug(msg)
    }

    override fun error(msg: String?) {
        logger.debug(msg)
    }

    override fun error(msg: String?, e: Throwable?) {
        logger.error(msg, e)
    }

    override fun info(msg: String?) {
        logger.info(msg)
    }

    override fun trace(msg: String?) {
        logger.trace(msg)
    }

    override fun warn(msg: String?, e: Throwable?) {
        logger.warn(msg, e)
    }

    override fun warn(msg: String?) {
        logger.debug(msg)
    }
}
