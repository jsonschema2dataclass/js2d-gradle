plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

version = "1.0"

gradlePlugin {
    plugins {
        create("internal") {
            id = "org.jsonschema2dataclass.internal.jdk11.spotless"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.SpotlessConfigPlugin"
            description = "Set up Spotless defaults."
            dependencies {
                compileOnly(quality.spotless)
            }
        }
    }
}

dependencies {
    implementation(project(":common"))
}
