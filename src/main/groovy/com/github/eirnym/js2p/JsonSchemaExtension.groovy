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
    String targetPackage
    String annotationStyle
    Boolean useTitleAsClassname
    String inclusionLevel
    String classNamePrefix
    String classNameSuffix
    List<String> fileExtensions
    String customAnnotator
    String customRuleFactory
    Boolean generateBuilders
    Boolean includeJsonTypeInfoAnnotation
    Boolean useInnerClassBuilders
    Boolean includeConstructorPropertiesAnnotation
    Boolean includeGetters
    Boolean includeSetters
    Boolean includeAdditionalProperties
    Boolean includeDynamicAccessors
    Boolean includeDynamicGetters
    Boolean includeDynamicSetters
    Boolean includeDynamicBuilders
    Boolean includeConstructors
    Boolean constructorsRequiredPropertiesOnly
    Boolean includeRequiredPropertiesConstructor
    Boolean includeAllPropertiesConstructor
    Boolean includeCopyConstructor
    Boolean includeHashcodeAndEquals
    Boolean includeJsr303Annotations
    Boolean includeJsr305Annotations
    Boolean useOptionalForGetters
    Boolean includeToString
    List<String> toStringExcludes
    Boolean initializeCollections
    String outputEncoding
    Boolean parcelable
    Boolean serializable
    String propertyWordDelimiters
    Boolean removeOldOutput
    String sourceType
    String targetVersion
    Boolean useDoubleNumbers
    Boolean useBigDecimals
    Boolean useJodaDates
    Boolean useJodaLocalDates
    Boolean useJodaLocalTimes
    String dateTimeType
    String dateType
    String timeType
    Boolean useLongIntegers
    Boolean useBigIntegers
    Boolean usePrimitives
    FileFilter fileFilter
    Boolean formatDates
    Boolean formatTimes
    Boolean formatDateTimes
    String customDatePattern
    String customTimePattern
    String customDateTimePattern
    String refFragmentPathDelimiters
    String sourceSortOrder
    Map<String, String> formatTypeMapping
    @Inject
    JsonSchemaExtension(ObjectFactory objectFactory) {
        this.source = objectFactory.fileCollection()
    }
}
