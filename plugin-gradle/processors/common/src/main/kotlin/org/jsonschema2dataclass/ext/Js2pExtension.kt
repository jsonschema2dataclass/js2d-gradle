/**
 * JsonSchema 2 Pojo configuration extension
 */
package org.jsonschema2dataclass.ext

import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.invoke
import java.io.FileFilter
import javax.inject.Inject

/**
 * Input-output parameters
 */
abstract class PluginConfigJs2pIO {
    @get:Input
    @get:Optional
    abstract val delimitersPropertyWord: Property<String>

    @get:Input
    @get:Optional
    abstract val delimitersRefFragmentPath: Property<String>

    @get:Input
    @get:Optional
    abstract val fileExtensions: SetProperty<String>

    @get:Input
    @get:Optional
    abstract val fileFilter: Property<FileFilter>

    @get:Input
    @get:Optional
    abstract val outputEncoding: Property<String>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val source: ConfigurableFileCollection

    @get:Input
    @get:Optional
    abstract val sourceSortOrder: Property<String>

    @get:Input
    @get:Optional
    abstract val sourceType: Property<String>

    @get:Input
    @get:Optional
    abstract val targetJavaVersion: Property<String>
}

/**
 * Class-level annotations and targeting
 */
abstract class PluginConfigJs2pClass {
    @get:Input
    @get:Optional
    abstract val androidParcelable: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val annotateGenerated: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val annotateSerializable: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val annotationStyle: Property<String>

    @get:Input
    @get:Optional
    abstract val customAnnotatorClass: Property<String>

    @get:Input
    @get:Optional
    abstract val customRuleFactoryClass: Property<String>

    @get:Input
    @get:Optional
    abstract val jackson2IncludeTypeInfo: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val jackson2InclusionLevel: Property<String>

    @get:Input
    @get:Optional
    abstract val namePrefix: Property<String>

    @get:Input
    @get:Optional
    abstract val nameSuffix: Property<String>

    @get:Input
    @get:Optional
    abstract val nameUseTitle: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val targetPackage: Property<String>
}

abstract class PluginConfigJs2pConstructor {
    @get:Input
    @get:Optional
    abstract val allProperties: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val annotateConstructorProperties: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val copyConstructor: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val requiredProperties: Property<Boolean>
}

abstract class PluginConfigJs2pMethod {
    @get:Input
    @get:Optional
    abstract val additionalProperties: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val annotateJsr303Jakarta: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val annotateJsr303: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val annotateJsr305: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val builders: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val buildersDynamic: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val buildersInnerClass: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val getters: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val gettersDynamic: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val gettersUseOptional: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val hashcodeAndEquals: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val setters: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val settersDynamic: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val toStringMethod: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val toStringExcludes: SetProperty<String>
}

abstract class PluginConfigJs2pField {
    @get:Input
    @get:Optional
    abstract val floatUseBigDecimal: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val floatUseDouble: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val formatToTypeMapping: MapProperty<String, String>

    @get:Input
    @get:Optional
    abstract val initializeCollections: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val integerUseBigInteger: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val integerUseLong: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val usePrimitives: Property<Boolean>
}

abstract class PluginConfigJs2pDateTime {
    @get:Input
    @get:Optional
    abstract val dateFormat: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val datePattern: Property<String>

    @get:Input
    @get:Optional
    abstract val dateTimeFormat: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val dateTimePattern: Property<String>

    @get:Input
    @get:Optional
    abstract val dateTimeType: Property<String>

    @get:Input
    @get:Optional
    abstract val dateType: Property<String>

    @get:Input
    @get:Optional
    abstract val jodaDate: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val jodaLocalDate: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val jodaLocalTime: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val timeFormat: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val timePattern: Property<String>

    @get:Input
    @get:Optional
    abstract val timeType: Property<String>
}

abstract class Js2pConfiguration @Inject constructor(
    private val name: String,
) : Named {
    @Internal
    override fun getName(): String = name

    @get:Nested
    @get:Optional
    abstract val constructors: PluginConfigJs2pConstructor

    @get:Nested
    @get:Optional
    abstract val dateTime: PluginConfigJs2pDateTime

    @get:Nested
    @get:Optional
    abstract val fields: PluginConfigJs2pField

    @get:Nested
    @get:Optional
    abstract val io: PluginConfigJs2pIO

    @get:Nested
    @get:Optional
    abstract val klass: PluginConfigJs2pClass

    @get:Nested
    @get:Optional
    abstract val methods: PluginConfigJs2pMethod

    fun io(action: Action<PluginConfigJs2pIO>) {
        action(io)
    }

    fun klass(action: Action<PluginConfigJs2pClass>) {
        action(klass)
    }

    fun constructors(action: Action<PluginConfigJs2pConstructor>) {
        action(constructors)
    }

    fun methods(action: Action<PluginConfigJs2pMethod>) {
        action(methods)
    }

    fun fields(action: Action<PluginConfigJs2pField>) {
        action(fields)
    }

    fun dateTime(action: Action<PluginConfigJs2pDateTime>) {
        action(dateTime)
    }
}

abstract class Js2pExtension {
    abstract val executions: NamedDomainObjectContainer<Js2pConfiguration>

    @get: PathSensitive(PathSensitivity.RELATIVE)
    abstract val targetDirectoryPrefix: DirectoryProperty
}
