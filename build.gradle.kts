import com.diffplug.spotless.extra.wtp.EclipseWtpFormatterStep

plugins {
    id("com.gradle.plugin-publish") version "1.1.0" apply false
    id("com.diffplug.spotless") version "6.12.0"
}

group = "org.jsonschema2dataclass"
version = gitVersion(project)

repositories {
    mavenCentral()
}

configurations.all {
    resolutionStrategy {
        dependencySubstitution {
            val ktlintVersion = "0.47.1"
            substitute(module("com.pinterest:ktlint")).using(module("com.pinterest:ktlint:$ktlintVersion"))
        }
    }
}

spotless {
    kotlin {
        targetExclude(".idea", "**/.idea", "plugin/build", "**/build")
        target("**/*.kt")
        endWithNewline()
    }
    kotlinGradle {
        targetExclude(".idea", "**/.idea", "plugin/build", "**/build")
        target("*.gradle.kts")
        ktlint()
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
