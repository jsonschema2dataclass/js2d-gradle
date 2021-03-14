/**
 * Copyright © 2010-2014 Nokia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.eirnym.js2p

import groovy.transform.ToString
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory

import javax.inject.Inject

@ToString
class JsonSchemaExtension {
    ConfigurableFileCollection source

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
    JsonSchemaExtension(ObjectFactory objectFactory) {
        this.source = objectFactory.fileCollection()
    }
}
