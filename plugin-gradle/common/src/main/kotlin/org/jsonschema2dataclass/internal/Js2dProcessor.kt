package org.jsonschema2dataclass.internal

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.jsonschema2dataclass.internal.task.Js2dGeneratorTaskBase

interface Js2dProcessor<ConfigClass> {
    fun toolNameForTask(): Pair<String, String>

    fun generatorTaskClass(): Class<out Js2dGeneratorTaskBase<ConfigClass>>

    fun toolingMinimalDependencies(project: Project, configuration: Configuration)
}
