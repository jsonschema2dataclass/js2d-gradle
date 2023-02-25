plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

version = "1.0"

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
        create("settings-enterprise") {
            id = "org.jsonschema2dataclass.internal.settings-enterprise"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.SettingEnterpriseAccept"
            dependencies {
                compileOnly(libs.gradle.enterprise)
            }
        }
    }
}

dependencies {
    compileOnly(libs.kotlin.gradle)
}
