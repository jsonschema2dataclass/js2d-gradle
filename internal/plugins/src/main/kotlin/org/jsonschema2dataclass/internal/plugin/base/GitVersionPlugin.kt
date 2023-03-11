package org.jsonschema2dataclass.internal.plugin.base

import EXTRA_NO_GIT_VERSION
import isExtraEnabled
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class GitVersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        if (project.isExtraEnabled(EXTRA_NO_GIT_VERSION)) {
            project.version = "1.0-SNAPSHOT"
        } else {
            project.version = gitVersion(project)
        }
    }
}
