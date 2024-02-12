package org.jsonschema2dataclass.internal.plugin.base

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class GitVersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.version = gitVersion(project)
    }
}
