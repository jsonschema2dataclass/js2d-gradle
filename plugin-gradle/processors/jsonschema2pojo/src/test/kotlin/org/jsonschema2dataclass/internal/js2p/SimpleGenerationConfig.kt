package org.jsonschema2dataclass.internal.js2p

import org.jsonschema2pojo.*
import org.jsonschema2pojo.rules.RuleFactory
import java.io.File
import java.io.FileFilter
import java.net.URL

/**
 * Simple implementation of Json Schema 2 Pojo's Generation Config to test object conversion
 */
data class SimpleGenerationConfig(
    private val constructorsRequiredPropertiesOnly: Boolean,
    private val formatDateTimes: Boolean,
    private val formatDates: Boolean,
    private val formatTimes: Boolean,
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
    private val initializeCollections: Boolean,
    private val parcelable: Boolean,
    private val removeOldOutput: Boolean,
    private val serializable: Boolean,
    private val useBigDecimals: Boolean,
    private val useBigIntegers: Boolean,
    private val useDoubleNumbers: Boolean,
    private val useJakartaValidation: Boolean,
    private val useJodaDates: Boolean,
    private val useJodaLocalDates: Boolean,
    private val useJodaLocalTimes: Boolean,
    private val useLongIntegers: Boolean,
    private val useOptionalForGetters: Boolean,
    private val usePrimitives: Boolean,
    private val useTitleAsClassname: Boolean,
    private val annotationStyle: AnnotationStyle,
    private val classNamePrefix: String,
    private val classNameSuffix: String,
    private val customAnnotator: Class<out Annotator>,
    private val customDatePattern: String?,
    private val customDateTimePattern: String?,
    private val customRuleFactory: Class<out RuleFactory>,
    private val customTimePattern: String?,
    private val dateTimeType: String?,
    private val dateType: String?,
    private val fileExtensions: List<String>,
    private val fileFilter: FileFilter?,
    private val formatTypeMapping: Map<String, String>,
    private val inclusionLevel: InclusionLevel,
    private val outputEncoding: String,
    private val propertyWordDelimiters: String,
    private val refFragmentPathDelimiters: String,
    private val source: Iterator<URL>?,
    private val sourceSortOrder: SourceSortOrder,
    private val sourceType: SourceType,
    private val targetDirectory: File,
    private val targetPackage: String,
    private val targetVersion: String,
    private val timeType: String?,
    private val toStringExcludes: List<String>,
) : GenerationConfig {
    override fun getAnnotationStyle(): AnnotationStyle = annotationStyle
    override fun getClassNamePrefix(): String = classNamePrefix
    override fun getClassNameSuffix(): String = classNameSuffix
    override fun getCustomAnnotator(): Class<out Annotator> = customAnnotator
    override fun getCustomDatePattern(): String? = customDatePattern
    override fun getCustomDateTimePattern(): String? = customDateTimePattern
    override fun getCustomRuleFactory(): Class<out RuleFactory> = customRuleFactory
    override fun getCustomTimePattern(): String? = customTimePattern
    override fun getDateTimeType(): String? = dateTimeType
    override fun getDateType(): String? = dateType
    override fun getFileExtensions(): Array<String> = fileExtensions.toTypedArray()
    override fun getFileFilter(): FileFilter? = fileFilter
    override fun getFormatTypeMapping(): Map<String, String> = formatTypeMapping
    override fun getInclusionLevel(): InclusionLevel = inclusionLevel
    override fun getOutputEncoding(): String = outputEncoding
    override fun getPropertyWordDelimiters(): CharArray = propertyWordDelimiters.toCharArray()
    override fun getRefFragmentPathDelimiters(): String = refFragmentPathDelimiters
    override fun getSource(): Iterator<URL>? = source
    override fun getSourceSortOrder(): SourceSortOrder = sourceSortOrder
    override fun getSourceType(): SourceType = sourceType
    override fun getTargetDirectory(): File = targetDirectory
    override fun getTargetPackage(): String = targetPackage
    override fun getTargetVersion(): String = targetVersion
    override fun getTimeType(): String? = timeType
    override fun getToStringExcludes(): Array<String> = toStringExcludes.toTypedArray()
    override fun isConstructorsRequiredPropertiesOnly(): Boolean = constructorsRequiredPropertiesOnly
    override fun isFormatDateTimes(): Boolean = formatDateTimes
    override fun isFormatDates(): Boolean = formatDates
    override fun isFormatTimes(): Boolean = formatTimes
    override fun isGenerateBuilders(): Boolean = generateBuilders
    override fun isIncludeAdditionalProperties(): Boolean = includeAdditionalProperties
    override fun isIncludeAllPropertiesConstructor(): Boolean = includeAllPropertiesConstructor
    override fun isIncludeConstructorPropertiesAnnotation(): Boolean = includeConstructorPropertiesAnnotation
    override fun isIncludeConstructors(): Boolean = includeConstructors
    override fun isIncludeCopyConstructor(): Boolean = includeCopyConstructor
    override fun isIncludeDynamicAccessors(): Boolean = includeDynamicAccessors
    override fun isIncludeDynamicBuilders(): Boolean = includeDynamicBuilders
    override fun isIncludeDynamicGetters(): Boolean = includeDynamicGetters
    override fun isIncludeDynamicSetters(): Boolean = includeDynamicSetters
    override fun isIncludeGeneratedAnnotation(): Boolean = includeGeneratedAnnotation
    override fun isIncludeGetters(): Boolean = includeGetters
    override fun isIncludeHashcodeAndEquals(): Boolean = includeHashcodeAndEquals
    override fun isIncludeJsr303Annotations(): Boolean = includeJsr303Annotations
    override fun isIncludeJsr305Annotations(): Boolean = includeJsr305Annotations
    override fun isIncludeRequiredPropertiesConstructor(): Boolean = includeRequiredPropertiesConstructor
    override fun isIncludeSetters(): Boolean = includeSetters
    override fun isIncludeToString(): Boolean = includeToString
    override fun isIncludeTypeInfo(): Boolean = includeTypeInfo
    override fun isInitializeCollections(): Boolean = initializeCollections
    override fun isParcelable(): Boolean = parcelable
    override fun isRemoveOldOutput(): Boolean = removeOldOutput
    override fun isSerializable(): Boolean = serializable
    override fun isUseBigDecimals(): Boolean = useBigDecimals
    override fun isUseBigIntegers(): Boolean = useBigIntegers
    override fun isUseDoubleNumbers(): Boolean = useDoubleNumbers
    override fun isUseJakartaValidation(): Boolean = useJakartaValidation
    override fun isUseJodaDates(): Boolean = useJodaDates
    override fun isUseJodaLocalDates(): Boolean = useJodaLocalDates
    override fun isUseJodaLocalTimes(): Boolean = useJodaLocalTimes
    override fun isUseLongIntegers(): Boolean = useLongIntegers
    override fun isUseOptionalForGetters(): Boolean = useOptionalForGetters
    override fun isUsePrimitives(): Boolean = usePrimitives
    override fun isUseTitleAsClassname(): Boolean = useTitleAsClassname
}
