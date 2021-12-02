package com.github.js2d

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input
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

	@Input
	int execution;

	@TaskAction
	def generate(){
		def extension = project.getExtensions().getByType(JsonSchemaExtension)
		if (project.plugins.hasPlugin('java')) {
			project.sourceSets.main.java.srcDirs targetDirectory
		}
		sourceFiles.each { it.mkdir() }

		def jS2PConfig = new Js2dConfig(extension, extension.executions[execution], targetDirectory, sourceFiles)
		logger.info 'Using this configuration:\n{}', jS2PConfig
		Jsonschema2Pojo.generate(jS2PConfig, new GradleRuleLogger(logger))
	}
}
