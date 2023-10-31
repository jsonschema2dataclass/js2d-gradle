plugins {
    kotlin("jvm") version "1.9.20"
    id("org.jsonschema2dataclass")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = JavaVersion.current().toString()
    }
}
dependencies {
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.3")

    // see src/main/resources/json/external_dependencies.json
    implementation("joda-time:joda-time:2.12.5")
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
