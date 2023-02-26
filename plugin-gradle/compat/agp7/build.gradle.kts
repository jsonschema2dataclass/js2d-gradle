plugins {
    `kotlin-dsl-base`
    id("org.jsonschema2dataclass.internal.kotlin-target")
    id("org.jsonschema2dataclass.internal.agpcompat")
}

dependencies {
    compileOnly(projects.pluginGradle.common)
    compileOnly(agp.android.gradle.v7x) {
        exclude(group = "org.jetbrains.kotlin")
    }
}
