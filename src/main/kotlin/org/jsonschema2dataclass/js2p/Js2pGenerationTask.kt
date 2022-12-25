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

        val js2pConfig = Js2dConfig.fromConfig(
                config.source,
                config.annotationStyle,
                config.classNamePrefix,
                config.classNameSuffix,
                config.constructorsRequiredPropertiesOnly,
                config.customAnnotator,
                config.customDatePattern,
                config.customDateTimePattern,
                config.customRuleFactory,
                config.customTimePattern,
                config.dateTimeType,
                config.dateType,
                config.fileExtensions,
                config.fileFilter,
                config.formatDateTimes,
                config.formatDates,
                config.formatTimes,
                config.formatTypeMapping,
                config.generateBuilders,
                config.includeAdditionalProperties,
                config.includeAllPropertiesConstructor,
                config.includeConstructorPropertiesAnnotation,
                config.includeConstructors,
                config.includeCopyConstructor,
                config.includeDynamicAccessors,
                config.includeDynamicBuilders,
                config.includeDynamicGetters,
                config.includeDynamicSetters,
                config.includeGeneratedAnnotation,
                config.includeGetters,
                config.includeHashcodeAndEquals,
                config.includeJsr303Annotations,
                config.includeJsr305Annotations,
                config.includeRequiredPropertiesConstructor,
                config.includeSetters,
                config.includeToString,
                config.includeTypeInfo,
                config.inclusionLevel,
                config.initializeCollections,
                config.outputEncoding,
                config.parcelable,
                config.propertyWordDelimiters,
                config.refFragmentPathDelimiters,
                config.removeOldOutput,
                config.serializable,
                config.sourceSortOrder,
                config.sourceType,
                config.targetPackage,
                config.targetVersion,
                config.timeType,
                config.toStringExcludes,
                config.useBigDecimals,
                config.useBigIntegers,
                config.useDoubleNumbers,
                config.useInnerClassBuilders,
                config.useJodaDates,
                config.useJodaLocalDates,
                config.useJodaLocalTimes,
                config.useLongIntegers,
                config.useOptionalForGetters,
                config.usePrimitives,
                config.useTitleAsClassname,
                config.useJakartaValidation,
                targetDirectory.asFile.get())
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
