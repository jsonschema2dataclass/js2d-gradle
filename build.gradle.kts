import java.io.ByteArrayOutputStream

plugins {
    groovy
    `java-gradle-plugin`
    kotlin("jvm") version "1.5.20"
    id("com.gradle.plugin-publish") version "0.15.0"
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

pluginBundle {
    website = "https://github.com/jsonschema2dataclass/js2d-gradle"
    vcsUrl = "https://github.com/jsonschema2dataclass/js2d-gradle.git"
    tags = listOf(
        "json-schema", "jsonschema", "generator", "pojo", "jsonschema2pojo",
        "dataclass", "data", "json", "generation", "jsonschema2dataclass", "java"
    )
}

gradlePlugin {
    plugins {
        create("jsonschema2dataclassPlugin") {
            id = "org.jsonschema2dataclass"
            implementationClass = "com.github.js2d.JsonSchemaPlugin"
            displayName = "Extended Gradle JsonSchema2Pojo Plugin"
            description = "A plugins that generates Java sources from JsonSchema using jsonschema2pojo." +
                    " Please, see the GitHub page for details"
            version = getTag()
        }
    }
}

dependencies {
    implementation("org.codehaus.groovy:groovy-all:3.0.8")
    implementation("org.jsonschema2pojo:jsonschema2pojo-core:1.1.1")

    testImplementation(platform("org.junit:junit-bom:5.7.2"))

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation(gradleTestKit())
}

tasks.test {
    useJUnitPlatform()
}

println("Gradle uses Java ${org.gradle.internal.jvm.Jvm.current()}")
println("Build for version ${getTag()}")

fun String.runCommand(workingDir: File = file("./")): String {
    val parts = this.split("\\s".toRegex())
    val proc = ProcessBuilder(*parts.toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    proc.waitFor(1, TimeUnit.MINUTES)
    return proc.inputStream.bufferedReader().readText().trim()
}

fun getTag(): String {
    val gitVersion = "git describe --tags --dirty --long".runCommand()
    val parts = gitVersion.split("-")
    if (parts.size < 3) {
        throw GradleException("Unknown git version version=\"${gitVersion}\"")
    }
    return if (parts[parts.size - 1] == "dirty") {
        gitVersion
    } else {
        try {
            val count = parts[parts.size - 2].toInt()
            if (count == 0) {
                parts.subList(0, parts.size - 2).joinToString("-")
            } else {
                gitVersion
            }
        } catch (e: NumberFormatException) {
            throw GradleException("Unknown git version \"${gitVersion}\"", e)
        }
    }
}
