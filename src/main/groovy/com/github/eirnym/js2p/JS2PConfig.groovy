package com.github.eirnym.js2p

import groovy.transform.ToString
import org.codehaus.groovy.runtime.InvokerHelper
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.jsonschema2pojo.*
import org.jsonschema2pojo.rules.RuleFactory

import java.util.stream.Collectors

@ToString
class JS2PConfig implements GenerationConfig {
    List<URL> sourceFiles
    File targetDirectory

    AnnotationStyle annotationStyle
    String classNamePrefix
    String classNameSuffix
    boolean constructorsRequiredPropertiesOnly
    Class<? extends Annotator> customAnnotator
    String customDatePattern
    String customDateTimePattern
    Class<? extends RuleFactory> customRuleFactory
    String customTimePattern
    String dateTimeType
    String dateType
    String[] fileExtensions
    FileFilter fileFilter
    boolean formatDateTimes
    boolean formatDates
    boolean formatTimes
    Map<String, String> formatTypeMapping
    boolean generateBuilders
    boolean includeAdditionalProperties
    boolean includeAllPropertiesConstructor
    boolean includeConstructorPropertiesAnnotation
    boolean includeConstructors
    boolean includeCopyConstructor
    boolean includeDynamicAccessors
    boolean includeDynamicBuilders
    boolean includeDynamicGetters
    boolean includeDynamicSetters
    boolean includeGeneratedAnnotation
    boolean includeGetters
    boolean includeHashcodeAndEquals
    boolean includeJsr303Annotations
    boolean includeJsr305Annotations
    boolean includeRequiredPropertiesConstructor
    boolean includeSetters
    boolean includeToString
    boolean includeTypeInfo
    InclusionLevel inclusionLevel
    boolean initializeCollections
    String outputEncoding
    boolean parcelable
    char[] propertyWordDelimiters
    String refFragmentPathDelimiters
    boolean removeOldOutput
    boolean serializable
    SourceSortOrder sourceSortOrder
    SourceType sourceType
    String targetPackage
    String targetVersion
    String timeType
    String[] toStringExcludes
    boolean useBigDecimals
    boolean useBigIntegers
    boolean useDoubleNumbers
    boolean useInnerClassBuilders
    boolean useJodaDates
    boolean useJodaLocalDates
    boolean useJodaLocalTimes
    boolean useLongIntegers
    boolean useOptionalForGetters
    boolean usePrimitives
    boolean useTitleAsClassname

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
