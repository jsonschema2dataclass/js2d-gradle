plugins {
    alias(libs.plugins.gradle.publish) apply false
    id("org.jsonschema2dataclass.internal.git-version")

    if (JavaVersion.current() >= JavaVersion.VERSION_11) {
        id("org.jsonschema2dataclass.internal.spotless")
        alias(libs.plugins.spotless)
    }
}

val projectVersion = project.version

allprojects {
    group = "org.jsonschema2dataclass"
    version = projectVersion
}

if (JavaVersion.current() >= JavaVersion.VERSION_11) {
    jsonschema2dataclassSpotless {
        ktlintVersion = libs.spotless.ktlint.get().version
        palantirVersion = libs.spotless.palantir.get().version
    }
}
