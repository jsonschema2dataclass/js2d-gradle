import org.jsonschema2dataclass.internal.plugin.gitVersion

plugins {
    id("com.gradle.plugin-publish") version "1.1.0" apply false
    id("org.jsonschema2dataclass.internal.git-version")

    if (JavaVersion.current() >= JavaVersion.VERSION_11) {
        id("org.jsonschema2dataclass.internal.spotless")
        id("com.diffplug.spotless") version "6.15.0"
    }
}
private val extraName = "org.jsonschema2dataclass.local"
val projectVersion = if (project.extra.has(extraName)) {
    "1.0-SNAPSHOT"
} else {
    gitVersion(project)
}

allprojects {
    group = "org.jsonschema2dataclass"
    version = projectVersion
}

repositories {
    mavenCentral()
}
