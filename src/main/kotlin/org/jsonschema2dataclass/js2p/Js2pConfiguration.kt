package org.jsonschema2dataclass.js2p

import org.gradle.api.Named
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.*
import java.io.FileFilter
import javax.inject.Inject

/**
 * Input-output parameters
 */
abstract class PluginConfigJs2pIO {
    @get: InputFiles
    @get: PathSensitive(PathSensitivity.RELATIVE)
    abstract val source: ConfigurableFileCollection

    @get:Optional
    @get:Input
    abstract val fileExtensions: SetProperty<String>

    @get:Optional
    @get:Input
    abstract val fileFilter: Property<FileFilter>

    @get:Optional
    @get:Input
    abstract val outputEncoding: Property<String>

    @get:Optional
    @get:Input
    abstract val removeOldOutput: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val sourceType: Property<String>

    @get:Optional
    @get:Input
    abstract val sourceSortOrder: Property<String>

    @get:Optional
    @get:Input
    abstract val targetJavaVersion: Property<String>

    @get:Optional
    @get:Input
    abstract val refFragmentPathDelimiters: Property<String>
}

/**
 * Class-level annotations and targeting
 */
abstract class PluginConfigJs2pClass {
    @get:Optional
    @get:Input
    abstract val annotationStyle: Property<String>

    @get:Optional
    @get:Input
    abstract val classNamePrefix: Property<String>

    @get:Optional
    @get:Input
    abstract val classNameSuffix: Property<String>

    @get:Optional
    @get:Input
    abstract val customAnnotator: Property<String>

    @get:Optional
    @get:Input
    abstract val customRuleFactory: Property<String>

    @get:Optional
    @get:Input
    abstract val includeGeneratedAnnotation: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val includeTypeInfo: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val inclusionLevel: Property<String>

    @get:Optional
    @get:Input
    abstract val propertyWordDelimiters: Property<String>

    @get:Optional
    @get:Input
    abstract val serializable: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val targetPackage: Property<String>

    @get:Optional
    @get:Input
    abstract val useTitleAsClassname: Property<Boolean>
}

abstract class PluginConfigJs2pConstructor {
    @get:Optional
    @get:Input
    abstract val constructorsRequiredPropertiesOnly: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val includeAllPropertiesConstructor: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val includeConstructorPropertiesAnnotation: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val includeConstructors: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val includeCopyConstructor: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val includeRequiredPropertiesConstructor: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val parcelable: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val useInnerClassBuilders: Property<Boolean>
}

abstract class PluginConfigJs2pMethod {
    @get:Optional
    @get:Input
    abstract val generateBuilders: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val includeAdditionalProperties: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val includeDynamicAccessors: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val includeDynamicBuilders: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val includeDynamicGetters: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val includeDynamicSetters: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val includeGetters: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val includeHashcodeAndEquals: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val includeJsr303Annotations: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val includeJsr305Annotations: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val includeSetters: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val includeToString: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val toStringExcludes: SetProperty<String>

    @get:Optional
    @get:Input
    abstract val useJakartaValidation: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val useOptionalForGetters: Property<Boolean>
}

abstract class PluginConfigJs2pField {
    @get:Optional
    @get:Input
    abstract val formatTypeMapping: MapProperty<String, String>

    @get:Optional
    @get:Input
    abstract val initializeCollections: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val useBigDecimals: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val useBigIntegers: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val useDoubleNumbers: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val useLongIntegers: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val usePrimitives: Property<Boolean>
}

abstract class PluginConfigJs2pDateTime {
    @get:Optional
    @get:Input
    abstract val customDatePattern: Property<String>

    @get:Optional
    @get:Input
    abstract val customDateTimePattern: Property<String>

    @get:Optional
    @get:Input
    abstract val customTimePattern: Property<String>

    @get:Optional
    @get:Input
    abstract val dateTimeType: Property<String>

    @get:Optional
    @get:Input
    abstract val dateType: Property<String>

    @get:Optional
    @get:Input
    abstract val formatDateTimes: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val formatDates: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val formatTimes: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val timeType: Property<String>

    @get:Optional
    @get:Input
    abstract val useJodaDates: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val useJodaLocalDates: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val useJodaLocalTimes: Property<Boolean>
}

abstract class Js2pConfiguration @Inject constructor(
    private val name: String,
) : Named {
    @Internal
    override fun getName(): String = name

    @get:Optional
    @get:Input
    abstract val constructor: PluginConfigJs2pConstructor

    @get:Optional
    @get:Input
    abstract val dateTime: PluginConfigJs2pDateTime

    @get:Optional
    @get:Input
    abstract val fields: PluginConfigJs2pField

    @get:Optional
    @get:Input
    abstract val io: PluginConfigJs2pIO

    @get:Optional
    @get:Input
    abstract val klass: PluginConfigJs2pClass

    @get:Optional
    @get:Input
    abstract val methods: PluginConfigJs2pMethod
}
