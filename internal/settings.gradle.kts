rootProject.name = "Internal plugins"

include(":common")
include(":plugins")

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
        create("pluginDeps") {
            from(files("../gradle/plugins.dependencies.toml"))
        }
    }
}
