rootProject.name = "JsonSchema2DataClass"

pluginManagement {
    includeBuild("internal")
}

plugins {
    // Allows using classes / functions from gradle/plugins project.
    id("org.jsonschema2dataclass.internal.settings-enterprise")
}

enableFeaturePreviewQuietly("TYPESAFE_PROJECT_ACCESSORS", "Type-safe project accessors")

include(":plugin-gradle:plugin")

include(":plugin-gradle:compat:android")
include(":plugin-gradle:compat:agp34")

// If is for testing on Java 1.8
if (JavaVersion.current() > JavaVersion.VERSION_1_8) {
    include(":plugin-gradle:compat:agp7")
}
include(":plugin-gradle:processors:common")
include(":plugin-gradle:processors:jsonschema2pojo")
include(":plugin-gradle:compat:java")

include(":plugin-gradle:commons:kotlin-compat")
include(":plugin-gradle:commons:test-common")

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
            from(files("internal/agp.dependencies.toml"))
        }
        create("libs") {
            from(files("internal/libs.dependencies.toml"))
        }
    }
}
