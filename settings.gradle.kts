rootProject.name = "JsonSchema2DataClass"

pluginManagement {
    includeBuild("internal")
}

plugins {
    id("com.gradle.develocity") version "4.0.3"
    id("org.jsonschema2dataclass.internal.settings-develocity")
}

enableFeaturePreviewQuietly("TYPESAFE_PROJECT_ACCESSORS", "Type-safe project accessors")

// Main plugin
include(":plugin-gradle:plugin")
// common interfaces
include(":plugin-gradle:common")

// Kotlin language compatibility along Gradle versions
include(":plugin-gradle:compat:kotlin")
// Gradle plugin compatibility
include(":plugin-gradle:compat:java")

// processors:
include(":plugin-gradle:processors:jsonschema2pojo")

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
