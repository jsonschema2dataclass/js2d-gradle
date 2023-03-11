package org.jsonschema2dataclass.internal.plugin.base

import EXTRA_GIT_VERSION
import extraValue
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class GitVersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val version = project.extraValue(EXTRA_GIT_VERSION)
        if (version != null) {
            project.version = version
        } else {
            project.version = gitVersion(project)
        }
    }
}
