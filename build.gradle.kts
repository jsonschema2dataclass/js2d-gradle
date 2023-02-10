import org.jsonschema2dataclass.internal.plugin.gitVersion

plugins {
    id("com.gradle.plugin-publish") version "1.1.0" apply false
    id("org.jsonschema2dataclass.internal.spotless")
    if (JavaVersion.current() >= JavaVersion.VERSION_11) {
        id("com.diffplug.spotless") version "6.15.0"
    }
}

val projectVersion = gitVersion(project)

allprojects {
    group = "org.jsonschema2dataclass"
    version = projectVersion
}

repositories {
    mavenCentral()
}

// This section is required for dependabot to catch changes
