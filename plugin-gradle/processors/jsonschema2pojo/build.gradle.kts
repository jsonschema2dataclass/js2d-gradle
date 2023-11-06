plugins {
    `kotlin-dsl-base`
    id("org.jsonschema2dataclass.internal.library")
}

base.archivesName.set("jsonschema2dataclass-processor-jsonschema2pojo")
description = "Jsonschema2pojo schema processor compatibility layer: Compatibility layer for Jsonschema2pojo processor."

dependencies {
    compileOnly(libs.processor.jsonschema2pojo)
    implementation(projects.pluginGradle.compat.kotlin)
    implementation(projects.pluginGradle.common)

    testImplementation(libs.bundles.junit.tests)
    testRuntimeOnly(libs.bundles.junit.runtime)
    testImplementation(gradleTestKit())

    testImplementation(libs.processor.jsonschema2pojo)
}

tasks.test {
    useJUnitPlatform()
    systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_method")
    systemProperty("junit.jupiter.execution.parallel.enabled", "true")
}
