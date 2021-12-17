plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jsonschema2dataclass") version "4.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")

    // see src/main/resources/json/external_dependencies.json
    implementation("joda-time:joda-time:2.10.13")
}

jsonSchema2Pojo {
    targetPackage.set("example")
    propertyWordDelimiters.set("_")
    includeGeneratedAnnotation.set(false)
}