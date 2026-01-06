plugins {
    `java-gradle-plugin` // Gradle plugin base
    `kotlin-dsl`
    id("org.jsonschema2dataclass.internal.gradle-plugin")
}

gradlePlugin {
    website.set("https://github.com/jsonschema2dataclass/js2d-gradle")
    vcsUrl.set("https://github.com/jsonschema2dataclass/js2d-gradle.git")

    plugins {
        register("jsonschema2dataclassPlugin") {
            id = "org.jsonschema2dataclass"

            implementationClass = "org.jsonschema2dataclass.Js2dPlugin"
            displayName = "jsonschema2dataclass plugin"
            description =
                "A plugins that generates Java sources from Json Schema. " +
                "Please, see the GitHub page for details"

            tags.set(
                listOf(
                    "json-schema",
                    "jsonschema",
                    "generator",
                    "pojo",
                    "jsonschema2pojo",
                    "dataclass",
                    "data",
                    "json",
                    "generation",
                    "jsonschema2dataclass",
                    "java",
                    "kotlin",
                    "groovy",
                ),
            )
        }
    }
}

dependencies {
    implementation(projects.pluginGradle.common) {
        exclude(group = "org.jetbrains.kotlin")
    }

    // Java language compatibility layer
    implementation(projects.pluginGradle.compat.kotlin)

    // Processors
    implementation(projects.pluginGradle.processors.jsonschema2pojo)

    // Gradle plugin compatibility
    implementation(projects.pluginGradle.compat.java)

    testImplementation(libs.bundles.junit.tests)
    testRuntimeOnly(libs.bundles.junit.runtime)
    testImplementation(gradleTestKit())
}

tasks.test {
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(JavaVersion.current().majorVersion)
    }
    useJUnitPlatform()
    systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_method")
    systemProperty("junit.jupiter.execution.parallel.enabled", "true")
}

// Integration tests using ProjectBuilder
val integrationTest by sourceSets.creating {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}

val integrationTestImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

val integrationTestRuntimeOnly by configurations.getting {
    extendsFrom(configurations.testRuntimeOnly.get())
}

// Functional tests using Gradle TestKit
val functionalTest by sourceSets.creating {
    compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
    runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
}

val functionalTestImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

val functionalTestRuntimeOnly by configurations.getting {
    extendsFrom(configurations.testRuntimeOnly.get())
}

// Register with java-gradle-plugin for automatic withPluginClasspath() support
gradlePlugin {
    testSourceSets(functionalTest)
}

val integrationTestTask = tasks.register<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"
    testClassesDirs = integrationTest.output.classesDirs
    classpath = integrationTest.runtimeClasspath
    mustRunAfter(tasks.test)
    useJUnitPlatform()
}

val functionalTestTask = tasks.register<Test>("functionalTest") {
    description = "Runs functional tests."
    group = "verification"
    testClassesDirs = functionalTest.output.classesDirs
    classpath = functionalTest.runtimeClasspath
    mustRunAfter(integrationTestTask)
    useJUnitPlatform()

    // No parallelism for TestKit - file contention makes it slower (see gradle/gradle#13421)
}

tasks.check {
    dependsOn(integrationTestTask, functionalTestTask)
}
