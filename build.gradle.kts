import com.diffplug.spotless.extra.wtp.EclipseWtpFormatterStep

plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "1.6.0"
    id("com.gradle.plugin-publish") version "0.18.0"
    id("com.diffplug.spotless") version "6.0.1"
}

version = gitVersion(project)

repositories {
    google().content {
        includeGroup("com.android")
        includeGroup("android.arch.lifecycle")
        includeGroup("android.arch.core")
        includeGroupByRegex("com\\.android\\..*")
        includeGroupByRegex("com\\.google\\..*")
        includeGroupByRegex("androidx\\..*")
        includeModuleByRegex("com\\.android\\..*", "com\\.android\\..*")
        includeModuleByRegex("com\\.google\\..*", "com\\.google\\..*")
        includeModuleByRegex("androidx\\..*", "androidx\\..*")
    }
    mavenCentral()
}

pluginBundle {
    website = "https://github.com/jsonschema2dataclass/js2d-gradle"
    vcsUrl = "https://github.com/jsonschema2dataclass/js2d-gradle.git"
    tags = listOf(
        "json-schema", "jsonschema", "generator", "pojo", "jsonschema2pojo",
        "dataclass", "data", "json", "generation", "jsonschema2dataclass", "java"
    )
}

gradlePlugin {
    plugins {
        create("jsonschema2dataclassPlugin") {
            id = "org.jsonschema2dataclass"

            implementationClass = "org.jsonschema2dataclass.js2p.Js2pPlugin"
            displayName = "Extended Gradle JsonSchema2Pojo Plugin"
            description = "A plugins that generates Java sources from JsonSchema using jsonschema2pojo." +
                " Please, see the GitHub page for details"
        }
    }
}

val provided by configurations.creating
sourceSets {
    main {
        this.compileClasspath += provided
    }
}
dependencies {

    provided("com.android.tools.build:gradle:7.0.3")

    implementation("org.jsonschema2pojo:jsonschema2pojo-core:1.1.1")

    testImplementation(platform("org.junit:junit-bom:5.8.2"))

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation(gradleTestKit())
}

tasks.test {
    useJUnitPlatform()
}

configurations.all {
    resolutionStrategy {
        dependencySubstitution {
            val ktlintVersion = "0.43.2"
            substitute(module("com.pinterest:ktlint")).using(module("com.pinterest:ktlint:$ktlintVersion"))
        }
    }
}

spotless {
    kotlin {
        target("demo/**/*.kt", "src/**/*.kt")
        ktlint()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
        endWithNewline()
    }
    json {
        target("demo/**/*.json", "src/**/*.json")
        simple()
        endWithNewline()
    }
    format("xml") {
        target("demo/**/*.xml", "src/**/*.xml")
        eclipseWtp(EclipseWtpFormatterStep.XML)
    }
}
