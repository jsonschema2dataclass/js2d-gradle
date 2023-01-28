plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
    id("com.gradle.plugin-publish")
}

repositories {
    mavenCentral()
}

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
    // Java language compatibility layer
    implementation(project(":plugin-gradle:commons:kotlin-compat"))

    // Processors
    implementation(project(":plugin-gradle:processors:common"))
    implementation(project(":plugin-gradle:processors:jsonschema2pojo"))

    // Gradle applicability
    implementation(project(":plugin-gradle:compat:java"))
    implementation(project(":plugin-gradle:compat:android"))

    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation(project(":plugin-gradle:commons:test-common"))
    testImplementation(project(":plugin-gradle:processors:common"))
    testImplementation(gradleTestKit())
}

tasks.test {
    useJUnitPlatform()
    systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_method")
    systemProperty("junit.jupiter.execution.parallel.enabled", "true")
}
