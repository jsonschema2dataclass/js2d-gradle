package org.jsonschema2dataclass.support

/**
 * Kotlin version independent functions as we support quite wide range of Gradle
 */

import java.util.*

/**
 * Kotlin-independent version of making string uppercase
 */
@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "KotlinConstantConditions")
internal fun String.asUppercase(): String = (this as java.lang.String).toUpperCase(Locale.ROOT)

/**
 * Kotlin-independent version of capitalization.
 */
internal fun CharSequence.capitalized(): String = this[0].toString().asUppercase() + substring(1)
