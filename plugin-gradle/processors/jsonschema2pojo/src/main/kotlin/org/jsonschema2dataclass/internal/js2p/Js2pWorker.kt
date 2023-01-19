package org.jsonschema2dataclass.internal.js2p

import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.jsonschema2pojo.Jsonschema2Pojo
import org.jsonschema2pojo.RuleLogger

internal abstract class Js2pWorker : WorkAction<Js2pWorkerParams> {
    override fun execute() {
        val config = Js2pConfig(
            parameters.config.targetDirectory,
            parameters.config.io,
            parameters.config.klass,
            parameters.config.constructors,
            parameters.config.methods,
            parameters.config.fields,
            parameters.config.dateTime,
        )
        val ruleLogAdapter = GradleRuleLogAdapter(Logging.getLogger(this.javaClass))
        if (ruleLogAdapter.isTraceEnabled) {
            ruleLogAdapter.trace("[{}] Using this configuration:\n{}", parameters.config.uuid, config)
        }

        Jsonschema2Pojo.generate(config, ruleLogAdapter)
    }
}

internal interface Js2pWorkerParams : WorkParameters {
    var config: Js2pWorkerConfig
}

internal class GradleRuleLogAdapter constructor(
    private val logger: Logger,
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

    fun trace(msg: String?, arg: Any, arg2: Any) {
        logger.trace(msg, arg, arg2)
    }

    override fun warn(msg: String?, e: Throwable?) {
        logger.warn(msg, e)
    }

    override fun warn(msg: String?) {
        logger.debug(msg)
    }
}
