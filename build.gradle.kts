import com.diffplug.spotless.extra.wtp.EclipseWtpFormatterStep

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
    id("com.gradle.plugin-publish") version "1.1.0"
    id("com.diffplug.spotless") version "6.12.0"
}

group = "org.jsonschema2dataclass"
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

gradlePlugin {
    website.set("https://github.com/jsonschema2dataclass/js2d-gradle")
    vcsUrl.set("https://github.com/jsonschema2dataclass/js2d-gradle.git")

    plugins {
        create("jsonschema2dataclassPlugin") {
            id = "org.jsonschema2dataclass"

            implementationClass = "org.jsonschema2dataclass.js2p.Js2pPlugin"
            displayName = "Extended Gradle JsonSchema2Pojo Plugin"
            description =
                "A plugins that generates Java sources from JsonSchema using jsonschema2pojo. " +
                "Please, see the GitHub page for details"

            tags.set(
                listOf(
                    "json-schema", "jsonschema", "generator", "pojo", "jsonschema2pojo",
                    "dataclass", "data", "json", "generation", "jsonschema2dataclass", "java"
                )
            )
        }
    }
}

if (JavaVersion.current() > JavaVersion.VERSION_11) {
    tasks.withType<JavaCompile> {
        options.release.set(8)
    }
} else {
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

val provided: Configuration by configurations.creating
sourceSets {
    main {
        this.compileClasspath += provided
    }
}
dependencies {

    provided("com.android.tools.build:gradle:7.3.1") {
        exclude(group = "org.jetbrains.kotlin")
    }

    implementation("org.jsonschema2pojo:jsonschema2pojo-core:1.1.2")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation(gradleTestKit())
}

tasks.test {
    useJUnitPlatform()
    systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_method")
    systemProperty("junit.jupiter.execution.parallel.enabled", "true")
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
        targetExclude(".idea", "**/.idea", "build", "**/build")
        target("**/*.kt")
        endWithNewline()
    }
    kotlinGradle {
        targetExclude(".idea", "**/.idea", "build", "**/build")
        target("*.gradle.kts")
        ktlint()
        endWithNewline()
    }
    json {
        targetExclude(".idea", "**/.idea", "build", "**/build")
        target("demo/**/*.json", "src/**/*.json")
        simple()
        endWithNewline()
    }
    format("xml") {
        targetExclude(".idea", "**/.idea", "build", "**/build")
        target("demo/**src/**/*.xml", "src/**/*.xml")
        eclipseWtp(EclipseWtpFormatterStep.XML)
    }
    if (JavaVersion.current() > JavaVersion.VERSION_11) {
        java {
            targetExclude(".idea", "**/.idea", "build", "**/build")
            target("demo/**src/**/*.java", "src/**/*.java")
            googleJavaFormat("1.13.0")
        }
    }
}
