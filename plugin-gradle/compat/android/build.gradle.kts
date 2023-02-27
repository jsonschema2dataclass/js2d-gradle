plugins {
    `kotlin-dsl-base`
    id("org.jsonschema2dataclass.internal.kotlin-target")
}

dependencies {
    // for Android build tools version :)
    compileOnly(libs.android.tools) {
        exclude(group = "org.jetbrains.kotlin")
    }

    implementation(projects.pluginGradle.compat.kotlin)
    compileOnly(projects.pluginGradle.common)
    implementation(projects.pluginGradle.compat.agp34)

    if (JavaVersion.current() > JavaVersion.VERSION_1_8) {
        implementation(projects.pluginGradle.compat.agp7)
    }
}
