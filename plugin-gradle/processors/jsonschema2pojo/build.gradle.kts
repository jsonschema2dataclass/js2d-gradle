plugins {
    `kotlin-dsl-base`
    id("org.jsonschema2dataclass.internal.kotlin-target")
}

dependencies {
    compileOnly(libs.processor.jsonschema2pojo)
    implementation(projects.pluginGradle.compat.kotlin)
    implementation(projects.pluginGradle.common)

    testImplementation(libs.bundles.junit.tests)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(gradleTestKit())

    testImplementation(libs.processor.jsonschema2pojo)
}

tasks.test {
    useJUnitPlatform()
    systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_method")
    systemProperty("junit.jupiter.execution.parallel.enabled", "true")
}
