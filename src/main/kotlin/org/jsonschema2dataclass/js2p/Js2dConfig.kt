package org.jsonschema2dataclass.js2p

import org.gradle.api.GradleScriptException
import org.gradle.api.provider.Provider
import org.jsonschema2pojo.*
import org.jsonschema2pojo.rules.RuleFactory
import java.io.File
import java.io.FileFilter
import java.net.URL
import java.util.function.Function
import kotlin.reflect.KFunction1

internal data class Js2dConfig(
    private val targetDirectory: File,
    private val sourceFiles: List<URL>,

    private val annotationStyle: AnnotationStyle,
    private val classNamePrefix: String?,
    private val classNameSuffix: String?,
    private val constructorsRequiredPropertiesOnly: Boolean,
    private val customAnnotator: Class<out Annotator>,
    private val customDatePattern: String?,
    private val customDateTimePattern: String?,
    private val customRuleFactory: Class<out RuleFactory>,
    private val customTimePattern: String?,
    private val dateTimeType: String?,
    private val dateType: String?,
    private val fileExtensions: Array<String>,
    private val fileFilter: FileFilter,
    private val formatDateTimes: Boolean,
    private val formatDates: Boolean,
    private val formatTimes: Boolean,
    private val formatTypeMapping: Map<String, String>,
    private val generateBuilders: Boolean,
    private val includeAdditionalProperties: Boolean,
    private val includeAllPropertiesConstructor: Boolean,
    private val includeConstructorPropertiesAnnotation: Boolean,
    private val includeConstructors: Boolean,
    private val includeCopyConstructor: Boolean,
    private val includeDynamicAccessors: Boolean,
    private val includeDynamicBuilders: Boolean,
    private val includeDynamicGetters: Boolean,
    private val includeDynamicSetters: Boolean,
    private val includeGeneratedAnnotation: Boolean,
    private val includeGetters: Boolean,
    private val includeHashcodeAndEquals: Boolean,
    private val includeJsr303Annotations: Boolean,
    private val includeJsr305Annotations: Boolean,
    private val includeRequiredPropertiesConstructor: Boolean,
    private val includeSetters: Boolean,
    private val includeToString: Boolean,
    private val includeTypeInfo: Boolean,
    private val inclusionLevel: InclusionLevel,
    private val initializeCollections: Boolean,
    private val outputEncoding: String?,
    private val parcelable: Boolean,
    private val propertyWordDelimiters: CharArray,
    private val refFragmentPathDelimiters: String?,
    private val removeOldOutput: Boolean,
    private val serializable: Boolean,
    private val sourceSortOrder: SourceSortOrder,
    private val sourceType: SourceType,
    private val targetPackage: String?,
    private val targetVersion: String?,
    private val timeType: String?,
    private val toStringExcludes: Array<String>,
    private val useBigDecimals: Boolean,
    private val useBigIntegers: Boolean,
    private val useDoubleNumbers: Boolean,
    private val useInnerClassBuilders: Boolean,
    private val useJodaDates: Boolean,
    private val useJodaLocalDates: Boolean,
    private val useJodaLocalTimes: Boolean,
    private val useLongIntegers: Boolean,
    private val useOptionalForGetters: Boolean,
    private val usePrimitives: Boolean,
    private val useJakartaValidation: Boolean,
    private val useTitleAsClassname: Boolean,
) : GenerationConfig, java.io.Serializable {
    companion object {
        private val defaultConfiguration = DefaultGenerationConfig()

        private fun <E : Enum<E>?> fromEnum(provider: Provider<String>, enumClass: Class<E>): E? {
            val value = provider.orNull
            return if (value == null || value.isEmpty()) {
                null
            } else {
                java.lang.Enum.valueOf(
                    enumClass,
                    value.toUpperCase()
                )
            }
        }

        private fun <C> findClass(className: String?): Class<out C>? {
            if (className == null || className.isEmpty()) {
                return null
            }
            return try {
                @Suppress("UNCHECKED_CAST")
                Class.forName(className, true, Js2dConfig::class.java.classLoader) as Class<C>
            } catch (e: ClassNotFoundException) {
                throw GradleScriptException("Unable to find class $className", e)
            }
        }

        private fun <V> maybeDefaultRaw(value: V?, vFunction: Function<DefaultGenerationConfig, V>): V {
            return value ?: vFunction.apply(defaultConfiguration)
        }

        private fun maybeDefaultChar(
            provider: Provider<String>,
            vFunction: Function<DefaultGenerationConfig, CharArray>
        ): CharArray {
            val value = provider.orNull
            return if (value == null || value.isEmpty()) {
                vFunction.apply(defaultConfiguration)
            } else {
                value.toCharArray()
            }
        }

        private fun <V> maybeDefault(value: Provider<V>, vFunction: Function<DefaultGenerationConfig, V>): V {
            return maybeDefaultRaw(value.orNull, vFunction)
        }

        private inline fun <reified K> maybeDefaultClass(value: Provider<String>, vFunction: Function<DefaultGenerationConfig, Class<out K>>): Class<out K> {
            return maybeDefaultRaw(findClass(value.orNull), vFunction)
        }

        private inline fun <reified V> maybeDefaultArray(value: Provider<Set<V>>, vFunction: Function<DefaultGenerationConfig, Array<V>>): Array<V> {
            return maybeDefaultRaw(value.orNull?.toTypedArray(), vFunction)
        }

        private inline fun <reified V: Enum<V>?> maybeDefaultEnum(value: Provider<String>, vFunction: Function<DefaultGenerationConfig, V>): V {
            return fromEnum(value, V::class.java) ?: vFunction.apply(defaultConfiguration)
        }

        fun fromConfig(
            config: Js2pConfiguration,
            targetDirectory: File
        ): Js2dConfig =
            Js2dConfig(
                targetDirectory,
                config.source.map { it.toURI().toURL() },
                maybeDefaultEnum(config.annotationStyle) {it.annotationStyle },
                maybeDefault(config.classNamePrefix) {it.classNamePrefix },
                maybeDefault(config.classNameSuffix) {it.classNameSuffix },
                maybeDefault(config.constructorsRequiredPropertiesOnly) {it.isConstructorsRequiredPropertiesOnly },
                maybeDefaultClass(config.customAnnotator) {it.customAnnotator },
                maybeDefault(config.customDatePattern) {it.customDatePattern },
                maybeDefault(config.customDateTimePattern) {it.customDateTimePattern },
                maybeDefaultClass(config.customRuleFactory) {it.customRuleFactory },
                maybeDefault(config.customTimePattern) {it.customTimePattern },
                maybeDefault(config.dateTimeType) {it.dateTimeType },
                maybeDefault(config.dateType) {it.dateType },
                maybeDefaultArray(config.fileExtensions) {it.fileExtensions },
                maybeDefault(config.fileFilter) {it.fileFilter },
                maybeDefault(config.formatDateTimes) {it.isFormatDateTimes },
                maybeDefault(config.formatDates) {it.isFormatDates },
                maybeDefault(config.formatTimes) {it.isFormatTimes },
                maybeDefault(config.formatTypeMapping) {it.formatTypeMapping },
                maybeDefault(config.generateBuilders) {it.isGenerateBuilders },
                maybeDefault(config.includeAdditionalProperties) {it.isIncludeAdditionalProperties },
                maybeDefault(config.includeAllPropertiesConstructor) {it.isIncludeAllPropertiesConstructor },
                maybeDefault(config.includeConstructorPropertiesAnnotation) {it.isIncludeConstructorPropertiesAnnotation },
                maybeDefault(config.includeConstructors) {it.isIncludeConstructors },
                maybeDefault(config.includeCopyConstructor) {it.isIncludeCopyConstructor },
                maybeDefault(config.includeDynamicAccessors) {it.isIncludeDynamicAccessors },
                maybeDefault(config.includeDynamicBuilders) {it.isIncludeDynamicBuilders },
                maybeDefault(config.includeDynamicGetters) {it.isIncludeDynamicGetters },
                maybeDefault(config.includeDynamicSetters) {it.isIncludeDynamicSetters },
                maybeDefault(config.includeGeneratedAnnotation) {it.isIncludeGeneratedAnnotation },
                maybeDefault(config.includeGetters) {it.isIncludeGetters },
                maybeDefault(config.includeHashcodeAndEquals) {it.isIncludeHashcodeAndEquals },
                maybeDefault(config.includeJsr303Annotations) {it.isIncludeJsr303Annotations },
                maybeDefault(config.includeJsr305Annotations) {it.isIncludeJsr305Annotations },
                maybeDefault(config.includeRequiredPropertiesConstructor) {it.isIncludeRequiredPropertiesConstructor },
                maybeDefault(config.includeSetters) {it.isIncludeSetters },
                maybeDefault(config.includeToString) {it.isIncludeToString },
                maybeDefault(config.includeTypeInfo) {it.isIncludeTypeInfo },
                maybeDefaultEnum(config.inclusionLevel) {it.inclusionLevel },
                maybeDefault(config.initializeCollections) {it.isInitializeCollections },
                maybeDefault(config.outputEncoding) {it.outputEncoding },
                maybeDefault(config.parcelable) {it.isParcelable },
                maybeDefaultChar(config.propertyWordDelimiters) {it.propertyWordDelimiters },
                maybeDefault(config.refFragmentPathDelimiters) {it.refFragmentPathDelimiters },
                maybeDefault(config.removeOldOutput) {it.isRemoveOldOutput },
                maybeDefault(config.serializable) {it.isSerializable },
                maybeDefaultEnum(config.sourceSortOrder) {it.sourceSortOrder },
                maybeDefaultEnum(config.sourceType) {it.sourceType },
                maybeDefault(config.targetPackage) {it.targetPackage },
                maybeDefault(config.targetVersion) {it.targetVersion },
                maybeDefault(config.timeType) {it.timeType },
                maybeDefaultArray(config.toStringExcludes) {it.toStringExcludes },
                maybeDefault(config.useBigDecimals) {it.isUseBigDecimals },
                maybeDefault(config.useBigIntegers) {it.isUseBigIntegers },
                maybeDefault(config.useDoubleNumbers) {it.isUseDoubleNumbers },
                maybeDefault(config.useInnerClassBuilders) {it.isUseInnerClassBuilders },
                maybeDefault(config.useJodaDates) {it.isUseJodaDates },
                maybeDefault(config.useJodaLocalDates) {it.isUseJodaLocalDates },
                maybeDefault(config.useJodaLocalTimes) {it.isUseJodaLocalTimes },
                maybeDefault(config.useLongIntegers) {it.isUseLongIntegers },
                maybeDefault(config.useOptionalForGetters) {it.isUseOptionalForGetters },
                maybeDefault(config.usePrimitives) {it.isUsePrimitives },
                maybeDefault(config.useJakartaValidation) {it.isUseJakartaValidation },
                maybeDefault(config.useTitleAsClassname) {it.isUseTitleAsClassname }
            )
    }

    override fun isGenerateBuilders(): Boolean = generateBuilders
    override fun isIncludeTypeInfo(): Boolean = includeTypeInfo
    override fun isIncludeConstructorPropertiesAnnotation(): Boolean = includeConstructorPropertiesAnnotation
    override fun isUsePrimitives(): Boolean = usePrimitives
    override fun getSource(): Iterator<URL> = sourceFiles.iterator()
    override fun getTargetDirectory(): File = targetDirectory
    override fun getTargetPackage(): String? = targetPackage
    override fun getPropertyWordDelimiters(): CharArray = propertyWordDelimiters
    override fun isUseLongIntegers(): Boolean = useLongIntegers
    override fun isUseBigIntegers(): Boolean = useBigIntegers
    override fun isUseDoubleNumbers(): Boolean = useDoubleNumbers
    override fun isUseBigDecimals(): Boolean = useBigDecimals
    override fun isIncludeHashcodeAndEquals(): Boolean = includeHashcodeAndEquals
    override fun isIncludeToString(): Boolean = includeToString
    override fun getToStringExcludes(): Array<String> = toStringExcludes
    override fun getAnnotationStyle(): AnnotationStyle = annotationStyle
    override fun isUseTitleAsClassname(): Boolean = useTitleAsClassname
    override fun getInclusionLevel(): InclusionLevel = inclusionLevel
    override fun getCustomAnnotator(): Class<out Annotator> = customAnnotator
    override fun getCustomRuleFactory(): Class<out RuleFactory> = customRuleFactory
    override fun isIncludeJsr303Annotations(): Boolean = includeJsr303Annotations
    override fun isIncludeJsr305Annotations(): Boolean = includeJsr305Annotations
    override fun isUseOptionalForGetters(): Boolean = useOptionalForGetters
    override fun getSourceType(): SourceType = sourceType
    override fun isRemoveOldOutput(): Boolean = removeOldOutput
    override fun getOutputEncoding(): String? = outputEncoding
    override fun isUseJodaDates(): Boolean = useJodaDates
    override fun isUseJodaLocalDates(): Boolean = useJodaLocalDates
    override fun isUseJodaLocalTimes(): Boolean = useJodaLocalTimes
    override fun isParcelable(): Boolean = parcelable
    override fun isSerializable(): Boolean = serializable
    override fun getFileFilter(): FileFilter = fileFilter
    override fun isInitializeCollections(): Boolean = initializeCollections
    override fun getClassNamePrefix(): String? = classNamePrefix
    override fun getClassNameSuffix(): String? = classNameSuffix
    override fun getFileExtensions(): Array<String> = fileExtensions
    override fun isIncludeConstructors(): Boolean = includeConstructors
    override fun isConstructorsRequiredPropertiesOnly(): Boolean = constructorsRequiredPropertiesOnly
    override fun isIncludeRequiredPropertiesConstructor(): Boolean = includeRequiredPropertiesConstructor
    override fun isIncludeAllPropertiesConstructor(): Boolean = includeAllPropertiesConstructor
    override fun isIncludeCopyConstructor(): Boolean = includeCopyConstructor
    override fun isIncludeAdditionalProperties(): Boolean = includeAdditionalProperties
    override fun isIncludeGetters(): Boolean = includeGetters
    override fun isIncludeSetters(): Boolean = includeSetters
    override fun getTargetVersion(): String? = targetVersion
    override fun isIncludeDynamicAccessors(): Boolean = includeDynamicAccessors
    override fun isIncludeDynamicGetters(): Boolean = includeDynamicGetters
    override fun isIncludeDynamicSetters(): Boolean = includeDynamicSetters
    override fun isIncludeDynamicBuilders(): Boolean = includeDynamicBuilders
    override fun getDateTimeType(): String? = dateTimeType
    override fun getDateType(): String? = dateType
    override fun getTimeType(): String? = timeType
    override fun isFormatDates(): Boolean = formatDates
    override fun isFormatTimes(): Boolean = formatTimes
    override fun isFormatDateTimes(): Boolean = formatDateTimes
    override fun getCustomDatePattern(): String? = customDatePattern
    override fun getCustomTimePattern(): String? = customTimePattern
    override fun getCustomDateTimePattern(): String? = customDateTimePattern
    override fun getRefFragmentPathDelimiters(): String? = refFragmentPathDelimiters
    override fun getSourceSortOrder(): SourceSortOrder = sourceSortOrder
    override fun getFormatTypeMapping(): Map<String, String> = formatTypeMapping
    override fun isIncludeGeneratedAnnotation(): Boolean = includeGeneratedAnnotation
    override fun isUseJakartaValidation(): Boolean = useJakartaValidation
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Js2dConfig

        if (targetDirectory != other.targetDirectory) return false
        if (sourceFiles != other.sourceFiles) return false
        if (annotationStyle != other.annotationStyle) return false
        if (classNamePrefix != other.classNamePrefix) return false
        if (classNameSuffix != other.classNameSuffix) return false
        if (constructorsRequiredPropertiesOnly != other.constructorsRequiredPropertiesOnly) return false
        if (customAnnotator != other.customAnnotator) return false
        if (customDatePattern != other.customDatePattern) return false
        if (customDateTimePattern != other.customDateTimePattern) return false
        if (customRuleFactory != other.customRuleFactory) return false
        if (customTimePattern != other.customTimePattern) return false
        if (dateTimeType != other.dateTimeType) return false
        if (dateType != other.dateType) return false
        if (!fileExtensions.contentEquals(other.fileExtensions)) return false
        if (fileFilter != other.fileFilter) return false
        if (formatDateTimes != other.formatDateTimes) return false
        if (formatDates != other.formatDates) return false
        if (formatTimes != other.formatTimes) return false
        if (formatTypeMapping != other.formatTypeMapping) return false
        if (generateBuilders != other.generateBuilders) return false
        if (includeAdditionalProperties != other.includeAdditionalProperties) return false
        if (includeAllPropertiesConstructor != other.includeAllPropertiesConstructor) return false
        if (includeConstructorPropertiesAnnotation != other.includeConstructorPropertiesAnnotation) return false
        if (includeConstructors != other.includeConstructors) return false
        if (includeCopyConstructor != other.includeCopyConstructor) return false
        if (includeDynamicAccessors != other.includeDynamicAccessors) return false
        if (includeDynamicBuilders != other.includeDynamicBuilders) return false
        if (includeDynamicGetters != other.includeDynamicGetters) return false
        if (includeDynamicSetters != other.includeDynamicSetters) return false
        if (includeGeneratedAnnotation != other.includeGeneratedAnnotation) return false
        if (includeGetters != other.includeGetters) return false
        if (includeHashcodeAndEquals != other.includeHashcodeAndEquals) return false
        if (includeJsr303Annotations != other.includeJsr303Annotations) return false
        if (includeJsr305Annotations != other.includeJsr305Annotations) return false
        if (includeRequiredPropertiesConstructor != other.includeRequiredPropertiesConstructor) return false
        if (includeSetters != other.includeSetters) return false
        if (includeToString != other.includeToString) return false
        if (includeTypeInfo != other.includeTypeInfo) return false
        if (inclusionLevel != other.inclusionLevel) return false
        if (initializeCollections != other.initializeCollections) return false
        if (outputEncoding != other.outputEncoding) return false
        if (parcelable != other.parcelable) return false
        if (!propertyWordDelimiters.contentEquals(other.propertyWordDelimiters)) return false
        if (refFragmentPathDelimiters != other.refFragmentPathDelimiters) return false
        if (removeOldOutput != other.removeOldOutput) return false
        if (serializable != other.serializable) return false
        if (sourceSortOrder != other.sourceSortOrder) return false
        if (sourceType != other.sourceType) return false
        if (targetPackage != other.targetPackage) return false
        if (targetVersion != other.targetVersion) return false
        if (timeType != other.timeType) return false
        if (!toStringExcludes.contentEquals(other.toStringExcludes)) return false
        if (useBigDecimals != other.useBigDecimals) return false
        if (useBigIntegers != other.useBigIntegers) return false
        if (useDoubleNumbers != other.useDoubleNumbers) return false
        if (useInnerClassBuilders != other.useInnerClassBuilders) return false
        if (useJodaDates != other.useJodaDates) return false
        if (useJodaLocalDates != other.useJodaLocalDates) return false
        if (useJodaLocalTimes != other.useJodaLocalTimes) return false
        if (useLongIntegers != other.useLongIntegers) return false
        if (useOptionalForGetters != other.useOptionalForGetters) return false
        if (usePrimitives != other.usePrimitives) return false
        if (useTitleAsClassname != other.useTitleAsClassname) return false
        if (useJakartaValidation != other.useJakartaValidation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = targetDirectory.hashCode()
        result = 31 * result + sourceFiles.hashCode()
        result = 31 * result + annotationStyle.hashCode()
        result = 31 * result + classNamePrefix.hashCode()
        result = 31 * result + classNameSuffix.hashCode()
        result = 31 * result + constructorsRequiredPropertiesOnly.hashCode()
        result = 31 * result + customAnnotator.hashCode()
        result = 31 * result + customDatePattern.hashCode()
        result = 31 * result + customDateTimePattern.hashCode()
        result = 31 * result + customRuleFactory.hashCode()
        result = 31 * result + customTimePattern.hashCode()
        result = 31 * result + dateTimeType.hashCode()
        result = 31 * result + dateType.hashCode()
        result = 31 * result + fileExtensions.contentHashCode()
        result = 31 * result + fileFilter.hashCode()
        result = 31 * result + formatDateTimes.hashCode()
        result = 31 * result + formatDates.hashCode()
        result = 31 * result + formatTimes.hashCode()
        result = 31 * result + formatTypeMapping.hashCode()
        result = 31 * result + generateBuilders.hashCode()
        result = 31 * result + includeAdditionalProperties.hashCode()
        result = 31 * result + includeAllPropertiesConstructor.hashCode()
        result = 31 * result + includeConstructorPropertiesAnnotation.hashCode()
        result = 31 * result + includeConstructors.hashCode()
        result = 31 * result + includeCopyConstructor.hashCode()
        result = 31 * result + includeDynamicAccessors.hashCode()
        result = 31 * result + includeDynamicBuilders.hashCode()
        result = 31 * result + includeDynamicGetters.hashCode()
        result = 31 * result + includeDynamicSetters.hashCode()
        result = 31 * result + includeGeneratedAnnotation.hashCode()
        result = 31 * result + includeGetters.hashCode()
        result = 31 * result + includeHashcodeAndEquals.hashCode()
        result = 31 * result + includeJsr303Annotations.hashCode()
        result = 31 * result + includeJsr305Annotations.hashCode()
        result = 31 * result + includeRequiredPropertiesConstructor.hashCode()
        result = 31 * result + includeSetters.hashCode()
        result = 31 * result + includeToString.hashCode()
        result = 31 * result + includeTypeInfo.hashCode()
        result = 31 * result + inclusionLevel.hashCode()
        result = 31 * result + initializeCollections.hashCode()
        result = 31 * result + outputEncoding.hashCode()
        result = 31 * result + parcelable.hashCode()
        result = 31 * result + propertyWordDelimiters.contentHashCode()
        result = 31 * result + refFragmentPathDelimiters.hashCode()
        result = 31 * result + removeOldOutput.hashCode()
        result = 31 * result + serializable.hashCode()
        result = 31 * result + sourceSortOrder.hashCode()
        result = 31 * result + sourceType.hashCode()
        result = 31 * result + targetPackage.hashCode()
        result = 31 * result + targetVersion.hashCode()
        result = 31 * result + timeType.hashCode()
        result = 31 * result + toStringExcludes.contentHashCode()
        result = 31 * result + useBigDecimals.hashCode()
        result = 31 * result + useBigIntegers.hashCode()
        result = 31 * result + useDoubleNumbers.hashCode()
        result = 31 * result + useInnerClassBuilders.hashCode()
        result = 31 * result + useJodaDates.hashCode()
        result = 31 * result + useJodaLocalDates.hashCode()
        result = 31 * result + useJodaLocalTimes.hashCode()
        result = 31 * result + useLongIntegers.hashCode()
        result = 31 * result + useOptionalForGetters.hashCode()
        result = 31 * result + usePrimitives.hashCode()
        result = 31 * result + useTitleAsClassname.hashCode()
        result = 31 * result + useJakartaValidation.hashCode()
        return result
    }
}
