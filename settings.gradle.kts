rootProject.name = "JsonSchema2DataClass"

pluginManagement {
    includeBuild("internal")
}

plugins {
    id("com.gradle.enterprise") version "3.17.5"
    id("org.jsonschema2dataclass.internal.settings-enterprise")
}

enableFeaturePreviewQuietly("TYPESAFE_PROJECT_ACCESSORS", "Type-safe project accessors")

// Main plugin
include(":plugin-gradle:plugin")
// common interfaces
include(":plugin-gradle:common")

// Kotlin language compatibility along Gradle versions
include(":plugin-gradle:compat:kotlin")
// Gradle plugin compatibility
include(":plugin-gradle:compat:android")
include(":plugin-gradle:compat:agp34")
include(":plugin-gradle:compat:java")

include(":plugin-gradle:compat:agp7")

// processors:
include(":plugin-gradle:processors:jsonschema2pojo")

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google().content {
            includeGroup("com.android")
            includeGroup("android.arch.lifecycle")
            includeGroup("android.arch.core")
            includeGroupByRegex("com\\.android\\..*")
            includeGroupByRegex("com\\.google\\..*")
            includeGroupByRegex("androidx\\..*")
        }
        mavenCentral()
    }
    versionCatalogs {
        create("agp") {
            from(files("gradle/agp.dependencies.toml"))
        }
        create("libs") {
            from(files("gradle/libs.dependencies.toml"))
        }
        create("processors") {
            from(files("gradle/processors.toml"))
        }
    }
}
