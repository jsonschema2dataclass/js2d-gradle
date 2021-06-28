package om.github.js2d.plugin;

import org.gradle.api.logging.Logger;
import org.jsonschema2pojo.AbstractRuleLogger;

public class GradleRuleLogger extends AbstractRuleLogger {

    Logger logger;

    public GradleRuleLogger(Logger logger) {
        logger.info("Initializing {}", GradleRuleLogger.class);
        this.logger = logger;
    }

    @Override
    public void doDebug(String msg) {
        logger.debug(msg);
    }

    @Override
    protected void doError(String msg, Throwable e) {
        logger.error(msg, e);
    }

    @Override
    public void doInfo(String msg) {
        logger.info(msg);
    }

    @Override
    public void doTrace(String msg) {
        logger.trace(msg);
    }

    @Override
    protected void doWarn(String msg, Throwable e) {
        logger.warn(msg, e);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }
}
