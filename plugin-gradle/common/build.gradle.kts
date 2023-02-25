plugins {
    `kotlin-dsl-base`
    id("org.jsonschema2dataclass.internal.library")
}

project.basePluginExtension.archivesName.set("jsonschema2dataclass-plugin-common")
description = "Common processor compatibility layer: Compatibility layer for schema processors."
