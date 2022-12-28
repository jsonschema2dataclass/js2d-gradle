plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jsonschema2dataclass") version "5.0.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.1")

    // see src/main/resources/json/external_dependencies.json
    implementation("joda-time:joda-time:2.12.2")
}

jsonSchema2Pojo {
    executions {
        create("main") {
            targetPackage.set("example")
            propertyWordDelimiters.set("_")
            includeGeneratedAnnotation.set(false)
            source.setFrom(files("${project.rootDir}/src/main/resources/json"))
        }
    }
}
