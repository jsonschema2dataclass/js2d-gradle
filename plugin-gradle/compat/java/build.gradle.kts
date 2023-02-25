plugins {
    `kotlin-dsl-base`
    id("org.jsonschema2dataclass.internal.library")
}

project.basePluginExtension.archivesName.set("jsonschema2dataclass-java-plugin-compat")
description = "Java Plugin Compatibility layer: Compatibility layer for Java Plugin."

dependencies {
    implementation(projects.pluginGradle.common)
}
