package org.jsonschema2dataclass.internal

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.jsonschema2dataclass.internal.task.Js2dGeneratorTaskBase

/** Processor definition. */
interface Js2dProcessor<ConfigClass> {
    /** Intended task name and description. */
    fun toolNameForTask(): Pair<String, String>

    /** Class of a task for a generator. */
    fun generatorTaskClass(): Class<out Js2dGeneratorTaskBase<ConfigClass>>

    /** Add bare minimum tooling dependencies into configuration. */
    fun toolingMinimalDependencies(project: Project, configuration: Configuration)
}
