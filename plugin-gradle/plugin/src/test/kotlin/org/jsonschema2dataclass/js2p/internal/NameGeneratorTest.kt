package org.jsonschema2dataclass.js2p.internal

import org.jsonschema2dataclass.internal.createTaskNameDescription
import org.jsonschema2dataclass.internal.generatePart
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.condition.DisabledForJreRange
import org.junit.jupiter.api.condition.JRE
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

private const val EXECUTION_NAME = "execution"

@DisabledForJreRange(max = JRE.JAVA_8)
class NameGeneratorTest {
    @ParameterizedTest
    @CsvSource(
        "variant,release,true,ForRelease,'for variant release'",
        "flavor,release,true,ForRelease,'for flavor release'",
        "variant,release,false,ForVariantRelease,'for variant release'",
        "flavor,release,false,ForFlavorRelease,'for flavor release'",
    )
    fun generatePartTest(
        suffixName: String,
        suffixValue: String,
        compatible: Boolean,
        expectedName: String,
        expectedDescription: String,
    ) {
        val suffix = suffixName to suffixValue
        val actual = generatePart(suffix, compatible)
        assertEquals(expectedName to expectedDescription, actual)
    }

    @ParameterizedTest
    @CsvSource(
        "Js2p,'tool description',,,,,true," +
            "generateJsonSchema2DataClassConfigExecution," +
            "'tool description for configuration execution'",
        "Js2p,'tool description',,,,,false," +
            "generateJsonSchema2DataClassJs2pConfigExecution," +
            "'tool description for configuration execution'",
        "Js2p,'tool description',variant,release,,,true," +
            "generateJsonSchema2DataClassForReleaseConfigExecution," +
            "'tool description for variant release for configuration execution'",
        "Js2p,'tool description',variant,release,,,false," +
            "generateJsonSchema2DataClassJs2pForVariantReleaseConfigExecution," +
            "'tool description for variant release for configuration execution'",
        "Js2p,'tool description',variant,release,flavor,shop,true," +
            "generateJsonSchema2DataClassForVariantReleaseForFlavorShopConfigExecution," +
            "'tool description for variant release for flavor shop for configuration execution'",
        "Js2p,'tool description',variant,release,flavor,shop,false," +
            "generateJsonSchema2DataClassJs2pForVariantReleaseForFlavorShopConfigExecution," +
            "'tool description for variant release for flavor shop for configuration execution'",
        "Js2d,'tool description',,,,,true," +
            "generateJsonSchema2DataClassJs2dConfigExecution," +
            "'tool description for configuration execution'",
        "Js2d,'tool description',,,,,false," +
            "generateJsonSchema2DataClassJs2dConfigExecution," +
            "'tool description for configuration execution'",
        "Js2d,'tool description',variant,release,,,true," +
            "generateJsonSchema2DataClassJs2dForReleaseConfigExecution," +
            "'tool description for variant release for configuration execution'",
        "Js2d,'tool description',variant,release,,,false," +
            "generateJsonSchema2DataClassJs2dForVariantReleaseConfigExecution," +
            "'tool description for variant release for configuration execution'",
        "Js2d,'tool description',variant,release,flavor,shop,true," +
            "generateJsonSchema2DataClassJs2dForVariantReleaseForFlavorShopConfigExecution," +
            "'tool description for variant release for flavor shop for configuration execution'",
        "Js2d,'tool description',variant,release,flavor,shop,false," +
            "generateJsonSchema2DataClassJs2dForVariantReleaseForFlavorShopConfigExecution," +
            "'tool description for variant release for flavor shop for configuration execution'",
    )
    fun generateSuffixesTest(
        toolName: String,
        toolDescription: String,
        suffix1Name: String?,
        suffix1Value: String?,
        suffix2Name: String?,
        suffix2Value: String?,
        compatible: Boolean,
        expectedName: String,
        expectedDescription: String,
    ) {
        val pairs = mutableListOf<Pair<String, String>>()

        if (suffix1Name != null && suffix1Value != null) {
            pairs.add(suffix1Name to suffix1Value)
        }
        if (suffix2Name != null && suffix2Value != null) {
            pairs.add(suffix2Name to suffix2Value)
        }
        val suffixes = pairs.toMap()

        val tool = toolName to toolDescription

        val result = createTaskNameDescription(
            EXECUTION_NAME,
            suffixes,
            compatible,
            tool,
        )
        val expected = expectedName to expectedDescription
        assertEquals(expected, result)
    }
}
