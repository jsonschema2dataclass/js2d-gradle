package net.niebes.jsonschema2kotlin.gradle

import wu.seal.jsontokotlin.DefaultValueStrategy
import wu.seal.jsontokotlin.PropertyTypeStrategy
import wu.seal.jsontokotlin.TargetJsonConverter
import java.io.File

/**
 * TODO a config is called an extension in gradle?
 */
open class JsonSchemaExtension {

    val sourceFiles: File = File("schema")
    var targetDirectory: File? = null
    var isCommentOff = false
    var isOrderByAlphabetical = false
    var isPropertiesVar = false
    var targetJsonConvertLib = TargetJsonConverter.Gson
    var propertyTypeStrategy = PropertyTypeStrategy.NotNullable
    var defaultValueStrategy = DefaultValueStrategy.AvoidNull
    var isNestedClassModel = true

    var customPropertyAnnotationFormatString = "@SerialName(\"%s\")"
    var customAnnotaionImportClassString = "import kotlinx.serialization.SerialName\n" +
            "import kotlinx.serialization.Serializable"

    var customClassAnnotationFormatString = "@Serializable"

    var indent: Int = 4

    var enableMapType: Boolean = true

    var enableMinimalAnnotation = false

    var parenClassTemplate = ""

    var isKeywordPropertyValid = true

    var extensionsConfig = ""
}