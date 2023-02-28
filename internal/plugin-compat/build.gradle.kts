plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

version = "1.0"

gradlePlugin {
    plugins {
        create("internal") {
            id = "org.jsonschema2dataclass.internal.spotless"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.SpotlessConfigPlugin"
        }
        create("agp-compat") {
            id = "org.jsonschema2dataclass.internal.agpcompat"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.AGPCompat8Plugin"
        }
    }
}

dependencies {
    compileOnly("com.diffplug.spotless:spotless-plugin-gradle:6.16.0")
}
