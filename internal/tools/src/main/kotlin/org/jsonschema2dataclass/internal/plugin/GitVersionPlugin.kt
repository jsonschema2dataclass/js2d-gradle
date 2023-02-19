package org.jsonschema2dataclass.internal.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

private const val NO_GIT_VERSION_EXTRA = "org.jsonschema2dataclass.local"

@Suppress("unused")
class GitVersionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (target.extra.has(NO_GIT_VERSION_EXTRA)) {
            target.version = "1.0-SNAPSHOT"
        } else {
            target.version = gitVersion(target)
        }
    }
}
