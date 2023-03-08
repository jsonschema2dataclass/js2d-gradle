import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.getByName

val Project.basePluginExtension: BasePluginExtension
    get() = this.extensions.getByName<BasePluginExtension>("base")

val Project.javaPluginExtension: JavaPluginExtension
    get() = this.extensions.getByName<JavaPluginExtension>("java")

val Project.versionCatalogs: VersionCatalogsExtension
    get() = this.extensions.getByName<VersionCatalogsExtension>("versionCatalogs")

fun Project.isExtraEnabled(name: String): Boolean =
    project.extra.has(name) && project.extra[name].toString().toBoolean()

internal val pluginIds = mapOf(
    "kotlin-dokka" to "org.jetbrains.dokka",
    "nexus-publish" to "io.github.gradle-nexus.publish-plugin",
    "gradle-entrprise" to "com.gradle.enterprise",
    "gradle-publish" to "com.gradle.plugin-publish",
)

internal val classChecking = mapOf(
    "spotless" to "com.diffplug.gradle.spotless.SpotlessExtension",
)

/** If set, gradle plugin will be prepared for a local publication */
internal val EXTRA_LOCAL_PUBLISH = "org.jsonschema2dataclass.internal.local-publish"
