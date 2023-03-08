plugins {
    `kotlin-dsl-base`
    id("org.jsonschema2dataclass.internal.library")
}

dependencies {
    implementation(projects.pluginGradle.common)
}
