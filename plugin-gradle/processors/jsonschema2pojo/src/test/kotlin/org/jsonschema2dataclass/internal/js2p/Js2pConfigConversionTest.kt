package org.jsonschema2dataclass.internal.js2p

import org.gradle.api.GradleScriptException
import org.jsonschema2pojo.AnnotationStyle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Tests the configuration conversion logic in Js2pConfig.
 * Focuses on OUR defaults and error handling, not jsonschema2pojo library behavior.
 */
class Js2pConfigConversionTest {

    @Test
    @DisplayName("null annotationStyle defaults to JACKSON")
    fun nullAnnotationStyleDefaultsToJackson() {
        val config = createJs2pConfig(annotationStyle = null)
        assertEquals(AnnotationStyle.JACKSON, config.annotationStyle)
    }

    @Test
    @DisplayName("explicit annotationStyle GSON preserved")
    fun explicitAnnotationStylePreserved() {
        val config = createJs2pConfig(annotationStyle = "GSON")
        assertEquals(AnnotationStyle.GSON, config.annotationStyle)
    }

    @Test
    @DisplayName("invalid customAnnotator throws GradleScriptException")
    fun invalidCustomAnnotatorThrowsException() {
        val config = createJs2pConfig(customAnnotatorClass = "com.nonexistent.FakeAnnotator")
        val exception = assertThrows(GradleScriptException::class.java) {
            config.customAnnotator // Trigger lazy class loading
        }
        assertTrue(exception.message?.contains("Unable to find class") == true)
    }

    @Test
    @DisplayName("null customAnnotator uses default NoopAnnotator")
    fun nullCustomAnnotatorUsesDefault() {
        val config = createJs2pConfig(customAnnotatorClass = null)
        assertEquals("org.jsonschema2pojo.NoopAnnotator", config.customAnnotator.name)
    }

    private fun createJs2pConfig(
        annotationStyle: String? = null,
        customAnnotatorClass: String? = null,
    ): Js2pConfig {
        return Js2pConfig(
            targetDirectory = File(System.getProperty("java.io.tmpdir")),
            io = Js2pWorkerConfigIO(
                sourceFiles = emptyList(),
                delimitersPropertyWord = null,
                delimitersRefFragmentPath = null,
                fileExtensions = null,
                fileFilter = null,
                outputEncoding = null,
                sourceSortOrder = null,
                sourceType = null,
                targetJavaVersion = null,
            ),
            klass = Js2pWorkerConfigClass(
                androidParcelable = null,
                annotateGenerated = null,
                annotateSerializable = null,
                annotationStyle = annotationStyle,
                customAnnotatorClass = customAnnotatorClass,
                customRuleFactoryClass = null,
                jackson2IncludeTypeInfo = null,
                jackson2InclusionLevel = null,
                namePrefix = null,
                nameSuffix = null,
                nameUseTitle = null,
                targetPackage = null,
            ),
            constructors = Js2pWorkerConfigConstructor(
                allProperties = null,
                annotateConstructorProperties = null,
                copyConstructor = null,
                requiredProperties = null,
            ),
            methods = Js2pWorkerConfigMethod(
                additionalProperties = null,
                annotateJsr303Jakarta = null,
                annotateJsr303 = null,
                annotateJsr305 = null,
                builders = null,
                buildersDynamic = null,
                buildersInnerClass = null,
                getters = null,
                gettersDynamic = null,
                gettersUseOptional = null,
                hashcodeAndEquals = null,
                setters = null,
                settersDynamic = null,
                toStringExcludes = null,
                toStringMethod = null,
            ),
            fields = Js2pWorkerConfigFields(
                floatUseBigDecimal = null,
                floatUseDouble = null,
                formatToTypeMapping = null,
                initializeCollections = null,
                integerUseBigInteger = null,
                integerUseLong = null,
                usePrimitives = null,
            ),
            dateTime = Js2pWorkerConfigDateTime(
                dateFormat = null,
                datePattern = null,
                dateTimeFormat = null,
                dateTimePattern = null,
                dateTimeType = null,
                dateType = null,
                jodaDate = null,
                jodaLocalDate = null,
                jodaLocalTime = null,
                timeFormat = null,
                timePattern = null,
                timeType = null,
            ),
        )
    }
}
