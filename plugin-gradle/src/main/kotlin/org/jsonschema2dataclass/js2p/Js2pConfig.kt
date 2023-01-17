package org.jsonschema2dataclass.js2p

import org.gradle.api.GradleScriptException
import org.jsonschema2dataclass.support.asUppercase
import org.jsonschema2pojo.*
import org.jsonschema2pojo.rules.RuleFactory
import java.io.File
import java.io.FileFilter
import java.net.URL
import java.util.*
import java.util.function.Function

internal data class Js2pConfig(
    private val targetDirectory: File,
    private val io: Js2pConfigIO,
    private val klass: Js2pConfigClass,
    private val constructors: Js2pConfigConstructor,
    private val methods: Js2pConfigMethod,
    private val fields: Js2pConfigFields,
    private val dateTime: Js2pConfigDateTime,
) : GenerationConfig, java.io.Serializable {
    companion object {
        fun fromConfig(
            config: Js2pWorkerConfig,
        ): Js2pConfig =
            Js2pConfig(
                config.targetDirectory,
                convert(config.io),
                convert(config.klass),
                convert(config.constructors),
                convert(config.methods),
                convert(config.fields),
                convert(config.dateTime),
            )
    }

    override fun getAnnotationStyle(): AnnotationStyle = klass.annotationStyle
    override fun getClassNamePrefix(): String? = klass.namePrefix
    override fun getClassNameSuffix(): String? = klass.nameSuffix
    override fun getCustomAnnotator(): Class<out Annotator> = klass.customAnnotatorClass
    override fun getCustomDatePattern(): String? = dateTime.datePattern
    override fun getCustomDateTimePattern(): String? = dateTime.dateTimePattern
    override fun getCustomRuleFactory(): Class<out RuleFactory> = klass.customRuleFactoryClass
    override fun getCustomTimePattern(): String? = dateTime.timePattern
    override fun getDateTimeType(): String? = dateTime.dateTimeType
    override fun getDateType(): String? = dateTime.dateType
    override fun getFileExtensions(): Array<String> = io.fileExtensions.toTypedArray()
    override fun getFileFilter(): FileFilter = io.fileFilter
    override fun getFormatTypeMapping(): Map<String, String> = fields.formatToTypeMapping
    override fun getInclusionLevel(): InclusionLevel = klass.jackson2InclusionLevel
    override fun getOutputEncoding(): String? = io.outputEncoding
    override fun getPropertyWordDelimiters(): CharArray = io.delimitersPropertyWord.toCharArray()
    override fun getRefFragmentPathDelimiters(): String? = io.delimitersRefFragmentPath
    override fun getSource(): Iterator<URL> = io.sourceFiles.iterator()
    override fun getSourceSortOrder(): SourceSortOrder = io.sourceSortOrder
    override fun getSourceType(): SourceType = io.sourceType
    override fun getTargetDirectory(): File = targetDirectory
    override fun getTargetPackage(): String? = klass.targetPackage
    override fun getTargetVersion(): String? = io.targetJavaVersion
    override fun getTimeType(): String? = dateTime.timeType
    override fun getToStringExcludes(): Array<String> = methods.toStringExcludes.toTypedArray()
    override fun isConstructorsRequiredPropertiesOnly(): Boolean = false
    override fun isFormatDateTimes(): Boolean = dateTime.dateTimeFormat
    override fun isFormatDates(): Boolean = dateTime.dateFormat
    override fun isFormatTimes(): Boolean = dateTime.timeFormat
    override fun isGenerateBuilders(): Boolean = methods.builders || methods.buildersInnerClass
    override fun isIncludeAdditionalProperties(): Boolean = methods.additionalProperties
    override fun isIncludeAllPropertiesConstructor(): Boolean = constructors.allProperties
    override fun isIncludeConstructorPropertiesAnnotation(): Boolean = constructors.annotateConstructorProperties
    override fun isIncludeConstructors(): Boolean = constructors.generate
    override fun isIncludeCopyConstructor(): Boolean = constructors.copyConstructor
    override fun isIncludeDynamicAccessors(): Boolean = methods.accessorsDynamic
    override fun isIncludeDynamicBuilders(): Boolean = methods.buildersDynamic
    override fun isIncludeDynamicGetters(): Boolean = methods.gettersDynamic
    override fun isIncludeDynamicSetters(): Boolean = methods.settersDynamic
    override fun isIncludeGeneratedAnnotation(): Boolean = klass.annotateGenerated
    override fun isIncludeGetters(): Boolean = methods.getters
    override fun isIncludeHashcodeAndEquals(): Boolean = methods.hashcodeAndEquals
    override fun isIncludeJsr303Annotations(): Boolean = methods.annotateJsr303 || methods.annotateJsr303Jakarta
    override fun isIncludeJsr305Annotations(): Boolean = methods.annotateJsr305
    override fun isIncludeRequiredPropertiesConstructor(): Boolean = constructors.requiredProperties
    override fun isIncludeSetters(): Boolean = methods.setters
    override fun isIncludeToString(): Boolean = methods.toStringMethod
    override fun isIncludeTypeInfo(): Boolean = klass.jackson2IncludeTypeInfo
    override fun isInitializeCollections(): Boolean = fields.initializeCollections
    override fun isParcelable(): Boolean = klass.androidParcelable
    override fun isRemoveOldOutput(): Boolean = true
    override fun isSerializable(): Boolean = klass.annotateSerializable
    override fun isUseBigDecimals(): Boolean = fields.floatUseBigDecimal
    override fun isUseBigIntegers(): Boolean = fields.integerUseBigInteger
    override fun isUseDoubleNumbers(): Boolean = fields.floatUseDouble
    override fun isUseInnerClassBuilders(): Boolean = methods.buildersInnerClass
    override fun isUseJakartaValidation(): Boolean = methods.annotateJsr303Jakarta
    override fun isUseJodaDates(): Boolean = dateTime.jodaDate
    override fun isUseJodaLocalDates(): Boolean = dateTime.jodaLocalDate
    override fun isUseJodaLocalTimes(): Boolean = dateTime.jodaLocalTime
    override fun isUseLongIntegers(): Boolean = fields.integerUseLong
    override fun isUseOptionalForGetters(): Boolean = methods.gettersUseOptional
    override fun isUsePrimitives(): Boolean = fields.usePrimitives
    override fun isUseTitleAsClassname(): Boolean = klass.nameUseTitle
}

internal data class Js2pConfigIO(
    val sourceFiles: List<URL>,
    val delimitersPropertyWord: String,
    val delimitersRefFragmentPath: String?,
    val fileExtensions: Set<String>,
    val fileFilter: FileFilter,
    val outputEncoding: String?,
    val sourceSortOrder: SourceSortOrder,
    val sourceType: SourceType,
    val targetJavaVersion: String?,
)

internal data class Js2pConfigClass(
    val androidParcelable: Boolean,
    val annotateGenerated: Boolean,
    val annotateSerializable: Boolean,
    val annotationStyle: AnnotationStyle,
    val customAnnotatorClass: Class<out Annotator>,
    val customRuleFactoryClass: Class<out RuleFactory>,
    val jackson2IncludeTypeInfo: Boolean,
    val jackson2InclusionLevel: InclusionLevel,
    val namePrefix: String?,
    val nameSuffix: String?,
    val nameUseTitle: Boolean,
    val targetPackage: String?,
)

internal data class Js2pConfigConstructor(
    val allProperties: Boolean,
    val annotateConstructorProperties: Boolean,
    val copyConstructor: Boolean,
    val requiredProperties: Boolean,
    val generate: Boolean = allProperties || annotateConstructorProperties || copyConstructor || requiredProperties,
)

internal data class Js2pConfigMethod(
    val additionalProperties: Boolean,
    val annotateJsr303Jakarta: Boolean,
    val annotateJsr303: Boolean,
    val annotateJsr305: Boolean,
    val builders: Boolean,
    val buildersDynamic: Boolean,
    val buildersInnerClass: Boolean,
    val getters: Boolean,
    val gettersDynamic: Boolean,
    val gettersUseOptional: Boolean,
    val hashcodeAndEquals: Boolean,
    val setters: Boolean,
    val settersDynamic: Boolean,
    val toStringExcludes: Set<String>,
    val toStringMethod: Boolean,
    val accessorsDynamic: Boolean = gettersDynamic || buildersDynamic || settersDynamic,
)

internal data class Js2pConfigFields(
    val floatUseBigDecimal: Boolean,
    val floatUseDouble: Boolean,
    val formatToTypeMapping: Map<String, String>,
    val initializeCollections: Boolean,
    val integerUseBigInteger: Boolean,
    val integerUseLong: Boolean,
    val usePrimitives: Boolean,
)

internal data class Js2pConfigDateTime(
    val dateFormat: Boolean,
    val datePattern: String?,
    val dateTimeFormat: Boolean,
    val dateTimePattern: String?,
    val dateTimeType: String?,
    val dateType: String?,
    val jodaDate: Boolean,
    val jodaLocalDate: Boolean,
    val jodaLocalTime: Boolean,
    val timeFormat: Boolean,
    val timePattern: String?,
    val timeType: String?,
)

private fun convert(io: Js2pWorkerConfigIO): Js2pConfigIO =
    Js2pConfigIO(
        io.sourceFiles,
        maybeDefaultChar(io.delimitersPropertyWord) { it.propertyWordDelimiters },
        maybeDefault(io.delimitersRefFragmentPath) { it.refFragmentPathDelimiters },
        maybeDefaultSet(io.fileExtensions) { it.fileExtensions },
        maybeDefault(io.fileFilter) { it.fileFilter },
        maybeDefault(io.outputEncoding) { it.outputEncoding },
        maybeDefaultEnum(io.sourceSortOrder) { it.sourceSortOrder },
        maybeDefaultEnum(io.sourceType) { it.sourceType },
        maybeDefault(io.targetJavaVersion) { it.targetVersion },
    )

private fun convert(klass: Js2pWorkerConfigClass): Js2pConfigClass =
    Js2pConfigClass(
        maybeDefault(klass.androidParcelable) { it.isParcelable },
        maybeDefault(klass.annotateGenerated) { it.isIncludeGeneratedAnnotation },
        maybeDefault(klass.annotateSerializable) { it.isSerializable },
        maybeDefaultEnum(klass.annotationStyle) { it.annotationStyle },
        maybeDefaultClass(klass.customAnnotatorClass) { it.customAnnotator },
        maybeDefaultClass(klass.customRuleFactoryClass) { it.customRuleFactory },
        maybeDefault(klass.jackson2IncludeTypeInfo) { it.isIncludeTypeInfo },
        maybeDefaultEnum(klass.jackson2InclusionLevel) { it.inclusionLevel },
        maybeDefault(klass.namePrefix) { it.classNamePrefix },
        maybeDefault(klass.nameSuffix) { it.classNameSuffix },
        maybeDefault(klass.nameUseTitle) { it.isUseTitleAsClassname },
        maybeDefault(klass.targetPackage) { it.targetPackage },
    )

private fun convert(constructor: Js2pWorkerConfigConstructor): Js2pConfigConstructor =
    Js2pConfigConstructor(
        maybeDefault(constructor.allProperties) { it.isIncludeAllPropertiesConstructor },
        maybeDefault(constructor.annotateConstructorProperties) { it.isIncludeConstructorPropertiesAnnotation },
        maybeDefault(constructor.copyConstructor) { it.isIncludeCopyConstructor },
        maybeDefault(constructor.requiredProperties) { it.isIncludeRequiredPropertiesConstructor },
    )

private fun convert(methods: Js2pWorkerConfigMethod): Js2pConfigMethod =
    Js2pConfigMethod(
        maybeDefault(methods.additionalProperties) { it.isIncludeAdditionalProperties },
        maybeDefault(methods.annotateJsr303Jakarta) { it.isUseJakartaValidation },
        maybeDefault(methods.annotateJsr303) { it.isIncludeJsr303Annotations },
        maybeDefault(methods.annotateJsr305) { it.isIncludeJsr305Annotations },
        maybeDefault(methods.builders) { it.isGenerateBuilders },
        maybeDefault(methods.buildersDynamic) { it.isIncludeDynamicBuilders },
        maybeDefault(methods.buildersInnerClass) { it.isUseInnerClassBuilders },
        maybeDefault(methods.getters) { it.isIncludeGetters },
        maybeDefault(methods.gettersDynamic) { it.isIncludeDynamicGetters },
        maybeDefault(methods.gettersUseOptional) { it.isUseOptionalForGetters },
        maybeDefault(methods.hashcodeAndEquals) { it.isIncludeHashcodeAndEquals },
        maybeDefault(methods.setters) { it.isIncludeSetters },
        maybeDefault(methods.settersDynamic) { it.isIncludeDynamicSetters },
        maybeDefaultSet(methods.toStringExcludes) { it.toStringExcludes },
        maybeDefault(methods.toStringMethod) { it.isIncludeToString },
    )

private fun convert(fields: Js2pWorkerConfigFields): Js2pConfigFields =
    Js2pConfigFields(
        maybeDefault(fields.floatUseBigDecimal) { it.isUseBigDecimals },
        maybeDefault(fields.floatUseDouble) { it.isUseDoubleNumbers },
        maybeDefault(fields.formatToTypeMapping) { it.formatTypeMapping },
        maybeDefault(fields.initializeCollections) { it.isInitializeCollections },
        maybeDefault(fields.integerUseBigInteger) { it.isUseBigIntegers },
        maybeDefault(fields.integerUseLong) { it.isUseLongIntegers },
        maybeDefault(fields.usePrimitives) { it.isUsePrimitives },
    )

private fun convert(dateTime: Js2pWorkerConfigDateTime): Js2pConfigDateTime =
    Js2pConfigDateTime(
        maybeDefault(dateTime.dateFormat) { it.isFormatDates },
        maybeDefault(dateTime.datePattern) { it.customDatePattern },
        maybeDefault(dateTime.dateTimeFormat) { it.isFormatDateTimes },
        maybeDefault(dateTime.dateTimePattern) { it.customDateTimePattern },
        maybeDefault(dateTime.dateTimeType) { it.dateTimeType },
        maybeDefault(dateTime.dateType) { it.dateType },
        maybeDefault(dateTime.jodaDate) { it.isUseJodaDates },
        maybeDefault(dateTime.jodaLocalDate) { it.isUseJodaLocalDates },
        maybeDefault(dateTime.jodaLocalTime) { it.isUseJodaLocalTimes },
        maybeDefault(dateTime.timeFormat) { it.isFormatTimes },
        maybeDefault(dateTime.timePattern) { it.customTimePattern },
        maybeDefault(dateTime.timeType) { it.timeType },
    )

private val defaultConfiguration = DefaultGenerationConfig()

private fun <C> findClass(className: String?): Class<out C>? {
    if (className.isNullOrEmpty()) {
        return null
    }
    return try {
        @Suppress("UNCHECKED_CAST")
        Class.forName(className, true, Js2pConfig::class.java.classLoader) as Class<C>
    } catch (e: ClassNotFoundException) {
        throw GradleScriptException("Unable to find class $className", e)
    }
}

private fun <V> maybeDefault(value: V?, vFunction: Function<DefaultGenerationConfig, V>): V {
    return value ?: vFunction.apply(defaultConfiguration)
}

private fun maybeDefaultChar(
    value: String?,
    vFunction: Function<DefaultGenerationConfig, CharArray>,
): String {
    return if (value.isNullOrEmpty()) {
        vFunction.apply(defaultConfiguration).joinToString(separator = "")
    } else {
        value
    }
}

private inline fun <reified K> maybeDefaultClass(
    value: String?,
    vFunction: Function<DefaultGenerationConfig, Class<out K>>,
): Class<out K> {
    return maybeDefault(findClass(value), vFunction)
}

private inline fun <reified V> maybeDefaultSet(
    value: Set<V>?,
    vFunction: Function<DefaultGenerationConfig, Array<V>>,
): Set<V> {
    return if (value.isNullOrEmpty()) {
        vFunction.apply(defaultConfiguration).toSet()
    } else {
        value
    }
}

private fun <E : Enum<E>?> fromEnum(value: String?, enumClass: Class<E>): E? {
    return if (value.isNullOrEmpty()) {
        null
    } else {
        java.lang.Enum.valueOf(enumClass, value.asUppercase())
    }
}

private inline fun <reified V : Enum<V>?> maybeDefaultEnum(
    value: String?,
    vFunction: Function<DefaultGenerationConfig, V>,
): V {
    return fromEnum(value, V::class.java) ?: vFunction.apply(defaultConfiguration)
}
