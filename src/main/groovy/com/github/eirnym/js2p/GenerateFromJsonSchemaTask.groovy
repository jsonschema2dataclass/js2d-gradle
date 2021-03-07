package com.github.eirnym.js2p

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jsonschema2pojo.Jsonschema2Pojo

abstract class GenerateFromJsonSchemaTask extends DefaultTask{
    /**
     * Directory to save generated files to.
     */
    @OutputDirectory
    final DirectoryProperty targetDirectory = getProject().getObjects().directoryProperty()
    /**
     * Directory to save generated files to.
     */
    @InputFiles
    final ConfigurableFileCollection sourceFiles = project.objects.fileCollection()

    @TaskAction
    def generate(){

        def configuration = project.getExtensions().getByType(JsonSchemaExtension)
        if (project.plugins.hasPlugin('java')) {
            project.sourceSets.main.java.srcDirs targetDirectory
        }
        sourceFiles.each { it.mkdir() }

        logger.info 'Using this configuration:\n{}', configuration

        def jS2PConfig = new JS2PConfig(configuration, targetDirectory, sourceFiles)
        Jsonschema2Pojo.generate(jS2PConfig, new GradleRuleLogger(logger))
    }
}
