plugins {
    `java-gradle-plugin` // Gradle plugin base
    `kotlin-dsl`
    id("org.jsonschema2dataclass.internal.gradle-plugin")
}

@Suppress("UnstableApiUsage")
gradlePlugin {
    website.set("https://github.com/jsonschema2dataclass/js2d-gradle")
    vcsUrl.set("https://github.com/jsonschema2dataclass/js2d-gradle.git")

    plugins {
        create("jsonschema2dataclassPlugin") {
            id = "org.jsonschema2dataclass"

            implementationClass = "org.jsonschema2dataclass.Js2dPlugin"
            displayName = "jsonschema2dataclass plugin"
            description =
                "A plugins that generates Java sources from Json Schema. " +
                "Please, see the GitHub page for details"

            tags.set(
                listOf(
                    "json-schema", "jsonschema", "generator", "pojo", "jsonschema2pojo",
                    "dataclass", "data", "json", "generation", "jsonschema2dataclass", "java",
                    "kotlin", "groovy", "android",
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
    implementation(projects.pluginGradle.compat.android)

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
