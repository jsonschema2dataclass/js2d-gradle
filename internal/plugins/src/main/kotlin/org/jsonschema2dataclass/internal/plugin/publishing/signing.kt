package org.jsonschema2dataclass.internal.plugin.publishing

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.plugins.signing.SigningExtension

/**
 * -PsigningKey to gradlew, or ORG_GRADLE_PROJECT_signingKey env var
 *
 * -PsigningPassword to gradlew, or ORG_GRADLE_PROJECT_signingPassword env var
 */
fun applySigning(project: Project): Boolean {
    // val signingKeyId: String? by project // Gradle 6+ only
    val signingKey: String? by project
    val signingPassword: String? by project

    val credentials = if (signingKey != null && signingPassword != null) {
        signingKey to signingPassword
    } else {
        return false
    }

    project.plugins.apply("signing")
    project.configure<SigningExtension> {
        useInMemoryPgpKeys(credentials.first, credentials.second)
    }
    return true
}
