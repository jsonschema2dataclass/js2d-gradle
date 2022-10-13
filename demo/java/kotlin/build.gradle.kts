plugins {
    kotlin("jvm") version "1.7.20"
    id("org.jsonschema2dataclass") version "4.4.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4.1")

    // see src/main/resources/json/external_dependencies.json
    implementation("joda-time:joda-time:2.11.2")
}

jsonSchema2Pojo {
    targetPackage.set("example")
    propertyWordDelimiters.set("_")
    includeGeneratedAnnotation.set(false)
}
