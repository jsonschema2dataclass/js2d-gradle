plugins {
    `kotlin-dsl-base`
    id("org.jsonschema2dataclass.internal.kotlin-target")
}

dependencies {
    compileOnly(libs.processor.jsonschema2pojo)
    implementation(projects.pluginGradle.commons.kotlinCompat)
    implementation(projects.pluginGradle.processors.common)

    testImplementation(libs.bundles.junit.tests)
    testImplementation(projects.pluginGradle.commons.testCommon)
    testImplementation(projects.pluginGradle.processors.common)

    testRuntimeOnly(libs.junit.engine)
    testImplementation(gradleTestKit())

    testImplementation(libs.processor.jsonschema2pojo)
}

tasks.test {
    useJUnitPlatform()
    systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_method")
    systemProperty("junit.jupiter.execution.parallel.enabled", "true")
}
