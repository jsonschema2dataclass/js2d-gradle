package org.jsonschema2dataclass.internal.js2p

import org.jsonschema2dataclass.ext.*
import java.io.File
import java.io.FileFilter
import java.io.Serializable
import java.net.URL
import java.util.*

internal class Js2pWorkerConfig(
    internal val uuid: UUID,
    internal val targetDirectory: File,
    internal val io: Js2pWorkerConfigIO,
    internal val klass: Js2pWorkerConfigClass,
    internal val constructors: Js2pWorkerConfigConstructor,
    internal val methods: Js2pWorkerConfigMethod,
    internal val fields: Js2pWorkerConfigFields,
    internal val dateTime: Js2pWorkerConfigDateTime,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123L

        fun fromConfig(
            uuid: UUID,
            targetDirectory: File,
            config: Js2pConfiguration,
        ): Js2pWorkerConfig =
            Js2pWorkerConfig(
                uuid,
                targetDirectory,
                workerConvert(config.io),
                workerConvert(config.klass),
                workerConvert(config.constructors),
                workerConvert(config.methods),
                workerConvert(config.fields),
                workerConvert(config.dateTime),
            )
    }
}

internal class Js2pWorkerConfigIO(
    val sourceFiles: List<URL>,
    val delimitersPropertyWord: String?,
    val delimitersRefFragmentPath: String?,
    val fileExtensions: Set<String>?,
    val fileFilter: FileFilter?,
    val outputEncoding: String?,
    val sourceSortOrder: String?,
    val sourceType: String?,
    val targetJavaVersion: String?,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123L
    }
}

internal class Js2pWorkerConfigClass(
    val androidParcelable: Boolean?,
    val annotateGenerated: Boolean?,
    val annotateSerializable: Boolean?,
    val annotationStyle: String?,
    val customAnnotatorClass: String?,
    val customRuleFactoryClass: String?,
    val jackson2IncludeTypeInfo: Boolean?,
    val jackson2InclusionLevel: String?,
    val namePrefix: String?,
    val nameSuffix: String?,
    val nameUseTitle: Boolean?,
    val targetPackage: String?,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123L
    }
}

internal class Js2pWorkerConfigConstructor(
    val allProperties: Boolean?,
    val annotateConstructorProperties: Boolean?,
    val copyConstructor: Boolean?,
    val requiredProperties: Boolean?,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123L
    }
}

internal class Js2pWorkerConfigMethod(
    val additionalProperties: Boolean?,
    val annotateJsr303Jakarta: Boolean?,
    val annotateJsr303: Boolean?,
    val annotateJsr305: Boolean?,
    val builders: Boolean?,
    val buildersDynamic: Boolean?,
    val buildersInnerClass: Boolean?,
    val getters: Boolean?,
    val gettersDynamic: Boolean?,
    val gettersUseOptional: Boolean?,
    val hashcodeAndEquals: Boolean?,
    val setters: Boolean?,
    val settersDynamic: Boolean?,
    val toStringExcludes: Set<String>?,
    val toStringMethod: Boolean?,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123L
    }
}

internal class Js2pWorkerConfigFields(
    val floatUseBigDecimal: Boolean?,
    val floatUseDouble: Boolean?,
    val formatToTypeMapping: Map<String, String>?,
    val initializeCollections: Boolean?,
    val integerUseBigInteger: Boolean?,
    val integerUseLong: Boolean?,
    val usePrimitives: Boolean?,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123L
    }
}

internal class Js2pWorkerConfigDateTime(
    val dateFormat: Boolean?,
    val datePattern: String?,
    val dateTimeFormat: Boolean?,
    val dateTimePattern: String?,
    val dateTimeType: String?,
    val dateType: String?,
    val jodaDate: Boolean?,
    val jodaLocalDate: Boolean?,
    val jodaLocalTime: Boolean?,
    val timeFormat: Boolean?,
    val timePattern: String?,
    val timeType: String?,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123L
    }
}

private fun workerConvert(io: PluginConfigJs2pIO): Js2pWorkerConfigIO =
    Js2pWorkerConfigIO(
        io.source.map { it.toURI().toURL() },
        io.delimitersPropertyWord.orNull,
        io.delimitersRefFragmentPath.orNull,
        io.fileExtensions.orNull,
        io.fileFilter.orNull,
        io.outputEncoding.orNull,
        io.sourceSortOrder.orNull,
        io.sourceType.orNull,
        io.targetJavaVersion.orNull,
    )

private fun workerConvert(klass: PluginConfigJs2pClass): Js2pWorkerConfigClass =
    Js2pWorkerConfigClass(
        klass.androidParcelable.orNull,
        klass.annotateGenerated.orNull,
        klass.annotateSerializable.orNull,
        klass.annotationStyle.orNull,
        klass.customAnnotatorClass.orNull,
        klass.customRuleFactoryClass.orNull,
        klass.jackson2IncludeTypeInfo.orNull,
        klass.jackson2InclusionLevel.orNull,
        klass.namePrefix.orNull,
        klass.nameSuffix.orNull,
        klass.nameUseTitle.orNull,
        klass.targetPackage.orNull,
    )

private fun workerConvert(constructor: PluginConfigJs2pConstructor): Js2pWorkerConfigConstructor =
    Js2pWorkerConfigConstructor(
        constructor.allProperties.orNull,
        constructor.annotateConstructorProperties.orNull,
        constructor.copyConstructor.orNull,
        constructor.requiredProperties.orNull,
    )

private fun workerConvert(methods: PluginConfigJs2pMethod): Js2pWorkerConfigMethod =
    Js2pWorkerConfigMethod(
        methods.additionalProperties.orNull,
        methods.annotateJsr303Jakarta.orNull,
        methods.annotateJsr303.orNull,
        methods.annotateJsr305.orNull,
        methods.builders.orNull,
        methods.buildersDynamic.orNull,
        methods.buildersInnerClass.orNull,
        methods.getters.orNull,
        methods.gettersDynamic.orNull,
        methods.gettersUseOptional.orNull,
        methods.hashcodeAndEquals.orNull,
        methods.setters.orNull,
        methods.settersDynamic.orNull,
        methods.toStringExcludes.orNull,
        methods.toStringMethod.orNull,
    )

private fun workerConvert(fields: PluginConfigJs2pField): Js2pWorkerConfigFields =
    Js2pWorkerConfigFields(
        fields.floatUseBigDecimal.orNull,
        fields.floatUseDouble.orNull,
        fields.formatToTypeMapping.orNull,
        fields.initializeCollections.orNull,
        fields.integerUseBigInteger.orNull,
        fields.integerUseLong.orNull,
        fields.usePrimitives.orNull,
    )

private fun workerConvert(dateTime: PluginConfigJs2pDateTime): Js2pWorkerConfigDateTime =
    Js2pWorkerConfigDateTime(
        dateTime.dateFormat.orNull,
        dateTime.datePattern.orNull,
        dateTime.dateTimeFormat.orNull,
        dateTime.dateTimePattern.orNull,
        dateTime.dateTimeType.orNull,
        dateTime.dateType.orNull,
        dateTime.jodaDate.orNull,
        dateTime.jodaLocalDate.orNull,
        dateTime.jodaLocalTime.orNull,
        dateTime.timeFormat.orNull,
        dateTime.timePattern.orNull,
        dateTime.timeType.orNull,
    )
