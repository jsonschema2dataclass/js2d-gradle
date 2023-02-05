plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

gradlePlugin {
    plugins {
        create("internal") {
            id = "org.jsonschema2dataclass.internal.spotless"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.SpotlessPlugin"
        }
        create("agp-compat") {
            id = "org.jsonschema2dataclass.internal.agpcompat"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.AGPCompat8Plugin"
        }
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    compileOnly("com.diffplug.spotless:spotless-plugin-gradle:6.14.1")
}
