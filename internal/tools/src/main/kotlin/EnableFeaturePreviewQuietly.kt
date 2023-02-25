import org.gradle.api.initialization.Settings

/**
 * @see <a href="https://github.com/gradle/gradle/issues/19069">Feature request</a>
 */
@Suppress("unused")
fun Settings.enableFeaturePreviewQuietly(name: String, summary: String) {
    enableFeaturePreview(name)

    val logger: Any = org.gradle.util.internal.IncubationLogger::class.java
        .getDeclaredField("INCUBATING_FEATURE_HANDLER")
        .apply { isAccessible = true }
        .get(null)

    @Suppress("UNCHECKED_CAST")
    val features: MutableSet<String> = org.gradle.internal.featurelifecycle.LoggingIncubatingFeatureHandler::class.java
        .getDeclaredField("features")
        .apply { isAccessible = true }
        .get(logger) as MutableSet<String>

    features.add(summary)
}
