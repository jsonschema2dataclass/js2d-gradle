package org.jsonschema2dataclass.internal.js2p

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.jsonschema2dataclass.ext.Js2pConfiguration
import org.jsonschema2dataclass.internal.Js2dProcessor
import org.jsonschema2dataclass.internal.task.JS2P_TOOL_NAME
import org.jsonschema2dataclass.internal.task.Js2dGeneratorTaskBase
import java.util.Properties
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock

private const val JS2P_TASK_DESCRIPTION = "Generates Java models from a json schema using jsonschema2pojo"

private const val RESOURCE_FILE = "processor.properties"
private const val PROPERTY = "processor"

private val JS2PVersion = AtomicReference<String?>(null)
private val readLock = ReentrantLock()

class Js2pProcessor : Js2dProcessor<Js2pConfiguration> {
    private fun readVersion(): String {
        if (JS2PVersion.get() != null) {
            return JS2PVersion.get()!!
        }
        readLock.lock()
        try {
            if (JS2PVersion.get() != null) {
                return JS2PVersion.get()!!
            }
            val stream =
                Js2pProcessor::class.java
                    .classLoader
                    .getResourceAsStream(RESOURCE_FILE)
            if (stream == null) {
                throw GradleException("jsonschema2pojo processor version must be provided")
            }
            val props = Properties()
            stream.use {
                props.load(it)
            }
            JS2PVersion.compareAndSet(null, props[PROPERTY].toString())
        } finally {
            readLock.unlock()
        }

        if (JS2PVersion.get() == null) {
            throw GradleException("jsonschema2pojo processor version must be provided")
        }
        return JS2PVersion.get()!!
    }

    override fun toolNameForTask(): Pair<String, String> = JS2P_TOOL_NAME to JS2P_TASK_DESCRIPTION

    override fun generatorTaskClass(): Class<out Js2dGeneratorTaskBase<Js2pConfiguration>> =
        Js2pGenerationTask::class.java

    override fun toolingMinimalDependencies(project: Project, configuration: Configuration) {
        val version = readVersion()

        configuration.defaultDependencies {
            add(project.dependencies.create(version))
        }
    }
}
