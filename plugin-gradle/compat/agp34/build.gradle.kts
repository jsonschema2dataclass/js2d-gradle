plugins {
    `kotlin-dsl-base`
    id("org.jsonschema2dataclass.internal.kotlin-target")
}

dependencies {
    compileOnly(project(":plugin-gradle:processors:common"))

    compileOnly(agp.android.gradle.v4x) {
        exclude(group = "org.jetbrains.kotlin")
    }
}
