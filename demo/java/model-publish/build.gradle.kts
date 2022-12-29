plugins {
    `maven-publish`
    kotlin("jvm") version "1.8.0"
    id("org.jsonschema2dataclass") version "5.0.0"
}

repositories {
    mavenCentral()
}

project.group = "com.example"
project.version = "1.0"

val targetJSONBaseDir = files("${project.rootDir}/src/main/resources/json")

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.1")

    // see src/main/resources/json/external_dependencies.json
    implementation("joda-time:joda-time:2.12.2")
}

val sourceJar = tasks.create<Jar>("sourceJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().allJava)
    archiveClassifier.set("sources")
    dependsOn("build")
}

val schemasJar = tasks.create<Jar>("schemasJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(targetJSONBaseDir)
    archiveClassifier.set("schemas")
    dependsOn("build")
}

publishing {
    publications {
        create<MavenPublication>("modulePublish") {
            artifact(tasks.named("jar"))
            artifact(sourceJar)
            artifact(schemasJar)
            artifactId = "models"
        }
    }
}

jsonSchema2Pojo {
    executions {
        create("main") {
            targetPackage.set(project.group.toString())
            includeGeneratedAnnotation.set(false)
            source.setFrom(targetJSONBaseDir)
        }
    }
}
