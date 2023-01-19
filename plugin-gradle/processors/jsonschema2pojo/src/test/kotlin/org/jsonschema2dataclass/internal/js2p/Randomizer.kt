package org.jsonschema2dataclass.internal.js2p

import org.jsonschema2dataclass.ext.*
import org.jsonschema2dataclass.internal.*
import org.jsonschema2pojo.*
import org.jsonschema2pojo.rules.RuleFactory
import java.io.File

/**
 * Randomize an plugin configuration
 */
internal fun randomize(configuration: Js2pConfiguration): Js2pConfiguration {
    randomize(configuration.io)
    randomize(configuration.klass)
    randomize(configuration.constructors)
    randomize(configuration.methods)
    randomize(configuration.fields)
    randomize(configuration.dateTime)
    return configuration
}

private fun randomize(value: PluginConfigJs2pIO) {
    value.source.setFrom("/${randomString()}")
    value.delimitersPropertyWord.set(nullable(randomString()))
    value.delimitersRefFragmentPath.set(nullable(randomString()))
    value.fileExtensions.set(nullable(randomSet()))
    value.outputEncoding.set(nullable(randomString()))
    value.sourceSortOrder.set(nullable(randomEnum<SourceSortOrder>().toString()))
    value.sourceType.set(nullable(randomEnum<SourceType>().toString()))
    value.targetJavaVersion.set(nullable(randomString()))
}

private fun randomize(value: PluginConfigJs2pClass) {
    value.androidParcelable.set(nullable(randomBoolean()))
    value.annotateGenerated.set(nullable(randomBoolean()))
    value.annotateSerializable.set(nullable(randomBoolean()))
    value.annotationStyle.set(nullable(randomEnum<AnnotationStyle>().toString()))
    value.customAnnotatorClass.set(Annotator::class.java.canonicalName)
    value.customRuleFactoryClass.set(RuleFactory::class.java.canonicalName)
    value.jackson2IncludeTypeInfo.set(nullable(randomBoolean()))
    value.jackson2InclusionLevel.set(nullable(randomEnum<InclusionLevel>().toString()))
    value.namePrefix.set(nullable(randomString()))
    value.nameSuffix.set(nullable(randomString()))
    value.nameUseTitle.set(nullable(randomBoolean()))
    value.targetPackage.set(nullable(randomString()))
}

private fun randomize(value: PluginConfigJs2pConstructor) {
    value.allProperties.set(nullable(randomBoolean()))
    value.annotateConstructorProperties.set(nullable(randomBoolean()))
    value.copyConstructor.set(nullable(randomBoolean()))
    value.requiredProperties.set(nullable(randomBoolean()))
}

private fun randomize(value: PluginConfigJs2pMethod) {
    value.additionalProperties.set(nullable(randomBoolean()))
    value.annotateJsr303Jakarta.set(nullable(randomBoolean()))
    value.annotateJsr303.set(nullable(randomBoolean()))
    value.annotateJsr305.set(nullable(randomBoolean()))
    value.builders.set(nullable(randomBoolean()))
    value.buildersDynamic.set(nullable(randomBoolean()))
    value.buildersInnerClass.set(nullable(randomBoolean()))
    value.getters.set(nullable(randomBoolean()))
    value.gettersDynamic.set(nullable(randomBoolean()))
    value.gettersUseOptional.set(nullable(randomBoolean()))
    value.hashcodeAndEquals.set(nullable(randomBoolean()))
    value.setters.set(nullable(randomBoolean()))
    value.settersDynamic.set(nullable(randomBoolean()))
    value.toStringMethod.set(nullable(randomBoolean()))
    value.toStringExcludes.set(nullable(randomSet()))
}

private fun randomize(value: PluginConfigJs2pField) {
    value.floatUseBigDecimal.set(nullable(randomBoolean()))
    value.floatUseDouble.set(nullable(randomBoolean()))
    value.formatToTypeMapping.set(nullable(randomMap()))
    value.initializeCollections.set(nullable(randomBoolean()))
    value.integerUseBigInteger.set(nullable(randomBoolean()))
    value.integerUseLong.set(nullable(randomBoolean()))
    value.usePrimitives.set(nullable(randomBoolean()))
}

private fun randomize(value: PluginConfigJs2pDateTime) {
    value.dateFormat.set(nullable(randomBoolean()))
    value.datePattern.set(nullable(randomString()))
    value.dateTimeFormat.set(nullable(randomBoolean()))
    value.dateTimePattern.set(nullable(randomString()))
    value.dateTimeType.set(null) // TODO
    value.dateType.set(null) // TODO
    value.jodaDate.set(nullable(randomBoolean()))
    value.jodaLocalDate.set(nullable(randomBoolean()))
    value.jodaLocalTime.set(nullable(randomBoolean()))
    value.timeFormat.set(nullable(randomBoolean()))
    value.timePattern.set(nullable(randomString()))
    value.timeType.set(null) // TODO
}

/**
 * Randomize Generation config
 */
internal fun randomizeGenerationConfig(): GenerationConfig = SimpleGenerationConfig(
    constructorsRequiredPropertiesOnly = randomBoolean(),
    formatDateTimes = randomBoolean(),
    formatDates = randomBoolean(),
    formatTimes = randomBoolean(),
    generateBuilders = randomBoolean(),
    includeAdditionalProperties = randomBoolean(),
    includeAllPropertiesConstructor = randomBoolean(),
    includeConstructorPropertiesAnnotation = randomBoolean(),
    includeConstructors = randomBoolean(),
    includeCopyConstructor = randomBoolean(),
    includeDynamicAccessors = randomBoolean(),
    includeDynamicBuilders = randomBoolean(),
    includeDynamicGetters = randomBoolean(),
    includeDynamicSetters = randomBoolean(),
    includeGeneratedAnnotation = randomBoolean(),
    includeGetters = randomBoolean(),
    includeHashcodeAndEquals = randomBoolean(),
    includeJsr303Annotations = randomBoolean(),
    includeJsr305Annotations = randomBoolean(),
    includeRequiredPropertiesConstructor = randomBoolean(),
    includeSetters = randomBoolean(),
    includeToString = randomBoolean(),
    includeTypeInfo = randomBoolean(),
    initializeCollections = randomBoolean(),
    parcelable = randomBoolean(),
    removeOldOutput = randomBoolean(),
    serializable = randomBoolean(),
    useBigDecimals = randomBoolean(),
    useBigIntegers = randomBoolean(),
    useDoubleNumbers = randomBoolean(),
    useJakartaValidation = randomBoolean(),
    useJodaDates = randomBoolean(),
    useJodaLocalDates = randomBoolean(),
    useJodaLocalTimes = randomBoolean(),
    useLongIntegers = randomBoolean(),
    useOptionalForGetters = randomBoolean(),
    usePrimitives = randomBoolean(),
    useTitleAsClassname = randomBoolean(),
    annotationStyle = randomEnum(),
    classNamePrefix = randomString(),
    classNameSuffix = randomString(),
    customAnnotator = Annotator::class.java,
    customDatePattern = nullable(randomString()),
    customDateTimePattern = nullable(randomString()),
    customRuleFactory = RuleFactory::class.java,
    customTimePattern = nullable(randomString()),
    dateTimeType = nullable(randomString()),
    dateType = nullable(randomString()),
    fileExtensions = randomList(),
    fileFilter = null,
    formatTypeMapping = randomMap(),
    inclusionLevel = randomEnum(),
    outputEncoding = randomString(),
    propertyWordDelimiters = randomString(),
    refFragmentPathDelimiters = randomString(),
    source = null,
    sourceSortOrder = randomEnum(),
    sourceType = randomEnum(),
    targetDirectory = File("/"),
    targetPackage = randomString(),
    targetVersion = randomString(),
    timeType = nullable(randomString()),
    toStringExcludes = randomList(),
)
