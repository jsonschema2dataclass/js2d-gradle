plugins {
    `maven-publish`
    kotlin("jvm") version "2.1.10"
    id("org.jsonschema2dataclass")
}

project.group = "com.example"
project.version = "1.0"

val targetJSONBaseDir = files("$projectDir/src/main/resources/json")

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")

    // see src/main/resources/json/external_dependencies.json
    implementation("joda-time:joda-time:2.13.1")
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
            io.source.setFrom(targetJSONBaseDir)
            klass.annotateGenerated.set(false)
            klass.targetPackage.set(project.group.toString())
        }
    }
}
