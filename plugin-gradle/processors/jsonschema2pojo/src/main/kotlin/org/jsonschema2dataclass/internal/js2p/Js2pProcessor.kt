package org.jsonschema2dataclass.internal.js2p

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.jsonschema2dataclass.ext.Js2pConfiguration
import org.jsonschema2dataclass.internal.Js2dProcessor
import org.jsonschema2dataclass.internal.task.JS2P_TOOL_NAME
import org.jsonschema2dataclass.internal.task.Js2dGeneratorTaskBase

private const val JS2P_TASK_DESCRIPTION = "Generates Java models from a json schema using jsonschema2pojo"
private const val JS2P_GROUP = "org.jsonschema2pojo"
private const val JS2P_MODULE = "jsonschema2pojo-core"
private const val JS2P_VERSION = "1.2.1"

class Js2pProcessor : Js2dProcessor<Js2pConfiguration> {
    override fun toolNameForTask(): Pair<String, String> = JS2P_TOOL_NAME to JS2P_TASK_DESCRIPTION

    override fun generatorTaskClass(): Class<out Js2dGeneratorTaskBase<Js2pConfiguration>> =
        Js2pGenerationTask::class.java

    override fun toolingMinimalDependencies(project: Project, configuration: Configuration) {
        configuration.defaultDependencies {
            add(project.dependencies.create("$JS2P_GROUP:$JS2P_MODULE:$JS2P_VERSION"))
        }
    }
}
