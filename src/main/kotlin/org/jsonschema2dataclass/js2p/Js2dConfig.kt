package org.jsonschema2dataclass.js2p

import org.gradle.api.GradleScriptException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Provider
import org.jsonschema2pojo.*
import org.jsonschema2pojo.rules.RuleFactory
import java.io.File
import java.io.FileFilter
import java.net.URL
import java.util.function.Function

internal data class Js2dConfig(
    private val targetDirectory: File,
    private val io: Js2pConfigIO,
    private val klass: Js2pConfigClass,
    private val constructor: Js2pConfigConstructor,
    private val methods: Js2pConfigMethod,
    private val fields: Js2pConfigFields,
    private val dateTime: Js2pConfigDateTime,
) : GenerationConfig, java.io.Serializable {
    companion object {
        fun fromConfig(
            targetDirectory: DirectoryProperty,
            config: Js2pConfiguration,
        ): Js2dConfig =
            Js2dConfig(
                targetDirectory.asFile.get(),
                convert(config.io),
                convert(config.klass),
                convert(config.constructor),
                convert(config.methods),
                convert(config.fields),
                convert(config.dateTime),
            )
    }

    override fun isGenerateBuilders(): Boolean = methods.generateBuilders
    override fun isIncludeTypeInfo(): Boolean = klass.includeTypeInfo
    override fun isIncludeConstructorPropertiesAnnotation(): Boolean =
        constructor.includeConstructorPropertiesAnnotation
    override fun isUsePrimitives(): Boolean = fields.usePrimitives
    override fun getSource(): Iterator<URL> = io.sourceFiles.iterator()
    override fun getTargetDirectory(): File = targetDirectory
    override fun getTargetPackage(): String? = klass.targetPackage
    override fun getPropertyWordDelimiters(): CharArray = klass.propertyWordDelimiters.toCharArray()
    override fun isUseLongIntegers(): Boolean = fields.useLongIntegers
    override fun isUseBigIntegers(): Boolean = fields.useBigIntegers
    override fun isUseDoubleNumbers(): Boolean = fields.useDoubleNumbers
    override fun isUseBigDecimals(): Boolean = fields.useBigDecimals
    override fun isIncludeHashcodeAndEquals(): Boolean = methods.includeHashcodeAndEquals
    override fun isIncludeToString(): Boolean = methods.includeToString
    override fun getToStringExcludes(): Array<String> = methods.toStringExcludes.toTypedArray()
    override fun getAnnotationStyle(): AnnotationStyle = klass.annotationStyle
    override fun isUseTitleAsClassname(): Boolean = klass.useTitleAsClassname
    override fun getInclusionLevel(): InclusionLevel = klass.inclusionLevel
    override fun getCustomAnnotator(): Class<out Annotator> = klass.customAnnotator
    override fun getCustomRuleFactory(): Class<out RuleFactory> = klass.customRuleFactory
    override fun isIncludeJsr303Annotations(): Boolean = methods.includeJsr303Annotations
    override fun isIncludeJsr305Annotations(): Boolean = methods.includeJsr305Annotations
    override fun isUseOptionalForGetters(): Boolean = methods.useOptionalForGetters
    override fun getSourceType(): SourceType = io.sourceType
    override fun isRemoveOldOutput(): Boolean = io.removeOldOutput
    override fun getOutputEncoding(): String? = io.outputEncoding
    override fun isUseJodaDates(): Boolean = dateTime.useJodaDates
    override fun isUseJodaLocalDates(): Boolean = dateTime.useJodaLocalDates
    override fun isUseJodaLocalTimes(): Boolean = dateTime.useJodaLocalTimes
    override fun isParcelable(): Boolean = constructor.parcelable
    override fun isSerializable(): Boolean = klass.serializable
    override fun getFileFilter(): FileFilter = io.fileFilter
    override fun isInitializeCollections(): Boolean = fields.initializeCollections
    override fun getClassNamePrefix(): String? = klass.classNamePrefix
    override fun getClassNameSuffix(): String? = klass.classNameSuffix
    override fun getFileExtensions(): Array<String> = io.fileExtensions.toTypedArray()
    override fun isIncludeConstructors(): Boolean = constructor.includeConstructors
    override fun isConstructorsRequiredPropertiesOnly(): Boolean = constructor.constructorsRequiredPropertiesOnly
    override fun isIncludeRequiredPropertiesConstructor(): Boolean = constructor.includeRequiredPropertiesConstructor
    override fun isIncludeAllPropertiesConstructor(): Boolean = constructor.includeAllPropertiesConstructor
    override fun isIncludeCopyConstructor(): Boolean = constructor.includeCopyConstructor
    override fun isIncludeAdditionalProperties(): Boolean = methods.includeAdditionalProperties
    override fun isIncludeGetters(): Boolean = methods.includeGetters
    override fun isIncludeSetters(): Boolean = methods.includeSetters
    override fun getTargetVersion(): String? = io.targetJavaVersion
    override fun isIncludeDynamicAccessors(): Boolean = methods.includeDynamicAccessors
    override fun isIncludeDynamicGetters(): Boolean = methods.includeDynamicGetters
    override fun isIncludeDynamicSetters(): Boolean = methods.includeDynamicSetters
    override fun isIncludeDynamicBuilders(): Boolean = methods.includeDynamicBuilders
    override fun getDateTimeType(): String? = dateTime.dateTimeType
    override fun getDateType(): String? = dateTime.dateType
    override fun getTimeType(): String? = dateTime.timeType
    override fun isFormatDates(): Boolean = dateTime.formatDates
    override fun isFormatTimes(): Boolean = dateTime.formatTimes
    override fun isFormatDateTimes(): Boolean = dateTime.formatDateTimes
    override fun getCustomDatePattern(): String? = dateTime.customDatePattern
    override fun getCustomTimePattern(): String? = dateTime.customTimePattern
    override fun getCustomDateTimePattern(): String? = dateTime.customDateTimePattern
    override fun getRefFragmentPathDelimiters(): String? = io.refFragmentPathDelimiters
    override fun getSourceSortOrder(): SourceSortOrder = io.sourceSortOrder
    override fun getFormatTypeMapping(): Map<String, String> = fields.formatTypeMapping
    override fun isIncludeGeneratedAnnotation(): Boolean = klass.includeGeneratedAnnotation
    override fun isUseJakartaValidation(): Boolean = methods.useJakartaValidation
}

internal data class Js2pConfigIO(
    val sourceFiles: List<URL>,
    val fileExtensions: Set<String>,
    val fileFilter: FileFilter,
    val outputEncoding: String?,
    val refFragmentPathDelimiters: String?,
    val removeOldOutput: Boolean,
    val sourceSortOrder: SourceSortOrder,
    val sourceType: SourceType,
    val targetJavaVersion: String?,
)

internal data class Js2pConfigClass(
    val annotationStyle: AnnotationStyle,
    val classNamePrefix: String?,
    val classNameSuffix: String?,
    val customAnnotator: Class<out Annotator>,
    val customRuleFactory: Class<out RuleFactory>,
    val includeGeneratedAnnotation: Boolean,
    val includeTypeInfo: Boolean,
    val inclusionLevel: InclusionLevel,
    val propertyWordDelimiters: String,
    val serializable: Boolean,
    val targetPackage: String?,
    val useTitleAsClassname: Boolean
)

internal data class Js2pConfigConstructor(
    val constructorsRequiredPropertiesOnly: Boolean,
    val includeAllPropertiesConstructor: Boolean,
    val includeConstructorPropertiesAnnotation: Boolean,
    val includeConstructors: Boolean,
    val includeCopyConstructor: Boolean,
    val includeRequiredPropertiesConstructor: Boolean,
    val parcelable: Boolean,
    val useInnerClassBuilders: Boolean,
)

internal data class Js2pConfigMethod(
    val generateBuilders: Boolean,
    val includeAdditionalProperties: Boolean,
    val includeDynamicAccessors: Boolean,
    val includeDynamicBuilders: Boolean,
    val includeDynamicGetters: Boolean,
    val includeDynamicSetters: Boolean,
    val includeGetters: Boolean,
    val includeHashcodeAndEquals: Boolean,
    val includeJsr303Annotations: Boolean,
    val includeJsr305Annotations: Boolean,
    val includeSetters: Boolean,
    val includeToString: Boolean,
    val toStringExcludes: Set<String>,
    val useJakartaValidation: Boolean,
    val useOptionalForGetters: Boolean,
)

internal data class Js2pConfigFields(
    val formatTypeMapping: Map<String, String>,
    val initializeCollections: Boolean,
    val useBigDecimals: Boolean,
    val useBigIntegers: Boolean,
    val useDoubleNumbers: Boolean,
    val useLongIntegers: Boolean,
    val usePrimitives: Boolean,
)

internal data class Js2pConfigDateTime(
    val customDatePattern: String?,
    val customDateTimePattern: String?,
    val customTimePattern: String?,
    val dateTimeType: String?,
    val dateType: String?,
    val formatDateTimes: Boolean,
    val formatDates: Boolean,
    val formatTimes: Boolean,
    val timeType: String?,
    val useJodaDates: Boolean,
    val useJodaLocalDates: Boolean,
    val useJodaLocalTimes: Boolean,
)

private val defaultConfiguration = DefaultGenerationConfig()

private fun <E : Enum<E>?> fromEnum(provider: Provider<String>, enumClass: Class<E>): E? {
    val value = provider.orNull
    return if (value.isNullOrEmpty()) {
        null
    } else {
        java.lang.Enum.valueOf(enumClass, value.toUpperCase())
    }
}

private fun <C> findClass(className: String?): Class<out C>? {
    if (className.isNullOrEmpty()) {
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
): String {
    val value = provider.orNull
    return if (value.isNullOrEmpty()) {
        vFunction.apply(defaultConfiguration).joinToString(separator = "")
    } else {
        value
    }
}

private fun <V> maybeDefault(value: Provider<V>, vFunction: Function<DefaultGenerationConfig, V>): V {
    return maybeDefaultRaw(value.orNull, vFunction)
}

private inline fun <reified K> maybeDefaultClass(
    value: Provider<String>,
    vFunction: Function<DefaultGenerationConfig, Class<out K>>
): Class<out K> {
    return maybeDefaultRaw(findClass(value.orNull), vFunction)
}

private inline fun <reified V> maybeDefaultSet(
    provider: Provider<Set<V>>,
    vFunction: Function<DefaultGenerationConfig, Array<V>>
): Set<V> {
    val value = provider.orNull
    return if (value.isNullOrEmpty()) {
        vFunction.apply(defaultConfiguration).toSet()
    } else {
        value
    }
}

private inline fun <reified V : Enum<V>?> maybeDefaultEnum(
    value: Provider<String>,
    vFunction: Function<DefaultGenerationConfig, V>
): V {
    return fromEnum(value, V::class.java) ?: vFunction.apply(defaultConfiguration)
}

private fun convert(io: PluginConfigJs2pIO): Js2pConfigIO =
    Js2pConfigIO(
        io.source.map { it.toURI().toURL() },
        maybeDefaultSet(io.fileExtensions) { it.fileExtensions },
        maybeDefault(io.fileFilter) { it.fileFilter },
        maybeDefault(io.outputEncoding) { it.outputEncoding },
        maybeDefault(io.refFragmentPathDelimiters) { it.refFragmentPathDelimiters },
        maybeDefault(io.removeOldOutput) { it.isRemoveOldOutput },
        maybeDefaultEnum(io.sourceSortOrder) { it.sourceSortOrder },
        maybeDefaultEnum(io.sourceType) { it.sourceType },
        maybeDefault(io.targetJavaVersion) { it.targetVersion },
    )

private fun convert(klass: PluginConfigJs2pClass): Js2pConfigClass =
    Js2pConfigClass(
        maybeDefaultEnum(klass.annotationStyle) { it.annotationStyle },
        maybeDefault(klass.classNamePrefix) { it.classNamePrefix },
        maybeDefault(klass.classNameSuffix) { it.classNameSuffix },
        maybeDefaultClass(klass.customAnnotator) { it.customAnnotator },
        maybeDefaultClass(klass.customRuleFactory) { it.customRuleFactory },
        maybeDefault(klass.includeGeneratedAnnotation) { it.isIncludeGeneratedAnnotation },
        maybeDefault(klass.includeTypeInfo) { it.isIncludeTypeInfo },
        maybeDefaultEnum(klass.inclusionLevel) { it.inclusionLevel },
        maybeDefaultChar(klass.propertyWordDelimiters) { it.propertyWordDelimiters },
        maybeDefault(klass.serializable) { it.isSerializable },
        maybeDefault(klass.targetPackage) { it.targetPackage },
        maybeDefault(klass.useTitleAsClassname) { it.isUseTitleAsClassname },
    )

private fun convert(constructor: PluginConfigJs2pConstructor): Js2pConfigConstructor =
    Js2pConfigConstructor(
        maybeDefault(constructor.constructorsRequiredPropertiesOnly) { it.isConstructorsRequiredPropertiesOnly },
        maybeDefault(constructor.includeAllPropertiesConstructor) { it.isIncludeAllPropertiesConstructor },
        maybeDefault(constructor.includeConstructorPropertiesAnnotation) { it.isIncludeConstructorPropertiesAnnotation },
        maybeDefault(constructor.includeConstructors) { it.isIncludeConstructors },
        maybeDefault(constructor.includeCopyConstructor) { it.isIncludeCopyConstructor },
        maybeDefault(constructor.includeRequiredPropertiesConstructor) { it.isIncludeRequiredPropertiesConstructor },
        maybeDefault(constructor.parcelable) { it.isParcelable },
        maybeDefault(constructor.useInnerClassBuilders) { it.isUseInnerClassBuilders },
    )

private fun convert(methods: PluginConfigJs2pMethod): Js2pConfigMethod =
    Js2pConfigMethod(
        maybeDefault(methods.generateBuilders) { it.isGenerateBuilders },
        maybeDefault(methods.includeAdditionalProperties) { it.isIncludeAdditionalProperties },
        maybeDefault(methods.includeDynamicAccessors) { it.isIncludeDynamicAccessors },
        maybeDefault(methods.includeDynamicBuilders) { it.isIncludeDynamicBuilders },
        maybeDefault(methods.includeDynamicGetters) { it.isIncludeDynamicGetters },
        maybeDefault(methods.includeDynamicSetters) { it.isIncludeDynamicSetters },
        maybeDefault(methods.includeGetters) { it.isIncludeGetters },
        maybeDefault(methods.includeHashcodeAndEquals) { it.isIncludeHashcodeAndEquals },
        maybeDefault(methods.includeJsr303Annotations) { it.isIncludeJsr303Annotations },
        maybeDefault(methods.includeJsr305Annotations) { it.isIncludeJsr305Annotations },
        maybeDefault(methods.includeSetters) { it.isIncludeSetters },
        maybeDefault(methods.includeToString) { it.isIncludeToString },
        maybeDefaultSet(methods.toStringExcludes) { it.toStringExcludes },
        maybeDefault(methods.useJakartaValidation) { it.isUseJakartaValidation },
        maybeDefault(methods.useOptionalForGetters) { it.isUseOptionalForGetters },
        )

private fun convert(fields: PluginConfigJs2pField): Js2pConfigFields =
    Js2pConfigFields(
        maybeDefault(fields.formatTypeMapping) { it.formatTypeMapping },
        maybeDefault(fields.initializeCollections) { it.isInitializeCollections },
        maybeDefault(fields.useBigDecimals) { it.isUseBigDecimals },
        maybeDefault(fields.useBigIntegers) { it.isUseBigIntegers },
        maybeDefault(fields.useDoubleNumbers) { it.isUseDoubleNumbers },
        maybeDefault(fields.useLongIntegers) { it.isUseLongIntegers },
        maybeDefault(fields.usePrimitives) { it.isUsePrimitives },
        )

private fun convert(dateTime: PluginConfigJs2pDateTime): Js2pConfigDateTime =
    Js2pConfigDateTime(
        maybeDefault(dateTime.customDatePattern) { it.customDatePattern },
        maybeDefault(dateTime.customDateTimePattern) { it.customDateTimePattern },
        maybeDefault(dateTime.customTimePattern) { it.customTimePattern },
        maybeDefault(dateTime.dateTimeType) { it.dateTimeType },
        maybeDefault(dateTime.dateType) { it.dateType },
        maybeDefault(dateTime.formatDateTimes) { it.isFormatDateTimes },
        maybeDefault(dateTime.formatDates) { it.isFormatDates },
        maybeDefault(dateTime.formatTimes) { it.isFormatTimes },
        maybeDefault(dateTime.timeType) { it.timeType },
        maybeDefault(dateTime.useJodaDates) { it.isUseJodaDates },
        maybeDefault(dateTime.useJodaLocalDates) { it.isUseJodaLocalDates },
        maybeDefault(dateTime.useJodaLocalTimes) { it.isUseJodaLocalTimes },
        )
