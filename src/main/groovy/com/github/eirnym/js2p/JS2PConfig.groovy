package com.github.eirnym.js2p

import groovy.transform.ToString
import org.codehaus.groovy.runtime.InvokerHelper
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.jsonschema2pojo.*
import org.jsonschema2pojo.rules.RuleFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.stream.Collectors

@ToString
class JS2PConfig implements GenerationConfig {
    private static final Logger logger = LoggerFactory.getLogger(JS2PConfig)
    List<URL> sourceFiles
    File targetDirectory

    String targetPackage
    AnnotationStyle annotationStyle
    boolean useTitleAsClassname
    InclusionLevel inclusionLevel
    String classNamePrefix
    String classNameSuffix
    String[] fileExtensions
    Class<? extends Annotator> customAnnotator
    Class<? extends RuleFactory> customRuleFactory
    boolean generateBuilders
    boolean includeTypeInfo
    boolean useInnerClassBuilders
    boolean includeConstructorPropertiesAnnotation
    boolean includeGetters
    boolean includeSetters
    boolean includeAdditionalProperties
    boolean includeDynamicAccessors
    boolean includeDynamicGetters
    boolean includeDynamicSetters
    boolean includeDynamicBuilders
    boolean includeConstructors
    boolean constructorsRequiredPropertiesOnly
    boolean includeRequiredPropertiesConstructor
    boolean includeAllPropertiesConstructor
    boolean includeCopyConstructor
    boolean includeHashcodeAndEquals
    boolean includeJsr303Annotations
    boolean includeJsr305Annotations
    boolean useOptionalForGetters
    boolean includeToString
    boolean includeGeneratedAnnotation
    String[] toStringExcludes
    boolean initializeCollections
    String outputEncoding
    boolean parcelable
    boolean serializable
    char[] propertyWordDelimiters
    boolean removeOldOutput
    SourceType sourceType
    String targetVersion
    boolean useDoubleNumbers
    boolean useBigDecimals
    boolean useJodaDates
    boolean useJodaLocalDates
    boolean useJodaLocalTimes
    String dateTimeType
    String dateType
    String timeType
    boolean useLongIntegers
    boolean useBigIntegers
    boolean usePrimitives
    FileFilter fileFilter
    boolean formatDates
    boolean formatTimes
    boolean formatDateTimes
    String customDatePattern
    String customTimePattern
    String customDateTimePattern
    String refFragmentPathDelimiters
    SourceSortOrder sourceSortOrder
    Map<String, String> formatTypeMapping

    Iterator<URL> getSource() {
        return sourceFiles.iterator()
    }
    private static Map<String, Closure<?>> converters = [
            source                : { value -> null },
            targetDirectory       : { value -> null },
            fileExtensions        : { List<String> fileExtensions -> fileExtensions.toArray() },
            annotationStyle       : { String annotationStyle -> fromEnum(annotationStyle, AnnotationStyle) },
            inclusionLevel        : { String inclusionLevel -> fromEnum(inclusionLevel, InclusionLevel) },
            customAnnotator       : { String customAnnotator -> findClass(customAnnotator) },
            customRuleFactory     : { String customRuleFactory -> findClass(customRuleFactory) },
            toStringExcludes      : { String toStringExcludes -> toStringExcludes.toArray() },
            propertyWordDelimiters: { String propertyWordDelimiters -> propertyWordDelimiters.getChars() },
            sourceType            : { String sourceType -> fromEnum(sourceType, SourceType) },
            sourceSortOrder       : { String sourceSortOrder -> fromEnum(sourceSortOrder, SourceSortOrder) },
    ]

    JS2PConfig(JsonSchemaExtension extension, Provider<Directory> targetDirectory, ConfigurableFileCollection sourceFiles) {
        // Convert known properties
        Map<String, Object> newValues = [:]
        extension.properties.entrySet().each { Map.Entry<String, Object> entry ->
            setNewValue(newValues, entry.key, entry.value)
        }
        InvokerHelper.setProperties(this, new DefaultGenerationConfig().properties)
        InvokerHelper.setProperties(this, newValues)
        this.sourceFiles = sourceFiles.asList().stream().map({ it.toURI().toURL() }).collect(Collectors.toList())
        this.targetDirectory = targetDirectory.get().asFile
    }

    private static def setNewValue(Map<String, Object> properties, String key, Object value) {
        if (value instanceof Provider && value.isPresent()) {
            value = value.get()
        }

        if (value != null) {
            def newValue = convert(key, value)
            if (newValue != null) {
                properties[key] = newValue
            }
        }
    }

    private static def convert(String key, Object value) {
        def closure = converters.get(key)
        if (closure) {
            return closure(value)
        }
        return value
    }

    private static <E extends Enum<E>> E fromEnum(String value, Class<E> enumType) {
        return Enum.valueOf(enumType, value.toUpperCase())
    }

    private static <C> Class<C> findClass(String className) {
        return (Class<C>) Class.forName(className, true, JS2PConfig.class.classLoader)
    }
}
