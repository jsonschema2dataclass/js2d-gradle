plugins {
    `kotlin-dsl-base`
    id("org.jsonschema2dataclass.internal.library")
}

base.archivesName.set("jsonschema2dataclass-kotlin-compat")
description = "Plugin Kotlin Compat: Kotlin compatibility functions"
