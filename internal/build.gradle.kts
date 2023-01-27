plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

gradlePlugin {
    plugins {
        create("internal") {
            id = "org.jsonschema2dataclass.internal"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.InternalPlugin"
        }
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    compileOnly("com.diffplug.spotless:spotless-plugin-gradle:6.14.0")
}
