package org.jsonschema2dataclass.internal

import org.gradle.api.GradleException
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.util.GradleVersion
import org.jsonschema2dataclass.internal.task.PLUGIN_ID

internal const val MINIMUM_GRADLE_VERSION = "6.0"

/** Verify if gradle version is above minimum */
internal fun verifyGradleVersion() {
    if (GradleVersion.current() < GradleVersion.version(MINIMUM_GRADLE_VERSION)) {
        throw GradleException(
            "Plugin $PLUGIN_ID requires at least Gradle $MINIMUM_GRADLE_VERSION, " +
                "but you are using ${GradleVersion.current().version}",
        )
    }
}

/** All execution names must be a valid identifiers as they are used as part of Gradle Task names */
private val executionNameRegex = "[a-z][A-Za-z0-9_]*".toRegex()

/** Verify if execution names are passing the regex */
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

/** Error message if there's no executions defined */
private const val ERROR_NO_EXECUTION =
    "No executions defined, behavior to with default execution has been removed " +
        "in plugin $PLUGIN_ID version 6.0.0. " +
        "Please, consider follow migration guide to upgrade plugin properly"

/** Verify if executions are defined */
internal fun <T> verifyExecutions(project: Project, executions: NamedDomainObjectContainer<T>) {
    project.afterEvaluate {
        // this can be reported only after evaluation
        if (executions.size == 0) {
            throw InvalidUserDataException(ERROR_NO_EXECUTION)
        }
    }
}
