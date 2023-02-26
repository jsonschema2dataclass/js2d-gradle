plugins {
    `kotlin-dsl-base`
    id("org.jsonschema2dataclass.internal.kotlin-target")
}

dependencies {
    compileOnly(projects.pluginGradle.common)
    compileOnly(agp.android.gradle.v4x) {
        exclude(group = "org.jetbrains.kotlin")
    }
}
