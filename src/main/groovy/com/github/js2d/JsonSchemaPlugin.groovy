/**
 * Copyright © 2010-2014 Nokia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.js2d

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.util.GradleVersion

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Registers the plugin's tasks.
 */
class JsonSchemaPlugin implements Plugin<Project> {

    private static final String MINIMUM_GRADLE_VERSION = "6.0"
    public static String TARGET_FOLDER_BASE = 'generated/sources/js2d'
    public static String DEFAULT_EXECUTION_NAME = 'main'
    public static String TASK_NAME = 'generateJsonSchema2DataClass'
    public static String PLUGIN_ID = 'org.jsonschema2dataclass'

    @Override
    void apply(Project project) {
        verifyGradleVersion()

        project.extensions.create('jsonSchema2Pojo', JsonSchemaExtension)
        JsonSchemaExtension extension = project.extensions.getByType(JsonSchemaExtension)
        extension.targetDirectoryPrefix.convention(project.layout.buildDirectory.dir(TARGET_FOLDER_BASE))
        project.afterEvaluate {
            if (project.plugins.hasPlugin('java')) {
                applyJava(extension, project)
            } else if (project.plugins.hasPlugin('com.android.application') || project.plugins.hasPlugin('com.android.library')) {
                applyAndroid(extension, project)
            } else {
                for (Plugin<?> plugin : project.plugins) {
                    project.logger.error(plugin.class.name)
                }
                throw new ProjectConfigurationException("${TASK_NAME}: Java or Android plugin required", [])
            }
        }
    }

    private static void applyJava(JsonSchemaExtension extension, Project project) {
        project.plugins.withId('java') {
            JavaPluginExtension javaPluginExtension = project.extensions.getByType(JavaPluginExtension)

            setupConfigExecutions(
                    project.objects,
                    extension,
                    getJavaJsonPath(javaPluginExtension),
                    false
            )
            def js2pTask = createJS2DTask(project, extension, "", "",
                    { js2pTaskExecution ->
                        js2pTaskExecution.dependsOn('processResources')
                        javaPluginExtension.getSourceSets().getByName('main').allJava.srcDir(js2pTaskExecution.targetDirectory)
                    })
            project.tasks.named('compileJava').configure {
                it.dependsOn(js2pTask)
            }
        }
    }

    private static void applyAndroid(JsonSchemaExtension extension, Project project) {

        setupConfigExecutions(
                project.objects,
                extension,
                getAndroidJsonPath(project),
                System.getProperty('java.specification.version').toFloat() >= 9
        )

        def variants = obtainAndroidVariants(project)
        variants.all { variant ->
            def task = createJS2DTask(
                    project,
                    extension,
                    "For${variant.name.capitalize()}",
                    "${variant.flavorName}/${variant.buildType.name}/",
                    { execution ->
                        variant.registerJavaGeneratingTask(execution, execution.targetDirectory.get().asFile)
                    }
            )
            variant.registerJavaGeneratingTask(task)
        }
    }

    private static Path getJavaJsonPath(JavaPluginExtension javaPluginExtension) {
        return javaPluginExtension
                .sourceSets
        .getByName("main")
        .output
        .resourcesDir
        .toPath()
        .resolve("json")
    }

    private static Path getAndroidJsonPath(Project project) {
        def sets
        if (project.android.hasProperty('sourceSets')) {
            sets = project.android.sourceSets
        } else {
            sets = project.android.sourceSetsContainer
        }

        def main = sets.find { it.name.startsWith("main") }
        return Paths.get(main.resources.source[0], 'json')
    }

    private static def obtainAndroidVariants(Project project) {
        if (project.android.hasProperty('applicationVariants')) {
            return project.android.applicationVariants
        } else if (project.android.hasProperty('libraryVariants')) {
            return project.android.libraryVariants
        } else {
            throw new IllegalStateException('Android project must have applicationVariants or libraryVariants!')
        }
    }

    private static Task createJS2DTask(
            Project project,
            JsonSchemaExtension extension,
            String taskNameSuffix,
            String targetDirectorySuffix,
            Closure postConfigure
    ) {

        Task js2dTask = project.task(
                [
                        description: 'Generates Java classes from a json schema using JsonSchema2Pojo. ',
                        group      : 'Build'
                ],
                "${TASK_NAME}${taskNameSuffix}"
        )

        extension.executions.eachWithIndex { execution, execId ->
            GenerateFromJsonSchemaTask task = createJS2DTaskExecution(
                    project,
                    taskNameSuffix,
                    execId,
                    execution.name,
                    execution.source,
                    extension.targetDirectoryPrefix,
                    targetDirectorySuffix
            )
            postConfigure(task)
            js2dTask.dependsOn(task)
        }
        return js2dTask
    }

    private static GenerateFromJsonSchemaTask createJS2DTaskExecution(
            Project project,
            String taskNameSuffix,
            int executionId,
            String executionName,
            ConfigurableFileCollection source,
            DirectoryProperty targetDirectoryPrefix,
            String targetDirectorySuffix
    ) {
        GenerateFromJsonSchemaTask task = (GenerateFromJsonSchemaTask) project.task(
                [
                        type       : GenerateFromJsonSchemaTask,
                        description: "Generates Java classes from a json schema using JsonSchema2Pojo. Execution ${executionId}",
                        group      : 'Build'
                ],
                "${TASK_NAME}${executionId}${taskNameSuffix}"
        )
        task.execution = executionId
        task.sourceFiles.setFrom(source)
        task.targetDirectory.set(
                targetDirectoryPrefix.dir("${executionName}${targetDirectorySuffix}")
        )
        task.inputs.files({ task.sourceFiles.filter({ it.exists() }) })
                .skipWhenEmpty()
        task.outputs.dir(task.targetDirectory)
        return task
    }

    private static void setupConfigExecutions(
            ObjectFactory objectFactory, JsonSchemaExtension config, Path path, boolean excludeGenerated) {
        if (config.source.isEmpty()) {
            config.source.from(path)
        }
        if (config.executions.isEmpty()) {
            config.executions.add(new JsonSchema2dPluginConfiguration("main", objectFactory))
        }

        for (JsonSchema2dPluginConfiguration execution : config.executions) {
            if (execution.source.isEmpty()) {
                execution.source.from(config.source)
            }
            if (excludeGenerated) {
                execution.includeGeneratedAnnotation = false
                // Temporary fixes #71 and upstream issue #1212 (used Generated annotation is not compatible with AGP 7+)
            }
        }
    }
    private static void verifyGradleVersion() {
        if (GradleVersion.current() < GradleVersion.version(MINIMUM_GRADLE_VERSION)) {
            throw new GradleException("Plugin ${PLUGIN_ID} requires at least Gradle $MINIMUM_GRADLE_VERSION, but you are using ${GradleVersion.current().version}")
        }
    }
}
