package org.jsonschema2dataclass.js2p

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.logging.Logger
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.gradle.work.DisableCachingByDefault
import org.gradle.work.NormalizeLineEndings
import org.jsonschema2pojo.Jsonschema2Pojo
import org.jsonschema2pojo.RuleLogger
import java.io.FileFilter
import java.util.UUID
import javax.inject.Inject

@CacheableTask
internal abstract class Js2pGenerationTask : DefaultTask() {
    @get:OutputDirectory
    abstract val targetDirectory: DirectoryProperty

    @get:InputFiles
    @get:IgnoreEmptyDirectories
    @get:NormalizeLineEndings
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val source: ConfigurableFileCollection

    @get:Input
    @get: Optional
    abstract var annotationStyle: Provider<String>

    @get:Input
    @get: Optional
    abstract var classNamePrefix: Provider<String>

    @get:Input
    @get: Optional
    abstract var classNameSuffix: Provider<String>

    @get:Input
    @get: Optional
    abstract var constructorsRequiredPropertiesOnly: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var customAnnotator: Provider<String>

    @get:Input
    @get: Optional
    abstract var customDatePattern: Provider<String>

    @get:Input
    @get: Optional
    abstract var customDateTimePattern: Provider<String>

    @get:Input
    @get: Optional
    abstract var customRuleFactory: Provider<String>

    @get:Input
    @get: Optional
    abstract var customTimePattern: Provider<String>

    @get:Input
    @get: Optional
    abstract var dateTimeType: Provider<String>

    @get:Input
    @get: Optional
    abstract var dateType: Provider<String>

    @get:Input
    @get: Optional
    abstract var fileExtensions: Provider<Set<String>>

    @get:Input
    @get: Optional
    abstract var fileFilter: Provider<FileFilter>

    @get:Input
    @get: Optional
    abstract var formatDateTimes: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var formatDates: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var formatTimes: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var formatTypeMapping: Provider<Map<String, String>>

    @get:Input
    @get: Optional
    abstract var generateBuilders: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var includeAdditionalProperties: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var includeAllPropertiesConstructor: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var includeConstructorPropertiesAnnotation: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var includeConstructors: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var includeCopyConstructor: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var includeDynamicAccessors: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var includeDynamicBuilders: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var includeDynamicGetters: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var includeDynamicSetters: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var includeGeneratedAnnotation: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var includeGetters: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var includeHashcodeAndEquals: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var includeJsr303Annotations: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var includeJsr305Annotations: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var includeRequiredPropertiesConstructor: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var includeSetters: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var includeToString: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var includeTypeInfo: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var inclusionLevel: Provider<String>

    @get:Input
    @get: Optional
    abstract var initializeCollections: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var outputEncoding: Provider<String>

    @get:Input
    @get: Optional
    abstract var parcelable: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var propertyWordDelimiters: Provider<String>

    @get:Input
    @get: Optional
    abstract var refFragmentPathDelimiters: Provider<String>

    @get:Input
    @get: Optional
    abstract var removeOldOutput: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var serializable: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var sourceSortOrder: Provider<String>

    @get:Input
    @get: Optional
    abstract var sourceType: Provider<String>

    @get:Input
    @get: Optional
    abstract var targetPackage: Provider<String>

    @get:Input
    @get: Optional
    abstract var targetVersion: Provider<String>

    @get:Input
    @get: Optional
    abstract var timeType: Provider<String>

    @get:Input
    @get: Optional
    abstract var toStringExcludes: Provider<Set<String>>

    @get:Input
    @get: Optional
    abstract var useBigDecimals: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var useBigIntegers: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var useDoubleNumbers: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var useInnerClassBuilders: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var useJodaDates: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var useJodaLocalDates: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var useJodaLocalTimes: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var useLongIntegers: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var useOptionalForGetters: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var usePrimitives: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var useTitleAsClassname: Provider<Boolean>

    @get:Input
    @get: Optional
    abstract var useJakartaValidation: Provider<Boolean>

    @get: Internal
    abstract var uuid: UUID

    @TaskAction
    fun action() {
        val config = Js2dConfig.fromConfig(
                source,
                annotationStyle,
                classNamePrefix,
                classNameSuffix,
                constructorsRequiredPropertiesOnly,
                customAnnotator,
                customDatePattern,
                customDateTimePattern,
                customRuleFactory,
                customTimePattern,
                dateTimeType,
                dateType,
                fileExtensions,
                fileFilter,
                formatDateTimes,
                formatDates,
                formatTimes,
                formatTypeMapping,
                generateBuilders,
                includeAdditionalProperties,
                includeAllPropertiesConstructor,
                includeConstructorPropertiesAnnotation,
                includeConstructors,
                includeCopyConstructor,
                includeDynamicAccessors,
                includeDynamicBuilders,
                includeDynamicGetters,
                includeDynamicSetters,
                includeGeneratedAnnotation,
                includeGetters,
                includeHashcodeAndEquals,
                includeJsr303Annotations,
                includeJsr305Annotations,
                includeRequiredPropertiesConstructor,
                includeSetters,
                includeToString,
                includeTypeInfo,
                inclusionLevel,
                initializeCollections,
                outputEncoding,
                parcelable,
                propertyWordDelimiters,
                refFragmentPathDelimiters,
                removeOldOutput,
                serializable,
                sourceSortOrder,
                sourceType,
                targetPackage,
                targetVersion,
                timeType,
                toStringExcludes,
                useBigDecimals,
                useBigIntegers,
                useDoubleNumbers,
                useInnerClassBuilders,
                useJodaDates,
                useJodaLocalDates,
                useJodaLocalTimes,
                useLongIntegers,
                useOptionalForGetters,
                usePrimitives,
                useTitleAsClassname,
                useJakartaValidation,
                targetDirectory.asFile.get())

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
