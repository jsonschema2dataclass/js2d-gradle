package org.jsonschema2dataclass.internal.plugin.publishing

import basePluginExtension
import groovy.util.Node
import groovy.util.NodeList
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.register
import org.gradle.plugins.signing.SigningExtension

fun applyPublishing(project: Project, signing: Boolean = false) {
    project.plugins.apply("maven-publish")

    project.configure<PublishingExtension> {
        publications {
            register<MavenPublication>("release") release@{
                if (signing) {
                    project.configure<SigningExtension> {
                        sign(this@release)
                    }
                }
                setupModuleIdentity(project)
                from(project.components["java"])
                setupLinks()
            }
        }
    }
    project.afterEvaluate {
        project.configure<PublishingExtension> {
            publications {
                getByName<MavenPublication>("release") {
                    reorderNodes(project)
                }
            }
        }
    }
}

private fun MavenPublication.setupModuleIdentity(project: Project) {
    project.afterEvaluate {
        artifactId = project.basePluginExtension.archivesName.get()
        version = project.version as String

        pom {
            val projectDescription = project.description?.takeIf { it.contains(": ") }
                ?: error("$project must have a description with format: \"Module Display Name: Module description.\"")
            name.set(projectDescription.substringBefore(": ").also { check(it.isNotBlank()) })
            description.set(projectDescription.substringAfter(": ").also { check(it.isNotBlank()) })
        }
    }
}

private fun MavenPublication.setupLinks() {
    pom {
        url.set("https://github.com/jsonschema2dataclass/js2d-gradle")
        scm {
            connection.set("scm:git:github.com/jsonschema2dataclass/js2d-gradle.git")
            developerConnection.set("scm:git:github.com/jsonschema2dataclass/js2d-gradle.git")
            url.set("https://github.com/jsonschema2dataclass/js2d-gradle/tree/master")
        }
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("eirnym")
                name.set("Eir Nym")
                email.set("eirnym@gmail.com")
            }
        }
    }
}

private fun MavenPublication.reorderNodes(project: Project) {
    fun Node.getChildren(localName: String): NodeList =
        this.get(localName) as NodeList

    fun NodeList.nodes(): List<Node> =
        (this as Iterable<*>).filterIsInstance<Node>()

    fun Node.getChild(localName: String): Node? =
        this.getChildren(localName).nodes().singleOrNull()

    project.afterEvaluate {
        pom.withXml {
            asNode().apply {
                val lastNodes = sequenceOf(
                    getChild("modelVersion"),
                    getChild("groupId"),
                    getChild("artifactId"),
                    getChild("version"),
                    getChild("name"),
                    getChild("description"),
                    getChild("url"),
                    getChild("dependencies"),
                    getChild("scm"),
                    getChild("developers"),
                    getChild("licenses"),
                )
                    .filterNotNull()
                    .toList()

//                lastNodes.forEach { println("found node ${it.name()}") }
                lastNodes.forEach { remove(it) }
                lastNodes.forEach { append(it) }
            }
        }
    }
}
