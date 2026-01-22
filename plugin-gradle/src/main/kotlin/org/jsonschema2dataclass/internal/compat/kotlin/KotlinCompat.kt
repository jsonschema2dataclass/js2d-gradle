// Kotlin compatibility shims to smooth version differences
package org.jsonschema2dataclass.internal.compat.kotlin

// Kotlin version independent functions as we support quite wide range of Gradle.

import java.util.Locale

/**
 * Kotlin-independent version of making string uppercase
 */
@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "KotlinConstantConditions")
fun String.asUppercase(): String = (this as java.lang.String).toUpperCase(Locale.ROOT)

/** Kotlin-independent version of capitalization. */
fun CharSequence.capitalized(): String = this[0].toString().asUppercase() + substring(1)
