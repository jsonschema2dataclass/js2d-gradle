plugins {
    java
    id("org.jsonschema2dataclass") version "4.0.1"
}

repositories {
    mavenCentral()
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(8)
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
    source.setFrom(files("${project.rootDir}/src/main/resources/json"))
}
