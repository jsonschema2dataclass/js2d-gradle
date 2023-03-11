gradle.settingsEvaluated {
    val localPublishExtra = "org.jsonschema2dataclass.internal.local-publish"
    val localVersionExtra = "org.jsonschema2dataclass.internal.git-version"
    if (extra.has(localPublishExtra) && extra[localPublishExtra].toString().toBoolean()) {
        val localVersion = extra[localVersionExtra]!!.toString()
        pluginManagement {
            repositories {
                mavenCentral()
                gradlePluginPortal()
                exclusiveContent {
                    forRepository {
                        mavenLocal()
                    }
                    filter {
                        includeGroupByRegex(".*.jsonschema2dataclass.*")
                    }
                }
            }
            resolutionStrategy {
                eachPlugin {
                    if (requested.id.name == "jsonschema2dataclass" && requested.version != localVersion) {
                        useModule("org.jsonschema2dataclass:plugin:$localVersion")
                    }
                }
            }
        }
        rootProject {
            buildscript {
                configurations.forEach {
                    it.resolutionStrategy.eachDependency {
                        if (requested.group == "org.jsonschema2dataclass" && requested.version != localVersion) {
                            useVersion(localVersion)
                        }
                    }
                }
            }
        }
    }
}
