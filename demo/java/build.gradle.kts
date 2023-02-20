plugins {
    id("org.jsonschema2dataclass") version "6.0.0" apply false
}

subprojects {
    if (project.plugins.hasPlugin("java")) {
        project.extensions.configure<JavaPluginExtension> {
            toolchain.languageVersion.set(JavaLanguageVersion.of(JavaVersion.current().majorVersion.toInt()))
        }
    }
}
