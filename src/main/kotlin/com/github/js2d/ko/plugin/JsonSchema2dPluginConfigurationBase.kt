package com.github.js2d.ko.plugin

import org.gradle.api.Named
import java.io.FileFilter

abstract class JsonSchema2dPluginConfigurationBase: Named {
    abstract var  annotationStyle: String
    abstract var  classNamePrefix: String
    abstract var  classNameSuffix: String
    abstract var  constructorsRequiredPropertiesOnly: Boolean
    abstract var  customAnnotator: String
    abstract var  customDatePattern: String
    abstract var  customDateTimePattern: String
    abstract var  customRuleFactory: String
    abstract var  customTimePattern: String
    abstract var  dateTimeType: String
    abstract var  dateType: String
    abstract var  fileExtensions: List<String>
    abstract var  fileFilter: FileFilter
    abstract var  formatDateTimes: Boolean
    abstract var  formatDates: Boolean
    abstract var  formatTimes: Boolean
    abstract var  formatTypeMapping: Map<String,String>
    abstract var  generateBuilders: Boolean
    abstract var  includeAdditionalProperties: Boolean
    abstract var  includeAllPropertiesConstructor: Boolean
    abstract var  includeConstructorPropertiesAnnotation: Boolean
    abstract var  includeConstructors: Boolean
    abstract var  includeCopyConstructor: Boolean
    abstract var  includeDynamicAccessors: Boolean
    abstract var  includeDynamicBuilders: Boolean
    abstract var  includeDynamicGetters: Boolean
    abstract var  includeDynamicSetters: Boolean
    abstract var  includeGeneratedAnnotation: Boolean
    abstract var  includeGetters: Boolean
    abstract var  includeHashcodeAndEquals: Boolean
    abstract var  includeJsr303Annotations: Boolean
    abstract var  includeJsr305Annotations: Boolean
    abstract var  includeRequiredPropertiesConstructor: Boolean
    abstract var  includeSetters: Boolean
    abstract var  includeToString: Boolean
    abstract var  includeTypeInfo: Boolean
    abstract var  inclusionLevel: String
    abstract var  initializeCollections: Boolean
    abstract var  outputEncoding: String
    abstract var  parcelable: Boolean
    abstract var  propertyWordDelimiters: String
    abstract var  refFragmentPathDelimiters: String
    abstract var  removeOldOutput: Boolean
    abstract var  serializable: Boolean
    abstract var  sourceSortOrder: String
    abstract var  sourceType: String
    abstract var  targetPackage: String
    abstract var  targetVersion: String
    abstract var  timeType: String
    abstract var  toStringExcludes: List<String>
    abstract var  useBigDecimals: Boolean
    abstract var  useBigIntegers: Boolean
    abstract var  useDoubleNumbers: Boolean
    abstract var  useInnerClassBuilders: Boolean
    abstract var  useJodaDates: Boolean
    abstract var  useJodaLocalDates: Boolean
    abstract var  useJodaLocalTimes: Boolean
    abstract var  useLongIntegers: Boolean
    abstract var  useOptionalForGetters: Boolean
    abstract var  usePrimitives: Boolean
    abstract var  useTitleAsClassname: Boolean


}
