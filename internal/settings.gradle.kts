rootProject.name = "Internal plugins"

include(":common")
include(":plugins")

// Support for Spotless and other Java 11+ Plugins
if (JavaVersion.current() >= JavaVersion.VERSION_11) {
    include(":plugins-jdk11")
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.dependencies.toml"))
        }
        create("quality") {
            from(files("../gradle/quality.dependencies.toml"))
        }
    }
}
