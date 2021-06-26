package com.github.js2d.ko.plugin

import com.github.js2d.GradleRuleLogger
import com.github.js2d.Js2dConfig
import com.github.js2d.JsonSchemaExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jsonschema2pojo.Jsonschema2Pojo
import javax.inject.Inject

class GenerateFromJsonSchemaTask @Inject constructor(project: Project): DefaultTask(){
    /**
     * Directory to save generated files to.
     */
    @get:OutputDirectory
    @Suppress("UnstableApiUsage")
    val targetDirectory: DirectoryProperty= project.objects.directoryProperty()

    /**
     * Directory to save generated files to.
     */
    @get:InputFiles
    @Suppress("UnstableApiUsage")
    val sourceFiles: ConfigurableFileCollection = project.objects.fileCollection()

    @get:Input
    var execution: Int = 0

    @TaskAction
    fun generate() {
        val extension = project.getExtensions().getByType(JsonSchemaExtension.javaClass)
        if (project.plugins.hasPlugin("java")) {
            project.sourceSets.main.java.srcDirs (targetDirectory)
        }

        sourceFiles.forEach { it.mkdir() }

        val jS2PConfig = Js2dConfig(extension, extension.executions[execution], targetDirectory, sourceFiles)
        logger.info ("Using this configuration:\n{}", jS2PConfig)
        Jsonschema2Pojo.generate(jS2PConfig,  GradleRuleLogger(logger))
    }
}