plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

version = "1.0"

gradlePlugin {
    plugins {
        create("git-version") {
            id = "org.jsonschema2dataclass.internal.git-version"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.base.GitVersionPlugin"
        }
        create("kotlin-target") {
            id = "org.jsonschema2dataclass.internal.kotlin-target"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.lib.KotlinToolchain"
            dependencies {
                compileOnly(libs.kotlin.gradle)
            }
        }
        create("settings-enterprise") {
            id = "org.jsonschema2dataclass.internal.settings-enterprise"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.base.SettingEnterpriseAccept"
            dependencies {
                compileOnly(libs.gradle.enterprise)
            }
        }
        create("agp-compat") {
            id = "org.jsonschema2dataclass.internal.agpcompat"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.AGPCompat8Plugin"
        }
        create("gradle-plugin") {
            id = "org.jsonschema2dataclass.internal.library"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.lib.LibraryPlugin"
        }
        create("support-library") {
            id = "org.jsonschema2dataclass.internal.gradle-plugin"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.lib.GradlePlugin"
        }
    }
}

dependencies {
    implementation(project(":common"))
}
