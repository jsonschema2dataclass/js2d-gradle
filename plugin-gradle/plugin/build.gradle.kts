plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    alias(libs.plugins.gradle.publish)
    id("org.jsonschema2dataclass.internal.kotlin-target")
}

@Suppress("UnstableApiUsage")
gradlePlugin {
    website.set("https://github.com/jsonschema2dataclass/js2d-gradle")
    vcsUrl.set("https://github.com/jsonschema2dataclass/js2d-gradle.git")

    plugins {
        create("jsonschema2dataclassPlugin") {
            id = "org.jsonschema2dataclass"

            implementationClass = "org.jsonschema2dataclass.Js2dPlugin"
            displayName = "Extended Gradle JsonSchema2Pojo Plugin"
            description =
                "A plugins that generates Java sources from JsonSchema using jsonschema2pojo. " +
                "Please, see the GitHub page for details"

            tags.set(
                listOf(
                    "json-schema", "jsonschema", "generator", "pojo", "jsonschema2pojo",
                    "dataclass", "data", "json", "generation", "jsonschema2dataclass", "java",
                ),
            )
        }
    }
}

dependencies {
    implementation(projects.pluginGradle.common)

    // Java language compatibility layer
    implementation(projects.pluginGradle.compat.kotlinCompat)

    // Processors
    implementation(projects.pluginGradle.processors.jsonschema2pojo)

    // Gradle plugin compatibility
    implementation(projects.pluginGradle.compat.java)
    implementation(projects.pluginGradle.compat.android)

    testImplementation(libs.bundles.junit.tests)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(gradleTestKit())
}

tasks.test {
    useJUnitPlatform()
//    systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_method")
//    systemProperty("junit.jupiter.execution.parallel.enabled", "true")
}
