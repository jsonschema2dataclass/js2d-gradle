package org.jsonschema2dataclass.internal.js2p

import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.jsonschema2dataclass.ext.*
import org.jsonschema2pojo.GenerationConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertAll

internal fun checkIfEqual(configuration: Js2pConfiguration, workerConfig: Js2pWorkerConfig) {
    assertAll(
        { checkIfEqual(configuration.io, workerConfig.io) },
        { checkIfEqual(configuration.klass, workerConfig.klass) },
        { checkIfEqual(configuration.constructors, workerConfig.constructors) },
        { checkIfEqual(configuration.methods, workerConfig.methods) },
        { checkIfEqual(configuration.fields, workerConfig.fields) },
        { checkIfEqual(configuration.dateTime, workerConfig.dateTime) },
    )
}

private fun checkIfEqual(expected: PluginConfigJs2pIO, actual: Js2pWorkerConfigIO) {
    assertAll(
        { checkIfEqual(expected.delimitersPropertyWord, actual.delimitersPropertyWord, "io.delimitersPropertyWord") },
        {
            checkIfEqual(
                expected.delimitersRefFragmentPath,
                actual.delimitersRefFragmentPath,
                "io.delimitersRefFragmentPath",
            )
        },
        { checkIfEqual(expected.fileExtensions, actual.fileExtensions, "io.fileExtensions") },
        { checkIfEqual(expected.outputEncoding, actual.outputEncoding, "io.outputEncoding") },
        { checkIfEqual(expected.sourceSortOrder, actual.sourceSortOrder, "io.sourceSortOrder") },
        { checkIfEqual(expected.sourceType, actual.sourceType, "io.sourceType") },
        { checkIfEqual(expected.targetJavaVersion, actual.targetJavaVersion, "io.targetJavaVersion") },
    )
}

private fun checkIfEqual(expected: PluginConfigJs2pClass, actual: Js2pWorkerConfigClass) {
    assertAll(
        { checkIfEqual(expected.androidParcelable, actual.androidParcelable, "klass.androidParcelable") },
        { checkIfEqual(expected.annotateGenerated, actual.annotateGenerated, "klass.annotateGenerated") },
        { checkIfEqual(expected.annotateSerializable, actual.annotateSerializable, "klass.annotateSerializable") },
        { checkIfEqual(expected.annotationStyle, actual.annotationStyle, "klass.annotationStyle") },
        { checkIfEqual(expected.customAnnotatorClass, actual.customAnnotatorClass, "klass.customAnnotatorClass") },
        {
            checkIfEqual(
                expected.customRuleFactoryClass,
                actual.customRuleFactoryClass,
                "klass.customRuleFactoryClass",
            )
        },
        {
            checkIfEqual(
                expected.jackson2IncludeTypeInfo,
                actual.jackson2IncludeTypeInfo,
                "klass.jackson2IncludeTypeInfo",
            )
        },
        {
            checkIfEqual(
                expected.jackson2InclusionLevel,
                actual.jackson2InclusionLevel,
                "klass.jackson2InclusionLevel",
            )
        },
        { checkIfEqual(expected.namePrefix, actual.namePrefix, "klass.namePrefix") },
        { checkIfEqual(expected.nameSuffix, actual.nameSuffix, "klass.nameSuffix") },
        { checkIfEqual(expected.nameUseTitle, actual.nameUseTitle, "klass.nameUseTitle") },
        { checkIfEqual(expected.targetPackage, actual.targetPackage, "klass.targetPackage") },
    )
}

private fun checkIfEqual(expected: PluginConfigJs2pConstructor, actual: Js2pWorkerConfigConstructor) {
    assertAll(
        { checkIfEqual(expected.allProperties, actual.allProperties, "constructors.allProperties") },
        {
            checkIfEqual(
                expected.annotateConstructorProperties,
                actual.annotateConstructorProperties,
                "constructors.annotateConstructorProperties",
            )
        },
        { checkIfEqual(expected.copyConstructor, actual.copyConstructor, "constructors.copyConstructor") },
        { checkIfEqual(expected.requiredProperties, actual.requiredProperties, "constructors.requiredProperties") },
    )
}

private fun checkIfEqual(expected: PluginConfigJs2pMethod, actual: Js2pWorkerConfigMethod) {
    assertAll(
        { checkIfEqual(expected.additionalProperties, actual.additionalProperties, "methods.additionalProperties") },
        { checkIfEqual(expected.annotateJsr303Jakarta, actual.annotateJsr303Jakarta, "methods.annotateJsr303Jakarta") },
        { checkIfEqual(expected.annotateJsr303, actual.annotateJsr303, "methods.annotateJsr303") },
        { checkIfEqual(expected.annotateJsr305, actual.annotateJsr305, "methods.annotateJsr305") },
        { checkIfEqual(expected.builders, actual.builders, "methods.builders") },
        { checkIfEqual(expected.buildersDynamic, actual.buildersDynamic, "methods.buildersDynamic") },
        { checkIfEqual(expected.buildersInnerClass, actual.buildersInnerClass, "methods.buildersInnerClass") },
        { checkIfEqual(expected.getters, actual.getters, "methods.getters") },
        { checkIfEqual(expected.gettersDynamic, actual.gettersDynamic, "methods.gettersDynamic") },
        { checkIfEqual(expected.gettersUseOptional, actual.gettersUseOptional, "methods.gettersUseOptional") },
        { checkIfEqual(expected.hashcodeAndEquals, actual.hashcodeAndEquals, "methods.hashcodeAndEquals") },
        { checkIfEqual(expected.setters, actual.setters, "methods.setters") },
        { checkIfEqual(expected.settersDynamic, actual.settersDynamic, "methods.settersDynamic") },
        { checkIfEqual(expected.toStringExcludes, actual.toStringExcludes, "methods.toStringExcludes") },
        { checkIfEqual(expected.toStringMethod, actual.toStringMethod, "methods.toStringMethod") },
    )
}

private fun checkIfEqual(expected: PluginConfigJs2pField, actual: Js2pWorkerConfigFields) {
    assertAll(
        { checkIfEqual(expected.floatUseBigDecimal, actual.floatUseBigDecimal, "fields.floatUseBigDecimal") },
        { checkIfEqual(expected.floatUseDouble, actual.floatUseDouble, "fields.floatUseDouble") },
        { checkIfEqual(expected.formatToTypeMapping, actual.formatToTypeMapping, "fields.formatToTypeMapping") },
        { checkIfEqual(expected.initializeCollections, actual.initializeCollections, "fields.initializeCollections") },
        { checkIfEqual(expected.integerUseBigInteger, actual.integerUseBigInteger, "fields.integerUseBigInteger") },
        { checkIfEqual(expected.integerUseLong, actual.integerUseLong, "fields.integerUseLong") },
        { checkIfEqual(expected.usePrimitives, actual.usePrimitives, "fields.usePrimitives") },
    )
}

private fun checkIfEqual(expected: PluginConfigJs2pDateTime, actual: Js2pWorkerConfigDateTime) {
    assertAll(
        { checkIfEqual(expected.dateFormat, actual.dateFormat, "dateTime.dateFormat") },
        { checkIfEqual(expected.datePattern, actual.datePattern, "dateTime.datePattern") },
        { checkIfEqual(expected.dateTimeFormat, actual.dateTimeFormat, "dateTime.dateTimeFormat") },
        { checkIfEqual(expected.dateTimePattern, actual.dateTimePattern, "dateTime.dateTimePattern") },
        { checkIfEqual(expected.dateTimeType, actual.dateTimeType, "dateTime.dateTimeType") },
        { checkIfEqual(expected.dateType, actual.dateType, "dateTime.dateType") },
        { checkIfEqual(expected.jodaDate, actual.jodaDate, "dateTime.jodaDate") },
        { checkIfEqual(expected.jodaLocalDate, actual.jodaLocalDate, "dateTime.jodaLocalDate") },
        { checkIfEqual(expected.jodaLocalTime, actual.jodaLocalTime, "dateTime.jodaLocalTime") },
        { checkIfEqual(expected.timeFormat, actual.timeFormat, "dateTime.timeFormat") },
        { checkIfEqual(expected.timePattern, actual.timePattern, "dateTime.timePattern") },
        { checkIfEqual(expected.timeType, actual.timeType, "dateTime.timeType") },
    )
}

private fun <T> checkIfEqual(expected: Property<T>, actual: T?, message: String) {
    Assertions.assertEquals(expected.orNull, actual, message)
}

private fun <T> checkIfEqual(expected: SetProperty<T>, actual: Set<T>?, message: String) {
    Assertions.assertEquals(expected.orNull, actual, message)
}

private fun <T> checkIfEqual(
    expected: MapProperty<T, T>,
    actual: Map<T, T>?,
    @Suppress("SameParameterValue") message: String,
) {
    Assertions.assertEquals(expected.orNull, actual, message)
}

internal fun checkIfEqual(configuration: Js2pConfiguration, js2pConfig: Js2pConfig, simpleConfig: GenerationConfig) {
    assertAll(
        "all",
        { checkIfEqual(configuration.io, js2pConfig, simpleConfig) },
        { checkIfEqual(configuration.klass, js2pConfig, simpleConfig) },
        { checkIfEqual(configuration.constructors, js2pConfig, simpleConfig) },
        { checkIfEqual(configuration.methods, js2pConfig, simpleConfig) },
        { checkIfEqual(configuration.fields, js2pConfig, simpleConfig) },
        { checkIfEqual(configuration.dateTime, js2pConfig, simpleConfig) },
    )
}

private fun checkIfEqual(expected: PluginConfigJs2pIO, actual: Js2pConfig, simpleConfig: GenerationConfig) {
    assertAll(
        "io",
        {
            checkIfEqual(
                expected.delimitersPropertyWord,
                actual.propertyWordDelimiters,
                simpleConfig.propertyWordDelimiters,
                "io.delimitersPropertyWord",
            )
        },
        {
            checkIfEqual(
                expected.delimitersRefFragmentPath,
                actual.refFragmentPathDelimiters,
                simpleConfig.refFragmentPathDelimiters,
                "io.delimitersRefFragmentPath",
            )
        },
        {
            checkIfEqual(
                expected.fileExtensions,
                actual.fileExtensions,
                simpleConfig.fileExtensions,
                "io.fileExtensions",
            )
        },
        {
            checkIfEqual(
                expected.outputEncoding,
                actual.outputEncoding,
                simpleConfig.outputEncoding,
                "io.outputEncoding",
            )
        },
        {
            checkIfEqual(
                expected.sourceSortOrder,
                actual.sourceSortOrder,
                simpleConfig.sourceSortOrder,
                "io.sourceSortOrder",
            )
        },
        { checkIfEqual(expected.sourceType, actual.sourceType, simpleConfig.sourceType, "io.sourceType") },
        {
            checkIfEqual(
                expected.targetJavaVersion,
                actual.targetVersion,
                simpleConfig.targetVersion,
                "io.targetJavaVersion",
            )
        },
    )
}

private fun checkIfEqual(expected: PluginConfigJs2pClass, actual: Js2pConfig, simpleConfig: GenerationConfig) {
    assertAll(
        "class",
        {
            checkIfEqual(
                expected.androidParcelable,
                actual.isParcelable,
                simpleConfig.isParcelable,
                "klass.androidParcelable",
            )
        },
        {
            checkIfEqual(
                expected.annotateGenerated,
                actual.isIncludeGeneratedAnnotation,
                simpleConfig.isIncludeGeneratedAnnotation,
                "klass.annotateGenerated",
            )
        },
        {
            checkIfEqual(
                expected.annotateSerializable,
                actual.isSerializable,
                simpleConfig.isSerializable,
                "klass.annotateSerializable",
            )
        },
        {
            checkIfEqual(
                expected.annotationStyle,
                actual.annotationStyle,
                simpleConfig.annotationStyle,
                "klass.annotationStyle",
            )
        },
        {
            checkIfEqual(
                expected.customAnnotatorClass,
                actual.customAnnotator,
                simpleConfig.customAnnotator,
                "klass.customAnnotatorClass",
            )
        },
        {
            checkIfEqual(
                expected.customRuleFactoryClass,
                actual.customRuleFactory,
                simpleConfig.customRuleFactory,
                "klass.customRuleFactoryClass",
            )
        },
        {
            checkIfEqual(
                expected.jackson2IncludeTypeInfo,
                actual.isIncludeTypeInfo,
                simpleConfig.isIncludeTypeInfo,
                "klass.jackson2IncludeTypeInfo",
            )
        },
        {
            checkIfEqual(
                expected.jackson2InclusionLevel,
                actual.inclusionLevel,
                simpleConfig.inclusionLevel,
                "klass.jackson2InclusionLevel",
            )
        },
        { checkIfEqual(expected.namePrefix, actual.classNamePrefix, simpleConfig.classNamePrefix, "klass.namePrefix") },
        { checkIfEqual(expected.nameSuffix, actual.classNameSuffix, simpleConfig.classNameSuffix, "klass.nameSuffix") },
        {
            checkIfEqual(
                expected.nameUseTitle,
                actual.isUseTitleAsClassname,
                simpleConfig.isUseTitleAsClassname,
                "klass.nameUseTitle",
            )
        },
        {
            checkIfEqual(
                expected.targetPackage,
                actual.targetPackage,
                simpleConfig.targetPackage,
                "klass.targetPackage",
            )
        },
    )
}

private fun checkIfEqual(expected: PluginConfigJs2pConstructor, actual: Js2pConfig, simpleConfig: GenerationConfig) {
    assertAll(
        "constructors",
        {
            checkIfEqual(
                expected.allProperties,
                actual.isIncludeAllPropertiesConstructor,
                simpleConfig.isIncludeAllPropertiesConstructor,
                "constructors.allProperties",
            )
        },
        {
            checkIfEqual(
                expected.annotateConstructorProperties,
                actual.isIncludeConstructorPropertiesAnnotation,
                simpleConfig.isIncludeConstructorPropertiesAnnotation,
                "constructors.annotateConstructorProperties",
            )
        },
        {
            checkIfEqual(
                expected.copyConstructor,
                actual.isIncludeCopyConstructor,
                simpleConfig.isIncludeCopyConstructor,
                "constructors.copyConstructor",
            )
        },
        {
            checkIfEqual(
                expected.requiredProperties,
                actual.isIncludeRequiredPropertiesConstructor,
                simpleConfig.isIncludeRequiredPropertiesConstructor,
                "constructors.requiredProperties",
            )
        },
    )
}

private fun checkIfEqual(expected: PluginConfigJs2pMethod, actual: Js2pConfig, simpleConfig: GenerationConfig) {
    assertAll(
        "Methods",
        {
            checkIfEqual(
                expected.additionalProperties,
                actual.isIncludeAdditionalProperties,
                simpleConfig.isIncludeAdditionalProperties,
                "methods.additionalProperties",
            )
        },
        {
            checkIfEqual(
                expected.annotateJsr303Jakarta,
                actual.isUseJakartaValidation,
                simpleConfig.isUseJakartaValidation,
                "methods.annotateJsr303Jakarta",
            )
        },
        {
            checkIfEqual(
                expected.annotateJsr303,
                actual.isIncludeJsr303AnnotationRaw(),
                simpleConfig.isIncludeJsr303Annotations,
                "methods.annotateJsr303",
            )
        },
        {
            checkIfEqual(
                expected.annotateJsr305,
                actual.isIncludeJsr305Annotations,
                simpleConfig.isIncludeJsr305Annotations,
                "methods.annotateJsr305",
            )
        },
        {
            checkIfEqual(
                expected.builders,
                actual.isGenerateBuildersRaw(),
                simpleConfig.isGenerateBuilders,
                "methods.builders",
            )
        },
        {
            checkIfEqual(
                expected.buildersDynamic,
                actual.isIncludeDynamicBuilders,
                simpleConfig.isIncludeDynamicBuilders,
                "methods.buildersDynamic",
            )
        },
        {
            checkIfEqual(
                expected.buildersInnerClass,
                actual.isUseInnerClassBuilders,
                simpleConfig.isUseInnerClassBuilders,
                "methods.buildersInnerClass",
            )
        },
        { checkIfEqual(expected.getters, actual.isIncludeGetters, simpleConfig.isIncludeGetters, "methods.getters") },
        {
            checkIfEqual(
                expected.gettersDynamic,
                actual.isIncludeDynamicGetters,
                simpleConfig.isIncludeDynamicGetters,
                "methods.gettersDynamic",
            )
        },
        {
            checkIfEqual(
                expected.gettersUseOptional,
                actual.isUseOptionalForGetters,
                simpleConfig.isUseOptionalForGetters,
                "methods.gettersUseOptional",
            )
        },
        {
            checkIfEqual(
                expected.hashcodeAndEquals,
                actual.isIncludeHashcodeAndEquals,
                simpleConfig.isIncludeHashcodeAndEquals,
                "methods.hashcodeAndEquals",
            )
        },
        { checkIfEqual(expected.setters, actual.isIncludeSetters, simpleConfig.isIncludeSetters, "methods.setters") },
        {
            checkIfEqual(
                expected.settersDynamic,
                actual.isIncludeDynamicSetters,
                simpleConfig.isIncludeDynamicSetters,
                "methods.settersDynamic",
            )
        },
        {
            checkIfEqual(
                expected.toStringExcludes,
                actual.toStringExcludes,
                simpleConfig.toStringExcludes,
                "methods.toStringExcludes",
            )
        },
        {
            checkIfEqual(
                expected.toStringMethod,
                actual.isIncludeToString,
                simpleConfig.isIncludeToString,
                "methods.toStringMethod",
            )
        },
    )
}

private fun checkIfEqual(expected: PluginConfigJs2pField, actual: Js2pConfig, simpleConfig: GenerationConfig) {
    assertAll(
        "Fields",
        {
            checkIfEqual(
                expected.floatUseBigDecimal,
                actual.isUseBigDecimals,
                simpleConfig.isUseBigDecimals,
                "fields.floatUseBigDecimal",
            )
        },
        {
            checkIfEqual(
                expected.floatUseDouble,
                actual.isUseDoubleNumbers,
                simpleConfig.isUseDoubleNumbers,
                "fields.floatUseDouble",
            )
        },
        {
            checkIfEqual(
                expected.formatToTypeMapping,
                actual.formatTypeMapping,
                simpleConfig.formatTypeMapping,
                "fields.formatToTypeMapping",
            )
        },
        {
            checkIfEqual(
                expected.initializeCollections,
                actual.isInitializeCollections,
                simpleConfig.isInitializeCollections,
                "fields.initializeCollections",
            )
        },
        {
            checkIfEqual(
                expected.integerUseBigInteger,
                actual.isUseBigIntegers,
                simpleConfig.isUseBigIntegers,
                "fields.integerUseBigInteger",
            )
        },
        {
            checkIfEqual(
                expected.integerUseLong,
                actual.isUseLongIntegers,
                simpleConfig.isUseLongIntegers,
                "fields.integerUseLong",
            )
        },
        {
            checkIfEqual(
                expected.usePrimitives,
                actual.isUsePrimitives,
                simpleConfig.isUsePrimitives,
                "fields.usePrimitives",
            )
        },
    )
}

private fun checkIfEqual(expected: PluginConfigJs2pDateTime, actual: Js2pConfig, simpleConfig: GenerationConfig) {
    assertAll(
        "dateTime",
        { checkIfEqual(expected.dateFormat, actual.isFormatDates, simpleConfig.isFormatDates, "dateTime.dateFormat") },
        {
            checkIfEqual(
                expected.datePattern,
                actual.customDatePattern,
                simpleConfig.customDatePattern,
                "dateTime.datePattern",
            )
        },
        {
            checkIfEqual(
                expected.dateTimeFormat,
                actual.isFormatDateTimes,
                simpleConfig.isFormatDateTimes,
                "dateTime.dateTimeFormat",
            )
        },
        {
            checkIfEqual(
                expected.dateTimePattern,
                actual.customDateTimePattern,
                simpleConfig.customDateTimePattern,
                "dateTime.dateTimePattern",
            )
        },
        {
            checkIfEqual(
                expected.dateTimeType,
                actual.dateTimeType,
                simpleConfig.dateTimeType,
                "dateTime.dateTimeType",
            )
        },
        { checkIfEqual(expected.dateType, actual.dateType, simpleConfig.dateType, "dateTime.dateType") },
        { checkIfEqual(expected.jodaDate, actual.isUseJodaDates, simpleConfig.isUseJodaDates, "dateTime.jodaDate") },
        {
            checkIfEqual(
                expected.jodaLocalDate,
                actual.isUseJodaLocalDates,
                simpleConfig.isUseJodaLocalDates,
                "dateTime.jodaLocalDate",
            )
        },
        {
            checkIfEqual(
                expected.jodaLocalTime,
                actual.isUseJodaLocalTimes,
                simpleConfig.isUseJodaLocalTimes,
                "dateTime.jodaLocalTime",
            )
        },
        { checkIfEqual(expected.timeFormat, actual.isFormatTimes, simpleConfig.isFormatTimes, "dateTime.timeFormat") },
        {
            checkIfEqual(
                expected.timePattern,
                actual.customTimePattern,
                simpleConfig.customTimePattern,
                "dateTime.timePattern",
            )
        },
        { checkIfEqual(expected.timeType, actual.timeType, simpleConfig.timeType, "dateTime.timeType") },
    )
}

private fun <T> checkIfEqual(expected: Property<T>, actual: T?, simpleConfig: T?, message: String) {
    if (expected.isPresent) {
        Assertions.assertEquals(expected.get(), actual, message)
    } else {
        Assertions.assertEquals(simpleConfig, actual, message)
    }
}

private fun <T> checkIfEqual(
    expected: MapProperty<T, T>,
    actual: Map<T, T>?,
    simpleConfig: Map<T, T>?,
    @Suppress("SameParameterValue") message: String,
) {
    if (expected.isPresent) {
        Assertions.assertEquals(expected.get(), actual, message)
    } else {
        Assertions.assertEquals(simpleConfig, actual, message)
    }
}

fun checkIfEqual(
    expected: SetProperty<String>,
    actual: Array<String>,
    simpleConfig: Array<String>,
    message: String,
) {
    if (expected.isPresent && expected.get().isNotEmpty()) {
        Assertions.assertEquals(HashSet(expected.get()), HashSet(actual.toSet()), message)
    } else {
        Assertions.assertEquals(simpleConfig.toSet(), actual.toSet(), message)
    }
}

fun checkIfEqual(
    expected: Property<String>,
    actual: CharArray,
    simpleConfig: CharArray,
    message: String,
) {
    if (expected.isPresent) {
        Assertions.assertEquals(expected.get(), actual.joinToString(""), message)
    } else {
        Assertions.assertEquals(simpleConfig.joinToString(""), actual.joinToString(""), message)
    }
}

fun <T : Enum<T>> checkIfEqual(
    expected: Property<String>,
    actual: T,
    simpleConfig: T,
    message: String,
) {
    if (expected.isPresent) {
        Assertions.assertEquals(expected.get(), actual.toString(), message)
    } else {
        Assertions.assertEquals(simpleConfig, actual, message)
    }
}

fun <T> checkIfEqual(expected: Property<String>, actual: Class<out T>, simpleConfig: Class<out T>, message: String) {
    if (expected.isPresent) {
        Assertions.assertEquals(expected.get(), actual.canonicalName, message)
    } else {
        Assertions.assertEquals(simpleConfig, actual, message)
    }
}
