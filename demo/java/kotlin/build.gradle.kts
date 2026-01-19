plugins {
    kotlin("jvm") version "2.3.0"
    id("org.jsonschema2dataclass")
}

dependencies {
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.21.0")

    // see src/main/resources/json/external_dependencies.json
    implementation("joda-time:joda-time:2.14.0")
}

jsonSchema2Pojo {
    executions {
        create("main") {
            io.delimitersPropertyWord.set("_")
            io.source.setFrom(files("$projectDir/src/main/resources/json"))
            klass.annotateGenerated.set(false)
            klass.targetPackage.set("example")
        }
    }
}
