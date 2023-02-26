include(":tools")

if (JavaVersion.current() > JavaVersion.VERSION_1_8) {
    include(":plugin-compat")
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
    }
}
