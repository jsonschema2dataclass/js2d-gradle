package org.jsonschema2dataclass.internal

import org.gradle.api.*
import org.gradle.util.GradleVersion
import org.jsonschema2dataclass.internal.task.PLUGIN_ID

internal const val MINIMUM_GRADLE_VERSION = "6.0"

private val executionNameRegex = "[a-z][A-Za-z0-9_]*".toRegex()

private const val DEPRECATION_NO_EXECUTION =
    "No executions defined, behavior to with default execution has been removed " +
        "in plugin $PLUGIN_ID version 6.0.0. " +
        "Please, consider follow migration guide to upgrade plugin properly"

internal fun verifyGradleVersion() {
    if (GradleVersion.current() < GradleVersion.version(MINIMUM_GRADLE_VERSION)) {
        throw GradleException(
            "Plugin $PLUGIN_ID requires at least Gradle $MINIMUM_GRADLE_VERSION, " +
                "but you are using ${GradleVersion.current().version}",
        )
    }
}

internal fun <T : Named> verifyExecutionNames(executions: NamedDomainObjectContainer<T>) {
    executions.configureEach {
        if (!executionNameRegex.matches(name)) {
            throw InvalidUserDataException(
                "Plugin $PLUGIN_ID doesn't support execution name \"$name\" provided. " +
                    "Please, rename to match regex \"$executionNameRegex\"",
            )
        }
    }
}

internal fun <T> verifyExecutions(project: Project, configurations: NamedDomainObjectContainer<T>) {
    project.afterEvaluate { // this can be reported only after evaluation
        if (configurations.size == 0) {
            throw InvalidUserDataException(DEPRECATION_NO_EXECUTION)
        }
    }
}
