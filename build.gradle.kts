plugins {
    alias(libs.plugins.gradle.publish) apply false
    id("org.jsonschema2dataclass.internal.git-version")

    if (JavaVersion.current() >= JavaVersion.VERSION_11) {
        id("org.jsonschema2dataclass.internal.jdk11.spotless")
        alias(quality.plugins.spotless)
    }
}

val projectVersion: Any = project.version

allprojects {
    group = "org.jsonschema2dataclass"
    version = projectVersion
}
