package org.jsonschema2dataclass.internal.plugin.base

import org.gradle.api.Plugin
import org.gradle.api.Project

/** Plugin to set project version based on git version. */
@Suppress("unused")
class GitVersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.version = gitVersion(project)
    }
}
