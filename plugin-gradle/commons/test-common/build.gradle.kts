plugins {
    `kotlin-dsl`
    id("org.jsonschema2dataclass.internal.kotlin-target")
}

dependencies {
    implementation(platform("org.junit:junit-bom:5.9.2"))

    api("org.junit.jupiter:junit-jupiter-api")
    api("org.junit.jupiter:junit-jupiter-params")
    runtimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
