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
            dependencies {
                compileOnly(libs.spotless)
            }
        }
    }
}

dependencies {
    implementation(project(":common"))
}
