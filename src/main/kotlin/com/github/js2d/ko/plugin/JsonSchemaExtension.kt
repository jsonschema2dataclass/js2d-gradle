package com.github.js2d.ko.plugin

import com.github.js2d.ko.plugin.Constants.Companion.DEFAULT_EXECUTION_NAME
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import java.io.FileFilter
import javax.inject.Inject
import kotlin.reflect.KClass

class JsonSchemaExtension @Inject constructor(
    private val name: String = DEFAULT_EXECUTION_NAME,
    private var objectFactory: ObjectFactory,
    override var annotationStyle: String,
    override var classNamePrefix: String,
    override var classNameSuffix: String,
    override var constructorsRequiredPropertiesOnly: Boolean,
    override var customAnnotator: String,
    override var customDatePattern: String,
    override var customDateTimePattern: String,
    override var customRuleFactory: String,
    override var customTimePattern: String,
    override var dateTimeType: String,
    override var dateType: String,
    override var fileExtensions: List<String>,
    override var fileFilter: FileFilter,
    override var formatDateTimes: Boolean,
    override var formatDates: Boolean,
    override var formatTimes: Boolean,
    override var formatTypeMapping: Map<String, String>,
    override var generateBuilders: Boolean,
    override var includeAdditionalProperties: Boolean,
    override var includeAllPropertiesConstructor: Boolean,
    override var includeConstructorPropertiesAnnotation: Boolean,
    override var includeConstructors: Boolean,
    override var includeCopyConstructor: Boolean,
    override var includeDynamicAccessors: Boolean,
    override var includeDynamicBuilders: Boolean,
    override var includeDynamicGetters: Boolean,
    override var includeDynamicSetters: Boolean,
    override var includeGeneratedAnnotation: Boolean,
    override var includeGetters: Boolean,
    override var includeHashcodeAndEquals: Boolean,
    override var includeJsr303Annotations: Boolean,
    override var includeJsr305Annotations: Boolean,
    override var includeRequiredPropertiesConstructor: Boolean,
    override var includeSetters: Boolean,
    override var includeToString: Boolean,
    override var includeTypeInfo: Boolean,
    override var inclusionLevel: String,
    override var initializeCollections: Boolean,
    override var outputEncoding: String,
    override var parcelable: Boolean,
    override var propertyWordDelimiters: String,
    override var refFragmentPathDelimiters: String,
    override var removeOldOutput: Boolean,
    override var serializable: Boolean,
    override var sourceSortOrder: String,
    override var sourceType: String,
    override var targetPackage: String,
    override var targetVersion: String,
    override var timeType: String,
    override var toStringExcludes: List<String>,
    override var useBigDecimals: Boolean,
    override var useBigIntegers: Boolean,
    override var useDoubleNumbers: Boolean,
    override var useInnerClassBuilders: Boolean,
    override var useJodaDates: Boolean,
    override var useJodaLocalDates: Boolean,
    override var useJodaLocalTimes: Boolean,
    override var useLongIntegers: Boolean,
    override var useOptionalForGetters: Boolean,
    override var usePrimitives: Boolean,
    override var useTitleAsClassname: Boolean
) : JsonSchema2dPluginConfigurationBase() {
    @Suppress("UnstableApiUsage")
    val executions: NamedDomainObjectContainer<JsonSchema2dPluginConfigurationBase> =
        objectFactory.domainObjectContainer(JsonSchema2dPluginConfiguration::class.java)
    @Suppress("UnstableApiUsage")
    val targetDirectoryPrefix: DirectoryProperty = objectFactory.directoryProperty()

    override fun getName(): String {
        return name
    }
}
