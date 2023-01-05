package org.jsonschema2dataclass.js2p

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.*
import org.jsonschema2pojo.Jsonschema2Pojo
import org.jsonschema2pojo.RuleLogger
import java.util.*
import javax.inject.Inject

@CacheableTask
internal abstract class Js2pGenerationTask : DefaultTask() {
    @get:OutputDirectory
    abstract val targetDirectory: DirectoryProperty

    @get:Nested
    abstract var configuration: Js2pConfiguration?

    @get: Internal
    abstract var uuid: UUID

    @TaskAction
    fun action() {
        val config = configuration ?: throw GradleException("Invalid task setup")

        val js2pConfig = Js2pConfig.fromConfig(targetDirectory, config)
        logger.trace("Using this configuration:\n{}", js2pConfig)
        Jsonschema2Pojo.generate(js2pConfig, GradleRuleLogWrapper(logger))
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