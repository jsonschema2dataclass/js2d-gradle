plugins {
    `kotlin-dsl-base`
    id("org.jsonschema2dataclass.internal.library")
}

basePluginExtension.archivesName.set("jsonschema2dataclass-agp34-api-compat")
description = "AGP 3 and 4 Compatibility layer: Compatibility layer for Android Gradle Plugin 3.x and 4.x."

dependencies {
    compileOnly(projects.pluginGradle.common)
    compileOnly(agp.android.gradle.v4x) {
        exclude(group = "org.jetbrains.kotlin")
    }
}
