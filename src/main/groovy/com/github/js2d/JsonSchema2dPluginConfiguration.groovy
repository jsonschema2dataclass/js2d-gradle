package com.github.js2d

import groovy.transform.ToString
import org.gradle.api.Named
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory

import javax.inject.Inject

@ToString
class JsonSchema2dPluginConfiguration implements Named {
	final ConfigurableFileCollection source
	final String name

	String annotationStyle
	String classNamePrefix
	String classNameSuffix
	Boolean constructorsRequiredPropertiesOnly
	String customAnnotator
	String customDatePattern
	String customDateTimePattern
	String customRuleFactory
	String customTimePattern
	String dateTimeType
	String dateType
	List<String> fileExtensions
	FileFilter fileFilter
	Boolean formatDateTimes
	Boolean formatDates
	Boolean formatTimes
	Map<String, String> formatTypeMapping
	Boolean generateBuilders
	Boolean includeAdditionalProperties
	Boolean includeAllPropertiesConstructor
	Boolean includeConstructorPropertiesAnnotation
	Boolean includeConstructors
	Boolean includeCopyConstructor
	Boolean includeDynamicAccessors
	Boolean includeDynamicBuilders
	Boolean includeDynamicGetters
	Boolean includeDynamicSetters
	Boolean includeGeneratedAnnotation
	Boolean includeGetters
	Boolean includeHashcodeAndEquals
	Boolean includeJsr303Annotations
	Boolean includeJsr305Annotations
	Boolean includeRequiredPropertiesConstructor
	Boolean includeSetters
	Boolean includeToString
	Boolean includeTypeInfo
	String inclusionLevel
	Boolean initializeCollections
	String outputEncoding
	Boolean parcelable
	String propertyWordDelimiters
	String refFragmentPathDelimiters
	Boolean removeOldOutput
	Boolean serializable
	String sourceSortOrder
	String sourceType
	String targetPackage
	String targetVersion
	String timeType
	List<String> toStringExcludes
	Boolean useBigDecimals
	Boolean useBigIntegers
	Boolean useDoubleNumbers
	Boolean useInnerClassBuilders
	Boolean useJodaDates
	Boolean useJodaLocalDates
	Boolean useJodaLocalTimes
	Boolean useLongIntegers
	Boolean useOptionalForGetters
	Boolean usePrimitives
	Boolean useTitleAsClassname

	@Inject
	JsonSchema2dPluginConfiguration(String name, ObjectFactory objectFactory) {
		this.name = name
		this.source = objectFactory.fileCollection()
	}
}
