rootProject.name = "JsonSchema2DataClass"

pluginManagement {
    includeBuild("internal")
}

plugins {
    id("com.gradle.develocity") version "4.3.1"
    id("org.jsonschema2dataclass.internal.settings-develocity")
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

enableFeaturePreviewQuietly("TYPESAFE_PROJECT_ACCESSORS", "Type-safe project accessors")

// Main plugin
include(":plugin-gradle")

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.dependencies.toml"))
        }
        create("processors") {
            from(files("gradle/processors.toml"))
        }
        create("pluginDeps") {
            from(files("gradle/plugins.dependencies.toml"))
        }
    }
}
