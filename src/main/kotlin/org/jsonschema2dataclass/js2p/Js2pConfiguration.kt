package org.jsonschema2dataclass.js2p

import org.gradle.api.Named
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import java.io.FileFilter
import javax.inject.Inject

abstract class Js2pConfiguration @Inject constructor(
    private val name: String,
) : Named {
    @Internal
    override fun getName(): String = name

    @get: InputFiles
    @get: PathSensitive(PathSensitivity.RELATIVE)
    abstract val source: ConfigurableFileCollection

    @get: Optional
    abstract val annotationStyle: Property<String>

    @get: Optional
    abstract val classNamePrefix: Property<String>

    @get: Optional
    abstract val classNameSuffix: Property<String>

    @get: Optional
    abstract val constructorsRequiredPropertiesOnly: Property<Boolean>

    @get: Optional
    abstract val customAnnotator: Property<String>

    @get: Optional
    abstract val customDatePattern: Property<String>

    @get: Optional
    abstract val customDateTimePattern: Property<String>

    @get: Optional
    abstract val customRuleFactory: Property<String>

    @get: Optional
    abstract val customTimePattern: Property<String>

    @get: Optional
    abstract val dateTimeType: Property<String>

    @get: Optional
    abstract val dateType: Property<String>

    @get: Optional
    abstract val fileExtensions: SetProperty<String>

    @get: Optional
    abstract val fileFilter: Property<FileFilter>

    @get: Optional
    abstract val formatDateTimes: Property<Boolean>

    @get: Optional
    abstract val formatDates: Property<Boolean>

    @get: Optional
    abstract val formatTimes: Property<Boolean>

    @get: Optional
    abstract val formatTypeMapping: MapProperty<String, String>

    @get: Optional
    abstract val generateBuilders: Property<Boolean>

    @get: Optional
    abstract val includeAdditionalProperties: Property<Boolean>

    @get: Optional
    abstract val includeAllPropertiesConstructor: Property<Boolean>

    @get: Optional
    abstract val includeConstructorPropertiesAnnotation: Property<Boolean>

    @get: Optional
    abstract val includeConstructors: Property<Boolean>

    @get: Optional
    abstract val includeCopyConstructor: Property<Boolean>

    @get: Optional
    abstract val includeDynamicAccessors: Property<Boolean>

    @get: Optional
    abstract val includeDynamicBuilders: Property<Boolean>

    @get: Optional
    abstract val includeDynamicGetters: Property<Boolean>

    @get: Optional
    abstract val includeDynamicSetters: Property<Boolean>

    @get: Optional
    abstract val includeGeneratedAnnotation: Property<Boolean>

    @get: Optional
    abstract val includeGetters: Property<Boolean>

    @get: Optional
    abstract val includeHashcodeAndEquals: Property<Boolean>

    @get: Optional
    abstract val includeJsr303Annotations: Property<Boolean>

    @get: Optional
    abstract val includeJsr305Annotations: Property<Boolean>

    @get: Optional
    abstract val includeRequiredPropertiesConstructor: Property<Boolean>

    @get: Optional
    abstract val includeSetters: Property<Boolean>

    @get: Optional
    abstract val includeToString: Property<Boolean>

    @get: Optional
    abstract val includeTypeInfo: Property<Boolean>

    @get: Optional
    abstract val inclusionLevel: Property<String>

    @get: Optional
    abstract val initializeCollections: Property<Boolean>

    @get: Optional
    abstract val outputEncoding: Property<String>

    @get: Optional
    abstract val parcelable: Property<Boolean>

    @get: Optional
    abstract val propertyWordDelimiters: Property<String>

    @get: Optional
    abstract val refFragmentPathDelimiters: Property<String>

    @get: Optional
    abstract val removeOldOutput: Property<Boolean>

    @get: Optional
    abstract val serializable: Property<Boolean>

    @get: Optional
    abstract val sourceSortOrder: Property<String>

    @get: Optional
    abstract val sourceType: Property<String>

    @get: Optional
    abstract val targetPackage: Property<String>

    @get: Optional
    abstract val targetVersion: Property<String>

    @get: Optional
    abstract val timeType: Property<String>

    @get: Optional
    abstract val toStringExcludes: SetProperty<String>

    @get: Optional
    abstract val useBigDecimals: Property<Boolean>

    @get: Optional
    abstract val useBigIntegers: Property<Boolean>

    @get: Optional
    abstract val useDoubleNumbers: Property<Boolean>

    @get: Optional
    abstract val useInnerClassBuilders: Property<Boolean>

    @get: Optional
    abstract val useJodaDates: Property<Boolean>

    @get: Optional
    abstract val useJodaLocalDates: Property<Boolean>

    @get: Optional
    abstract val useJodaLocalTimes: Property<Boolean>

    @get: Optional
    abstract val useLongIntegers: Property<Boolean>

    @get: Optional
    abstract val useOptionalForGetters: Property<Boolean>

    @get: Optional
    abstract val usePrimitives: Property<Boolean>

    @get: Optional
    abstract val useTitleAsClassname: Property<Boolean>

    @get: Optional
    abstract val useJakartaValidation: Property<Boolean>
}
