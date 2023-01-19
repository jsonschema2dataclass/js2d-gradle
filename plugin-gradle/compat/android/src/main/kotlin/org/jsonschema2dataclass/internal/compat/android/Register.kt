package org.jsonschema2dataclass.internal.compat.android

import org.gradle.api.GradleScriptException
import org.gradle.api.ProjectConfigurationException
import org.jsonschema2dataclass.internal.GradlePluginRegistration
import org.jsonschema2dataclass.internal.task.PLUGIN_ID

fun androidProcessorRegistrationSelector(): GradlePluginRegistration =
    findClass<GradlePluginRegistration>(androidProcessorRegistrationClass()).getDeclaredConstructor().newInstance()

private fun androidProcessorRegistrationClass(): String =
    when (com.android.Version.ANDROID_GRADLE_PLUGIN_VERSION.split(".")[0].toInt()) {
        3, 4 -> "org.jsonschema2dataclass.internal.compat.android.AGP34PluginRegistration"
        7 -> "org.jsonschema2dataclass.internal.compat.android.AGP347PluginRegistration"
        else -> throw ProjectConfigurationException("$PLUGIN_ID: Plugin supports AGP 3.x, 4.x or 7.x", listOf())
    }

private inline fun <reified C> findClass(className: String): Class<out C> {
    return try {
        @Suppress("UNCHECKED_CAST")
        Class.forName(className, true, C::class.java.classLoader) as Class<C>
    } catch (e: ClassNotFoundException) {
        throw GradleScriptException("Unable to find class $className", e)
    }
}
