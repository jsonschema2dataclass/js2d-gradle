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
            description = "Set project version based on git tags and commits."
        }
        create("kotlin-target") {
            id = "org.jsonschema2dataclass.internal.kotlin-target"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.lib.KotlinToolchain"
            description = "Set up Kotlin & Java toolchain to use Java 8."
            dependencies {
                compileOnly(libs.kotlin.gradle)
            }
        }
        create("settings-enterprise") {
            id = "org.jsonschema2dataclass.internal.settings-enterprise"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.base.SettingEnterpriseAccept"
            description = "Agree on TOS for Gradle Scans."
            dependencies {
                compileOnly(libs.gradle.enterprise)
            }
        }
        create("agp-compat") {
            id = "org.jsonschema2dataclass.internal.agpcompat"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.AGPCompat8Plugin"
            description = "Rewrite metadata for AGP modules to be able to build with lower Java versions."
        }
        create("library-plugin") {
            id = "org.jsonschema2dataclass.internal.library"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.lib.LibraryPlugin"
            description = "Set up support library defaults."
        }
        create("gradle-plugin") {
            id = "org.jsonschema2dataclass.internal.gradle-plugin"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.lib.GradlePlugin"
            description = "Set up gradle plugin defaults."
        }
        create("plugin-publish") {
            id = "org.jsonschema2dataclass.internal.plugin-publish"
            implementationClass = "org.jsonschema2dataclass.internal.plugin.publishing.PublishingPlugin"
            description = "Set up library publishing settings."
            dependencies {
                implementation(libs.kotlin.dokka)
            }
        }
    }
}

dependencies {
    implementation(project(":common"))
}
