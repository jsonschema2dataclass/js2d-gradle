package org.jsonschema2dataclass.internal.js2p

import org.gradle.api.GradleScriptException
import org.jsonschema2dataclass.internal.*
import org.jsonschema2dataclass.internal.compat.kotlin.TestsNeeded
import org.jsonschema2dataclass.internal.compat.kotlin.asUppercase
import org.jsonschema2pojo.*
import org.jsonschema2pojo.rules.RuleFactory
import java.io.File
import java.io.FileFilter
import java.net.URL
import java.util.*
import java.util.function.Function

internal data class Js2pConfig(
    private val targetDirectory: File,
    private val io: Js2pWorkerConfigIO,
    private val klass: Js2pWorkerConfigClass,
    private val constructors: Js2pWorkerConfigConstructor,
    private val methods: Js2pWorkerConfigMethod,
    private val fields: Js2pWorkerConfigFields,
    private val dateTime: Js2pWorkerConfigDateTime,
    private val def: GenerationConfig = DefaultGenerationConfig(),
) : GenerationConfig, java.io.Serializable {
    override fun getAnnotationStyle(): AnnotationStyle = maybeDefaultEnum(klass.annotationStyle) { def.annotationStyle }
    override fun getClassNamePrefix(): String? = maybeDefault(klass.namePrefix) { def.classNamePrefix }
    override fun getClassNameSuffix(): String? = maybeDefault(klass.nameSuffix) { def.classNameSuffix }
    override fun getCustomAnnotator(): Class<out Annotator> =
        maybeDefaultClass(klass.customAnnotatorClass) { def.customAnnotator }

    override fun getCustomDatePattern(): String? = maybeDefault(dateTime.datePattern) { def.customDatePattern }
    override fun getCustomDateTimePattern(): String? =
        maybeDefault(dateTime.dateTimePattern) { def.customDateTimePattern }

    override fun getCustomRuleFactory(): Class<out RuleFactory> =
        maybeDefaultClass(klass.customRuleFactoryClass) { def.customRuleFactory }

    override fun getCustomTimePattern(): String? = maybeDefault(dateTime.timePattern) { def.customTimePattern }
    override fun getDateTimeType(): String? = maybeDefault(dateTime.dateTimeType) { def.dateTimeType }
    override fun getDateType(): String? = maybeDefault(dateTime.dateType) { def.dateType }
    override fun getFileExtensions(): Array<String> =
        maybeDefaultSet(io.fileExtensions) { def.fileExtensions }.toTypedArray()

    override fun getFileFilter(): FileFilter = maybeDefault(io.fileFilter) { def.fileFilter }
    override fun getFormatTypeMapping(): Map<String, String> =
        maybeDefault(fields.formatToTypeMapping) { def.formatTypeMapping }

    override fun getInclusionLevel(): InclusionLevel =
        maybeDefaultEnum(klass.jackson2InclusionLevel) { def.inclusionLevel }

    override fun getOutputEncoding(): String? = maybeDefault(io.outputEncoding) { def.outputEncoding }
    override fun getPropertyWordDelimiters(): CharArray =
        maybeDefaultChar(io.delimitersPropertyWord) { def.propertyWordDelimiters }.toCharArray()

    override fun getRefFragmentPathDelimiters(): String? =
        maybeDefault(io.delimitersRefFragmentPath) { def.refFragmentPathDelimiters }

    override fun getSource(): Iterator<URL> = io.sourceFiles.iterator()
    override fun getSourceSortOrder(): SourceSortOrder = maybeDefaultEnum(io.sourceSortOrder) { def.sourceSortOrder }
    override fun getSourceType(): SourceType = maybeDefaultEnum(io.sourceType) { def.sourceType }
    override fun getTargetDirectory(): File = targetDirectory
    override fun getTargetPackage(): String = maybeDefault(klass.targetPackage) { def.targetPackage }
    override fun getTargetVersion(): String? = maybeDefault(io.targetJavaVersion) { def.targetVersion }
    override fun getTimeType(): String? = maybeDefault(dateTime.timeType) { def.timeType }
    override fun getToStringExcludes(): Array<String> =
        maybeDefaultSet(methods.toStringExcludes) { def.toStringExcludes }.toTypedArray()

    override fun isConstructorsRequiredPropertiesOnly(): Boolean = false
    override fun isFormatDateTimes(): Boolean = maybeDefault(dateTime.dateTimeFormat) { def.isFormatDateTimes }
    override fun isFormatDates(): Boolean = maybeDefault(dateTime.dateFormat) { def.isFormatDates }
    override fun isFormatTimes(): Boolean = maybeDefault(dateTime.timeFormat) { def.isFormatTimes }
    internal fun isGenerateBuildersRaw(): Boolean = maybeDefault(methods.builders) { def.isGenerateBuilders }

    @TestsNeeded
    override fun isGenerateBuilders(): Boolean = isGenerateBuildersRaw() || isUseInnerClassBuilders
    override fun isIncludeAdditionalProperties(): Boolean =
        maybeDefault(methods.additionalProperties) { def.isIncludeAdditionalProperties }

    override fun isIncludeAllPropertiesConstructor(): Boolean =
        maybeDefault(constructors.allProperties) { def.isIncludeAllPropertiesConstructor }

    override fun isIncludeConstructorPropertiesAnnotation(): Boolean =
        maybeDefault(constructors.annotateConstructorProperties) { def.isIncludeConstructorPropertiesAnnotation }

    @TestsNeeded
    override fun isIncludeConstructors(): Boolean =
        this.isIncludeAllPropertiesConstructor || isIncludeCopyConstructor || isIncludeRequiredPropertiesConstructor

    override fun isIncludeCopyConstructor(): Boolean =
        maybeDefault(constructors.copyConstructor) { def.isIncludeCopyConstructor }

    @TestsNeeded
    override fun isIncludeDynamicAccessors(): Boolean =
        isIncludeDynamicBuilders || isIncludeDynamicGetters || isIncludeDynamicSetters

    override fun isIncludeDynamicBuilders(): Boolean =
        maybeDefault(methods.buildersDynamic) { def.isIncludeDynamicBuilders }

    override fun isIncludeDynamicGetters(): Boolean =
        maybeDefault(methods.gettersDynamic) { def.isIncludeDynamicGetters }

    override fun isIncludeDynamicSetters(): Boolean =
        maybeDefault(methods.settersDynamic) { def.isIncludeDynamicSetters }

    override fun isIncludeGeneratedAnnotation(): Boolean =
        maybeDefault(klass.annotateGenerated) { def.isIncludeGeneratedAnnotation }

    override fun isIncludeGetters(): Boolean = maybeDefault(methods.getters) { def.isIncludeGetters }
    override fun isIncludeHashcodeAndEquals(): Boolean =
        maybeDefault(methods.hashcodeAndEquals) { def.isIncludeHashcodeAndEquals }

    internal fun isIncludeJsr303AnnotationRaw(): Boolean =
        maybeDefault(methods.annotateJsr303) { def.isIncludeJsr303Annotations }

    @TestsNeeded
    override fun isIncludeJsr303Annotations(): Boolean = isIncludeJsr303AnnotationRaw() || isUseJakartaValidation
    override fun isIncludeJsr305Annotations(): Boolean =
        maybeDefault(methods.annotateJsr305) { def.isIncludeJsr305Annotations }

    override fun isIncludeRequiredPropertiesConstructor(): Boolean =
        maybeDefault(constructors.requiredProperties) { def.isIncludeRequiredPropertiesConstructor }

    override fun isIncludeSetters(): Boolean = maybeDefault(methods.setters) { def.isIncludeSetters }
    override fun isIncludeToString(): Boolean = maybeDefault(methods.toStringMethod) { def.isIncludeToString }
    override fun isIncludeTypeInfo(): Boolean = maybeDefault(klass.jackson2IncludeTypeInfo) { def.isIncludeTypeInfo }
    override fun isInitializeCollections(): Boolean =
        maybeDefault(fields.initializeCollections) { def.isInitializeCollections }

    override fun isParcelable(): Boolean = maybeDefault(klass.androidParcelable) { def.isParcelable }

    @TestsNeeded
    override fun isRemoveOldOutput(): Boolean = true
    override fun isSerializable(): Boolean = maybeDefault(klass.annotateSerializable) { def.isSerializable }
    override fun isUseBigDecimals(): Boolean = maybeDefault(fields.floatUseBigDecimal) { def.isUseBigDecimals }
    override fun isUseBigIntegers(): Boolean = maybeDefault(fields.integerUseBigInteger) { def.isUseBigIntegers }
    override fun isUseDoubleNumbers(): Boolean = maybeDefault(fields.floatUseDouble) { def.isUseDoubleNumbers }
    override fun isUseInnerClassBuilders(): Boolean =
        maybeDefault(methods.buildersInnerClass) { def.isUseInnerClassBuilders }

    override fun isUseJakartaValidation(): Boolean =
        maybeDefault(methods.annotateJsr303Jakarta) { def.isUseJakartaValidation }

    override fun isUseJodaDates(): Boolean = maybeDefault(dateTime.jodaDate) { def.isUseJodaDates }
    override fun isUseJodaLocalDates(): Boolean = maybeDefault(dateTime.jodaLocalDate) { def.isUseJodaLocalDates }
    override fun isUseJodaLocalTimes(): Boolean = maybeDefault(dateTime.jodaLocalTime) { def.isUseJodaLocalTimes }
    override fun isUseLongIntegers(): Boolean = maybeDefault(fields.integerUseLong) { def.isUseLongIntegers }
    override fun isUseOptionalForGetters(): Boolean =
        maybeDefault(methods.gettersUseOptional) { def.isUseOptionalForGetters }

    override fun isUsePrimitives(): Boolean = maybeDefault(fields.usePrimitives) { def.isUsePrimitives }
    override fun isUseTitleAsClassname(): Boolean = maybeDefault(klass.nameUseTitle) { def.isUseTitleAsClassname }
}

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
