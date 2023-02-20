plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

version = "1.0"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("git-version") {
            id = "org.jsonschema2dataclass.internal.git-version"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.GitVersionPlugin"
        }
        create("kotlin-target") {
            id = "org.jsonschema2dataclass.internal.kotlin-target"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.KotlinToolchain"
        }
    }
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
}
