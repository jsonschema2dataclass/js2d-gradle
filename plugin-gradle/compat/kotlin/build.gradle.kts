plugins {
    `kotlin-dsl-base`
    id("org.jsonschema2dataclass.internal.library")
}

project.basePluginExtension.archivesName.set("jsonschema2dataclass-kotlin-compat")
description = "Plugin Kotlin Compat: Kotlin compatibility functions"
