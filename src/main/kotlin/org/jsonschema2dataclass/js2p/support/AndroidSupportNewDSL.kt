package org.jsonschema2dataclass.js2p

import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException

internal fun applyInternalAndroidNewDSL(extension: Js2pExtension, project: Project) {
    throw ProjectConfigurationException("$TASK_NAME: Android new DSL is not supported...yet", listOf())
}
