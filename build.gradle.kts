import java.io.ByteArrayOutputStream

plugins {
    groovy
    `java-gradle-plugin`
    kotlin("jvm") version "1.6.0"
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

fun getTag(): String {
    System.getenv("VERSION")?.let {
        val tagVersionToken = it.split("/")
        if (tagVersionToken.size > 2)
            return tagVersionToken[2]
        else
            return tagVersionToken[0]
    }

    return "0.0.0-no-git-version"
}
