plugins {
    `kotlin-dsl-base`
    id("org.jsonschema2dataclass.internal.library")
    id("org.jsonschema2dataclass.internal.agpcompat")
}

base.archivesName.set("jsonschema2dataclass-agp7-api-compat")
description = "AGP 7 Compatibility layer: Compatibility layer for Android Gradle Plugin 7.x."

dependencies {
    compileOnly(projects.pluginGradle.common)
    compileOnly(agp.android.gradle.v7x) {
        exclude(group = "org.jetbrains.kotlin")
    }
}
