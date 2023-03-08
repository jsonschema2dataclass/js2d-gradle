plugins {
    `kotlin-dsl-base`
    id("org.jsonschema2dataclass.internal.library")
}

dependencies {
    // for Android build tools version :)
    compileOnly(libs.android.tools) {
        exclude(group = "org.jetbrains.kotlin")
    }

    implementation(projects.pluginGradle.compat.kotlin)
    implementation(projects.pluginGradle.common)
    implementation(projects.pluginGradle.compat.agp34)
    implementation(projects.pluginGradle.compat.agp7)
}
