package org.jsonschema2dataclass.js2p

import org.gradle.api.logging.Logger
import org.jsonschema2pojo.RuleLogger
import javax.inject.Inject

internal class GradleRuleLogWrapper @Inject constructor(
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
