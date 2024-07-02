plugins {
    alias(pluginDeps.plugins.gradle.publish) apply false
    id("org.jsonschema2dataclass.internal.git-version")
}

val projectVersion: Any = project.version

allprojects {
    group = "org.jsonschema2dataclass"
    version = projectVersion
}
