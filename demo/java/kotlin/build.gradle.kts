plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jsonschema2dataclass")
}

repositories {
    mavenCentral()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = JavaVersion.current().toString()
    }
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
            io.delimitersPropertyWord.set("_")
            io.source.setFrom(files("${projectDir}/src/main/resources/json"))
            klass.annotateGenerated.set(false)
            klass.targetPackage.set("example")
        }
    }
}
