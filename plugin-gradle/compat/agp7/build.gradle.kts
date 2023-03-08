plugins {
    `kotlin-dsl-base`
    id("org.jsonschema2dataclass.internal.library")
    id("org.jsonschema2dataclass.internal.agpcompat")
}

dependencies {
    compileOnly(projects.pluginGradle.common)
    compileOnly(agp.android.gradle.v7x) {
        exclude(group = "org.jetbrains.kotlin")
    }
}
