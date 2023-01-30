import org.jsonschema2dataclass.internal.plugin.gitVersion
import org.jsonschema2dataclass.internal.plugin.ktLintFormatVersion
import org.jsonschema2dataclass.internal.plugin.palantirJavaFormatVersion

plugins {
    id("com.gradle.plugin-publish") version "1.1.0" apply false
    id("org.jsonschema2dataclass.internal.spotless")
    if (JavaVersion.current() >= JavaVersion.VERSION_11) {
        id("com.diffplug.spotless") version "6.14.0"
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
val styleCheckers: Configuration by configurations.creating

dependencies {
    styleCheckers("com.pinterest:ktlint:$ktLintFormatVersion")
    styleCheckers("com.palantir.javaformat:palantir-java-format:$palantirJavaFormatVersion")
}
