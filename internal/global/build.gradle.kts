plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}
gradlePlugin {
    plugins {
        create("dummy") {
            id = "org.jsonschema2dataclass.internal.git-version"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.GitVersionPlugin"
        }
    }
}
