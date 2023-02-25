plugins {
    `kotlin-dsl-base`
    id("org.jsonschema2dataclass.internal.kotlin-target")
}

dependencies {
    compileOnly(project(":plugin-gradle:processors:common"))
}
