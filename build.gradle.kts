import com.diffplug.spotless.extra.wtp.EclipseWtpFormatterStep

plugins {
    id("com.gradle.plugin-publish") version "1.1.0" apply false
    id("com.diffplug.spotless") version "6.12.1"
}

group = "org.jsonschema2dataclass"
version = gitVersion(project)

repositories {
    mavenCentral()
}
// This section is required for dependabot to catch changes
val ktlintFormatVersion = "0.48.1"
val googleJavaFormatVersion = "1.13.0"
val orgJsonFormatVersion = "20220924"
val styleCheckers by configurations.registering

dependencies {
    add("styleCheckers", "com.pinterest:ktlint:$ktlintFormatVersion")
    add(
        "styleCheckers",
        "com.google.googlejavaformat:google-java-format:$googleJavaFormatVersion",
    )
}
spotless {
    kotlin {
        targetExclude(".idea", "**/.idea", "plugin/build", "**/build")
        target("**/*.kt")
//        ktlint(ktlintVersion)
        endWithNewline()
    }
    kotlinGradle {
        targetExclude(".idea", "**/.idea", "plugin/build", "**/build")
        target("*.gradle.kts")
        ktlint(ktlintFormatVersion)
        endWithNewline()
    }
    json {
        targetExclude(".idea", "**/.idea", "plugin/build", "**/build")
        target("demo/**/*.json", "src/**/*.json")
        simple()
        endWithNewline()
    }
    format("xml") {
        targetExclude(".idea", "**/.idea", "plugin/build", "**/build")
        target("demo/**src/**/*.xml", "src/**/*.xml")
        eclipseWtp(EclipseWtpFormatterStep.XML)
    }
    if (JavaVersion.current() > JavaVersion.VERSION_11) {
        java {
            targetExclude(".idea", "**/.idea", "plugin/build", "**/build")
            target("demo/**src/**/*.java", "src/**/*.java")
            googleJavaFormat("1.13.0")
        }
    }
}
