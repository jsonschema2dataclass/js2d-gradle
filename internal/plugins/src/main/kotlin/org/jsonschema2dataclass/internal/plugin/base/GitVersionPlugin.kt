package org.jsonschema2dataclass.internal.plugin.base

import isExtraEnabled
import org.gradle.api.Plugin
import org.gradle.api.Project

private const val NO_GIT_VERSION_EXTRA = "org.jsonschema2dataclass.local"

@Suppress("unused")
class GitVersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        if (project.isExtraEnabled(NO_GIT_VERSION_EXTRA)) {
            project.version = "1.0-SNAPSHOT"
        } else {
            project.version = gitVersion(project)
        }
    }
}
