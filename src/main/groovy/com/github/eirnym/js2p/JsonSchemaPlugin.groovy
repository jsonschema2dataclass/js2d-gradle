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
package com.github.eirnym.js2p

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.plugins.JavaPluginConvention

import java.nio.file.Paths

/**
 * Registers the plugin's tasks.
 */
class JsonSchemaPlugin implements Plugin<Project> {
    private static String BASE_FOLDER = 'generated/sources/js2d'
    public static String TASK_NAME = 'generateJsonSchema2DataClass'

    @Override
    void apply(Project project) {

        project.extensions.create('jsonSchema2Pojo', JsonSchemaExtension)

        project.plugins.withId('java') {
            JsonSchemaExtension config = project.extensions.getByType(JsonSchemaExtension)
            ConfigurableFileCollection sourceFiles
            JavaPluginConvention javaConvention = project.getConvention().plugins['java'] as JavaPluginConvention

            if (config.source.isEmpty()) {
                config.source.setFrom(javaConvention.getSourceSets().getByName('main').output.resourcesDir.toPath().resolve('json'))
            }

            GenerateFromJsonSchemaTask js2pTask = createJS2DTask(
                    project,
                    TASK_NAME,
                    config.source,
                    "$BASE_FOLDER"
            )

            js2pTask.dependsOn('processResources')
            javaConvention.getSourceSets().getByName('main').allJava.srcDir(js2pTask.targetDirectory)
            project.tasks.named('compileJava').configure {
                it.dependsOn(js2pTask)
            }
        }
        project.afterEvaluate {
            if (project.plugins.hasPlugin('java')) {
                // do nothing
            } else if (project.plugins.hasPlugin('com.android.application') || project.plugins.hasPlugin('com.android.library')) {
                def variants
                if (project.android.hasProperty('applicationVariants')) {
                    variants = project.android.applicationVariants
                } else if (project.android.hasProperty('libraryVariants')) {
                    variants = project.android.libraryVariants
                } else {
                    throw new IllegalStateException('Android project must have applicationVariants or libraryVariants!')
                }

                JsonSchemaExtension config = project.extensions.getByType(JsonSchemaExtension)

                if (System.getProperty('java.specification.version').toFloat() >= 9) {
                    config.includeGeneratedAnnotation = false  // Temporary fixes #71 and upstream issue #1212 (used Generated annotation is not compatible with AGP 7+)
                }

                ConfigurableFileCollection sourceFiles

                if (config.source.isEmpty()) {
                    def sets
                    if (project.android.hasProperty('sourceSets')) {
                        sets = project.android.sourceSets;
                    } else {
                        sets = project.android.sourceSetsContainer;
                    }

                    sets.all { sourceSet ->
                        if(sourceSet.name.startsWith("main")){
                            def path = Paths.get(sourceSet.resources.source[0], 'json')
                            config.source.from(path)
                        }
                    }
                }

                variants.all { variant ->
                    GenerateFromJsonSchemaTask task = createJS2DTask(
                            project,
                            "${TASK_NAME}For${variant.name.capitalize()}",
                            config.source,
                            "$BASE_FOLDER/${variant.flavorName}/${variant.buildType.name}/"
                    )
                    variant.registerJavaGeneratingTask(task, task.targetDirectory.get().asFile)
                }
            } else {
                for (Plugin<?> plugin : project.plugins) {
                    project.logger.error(plugin.class.name)
                }
                throw new GradleException('generateJsonSchema: Java or Android plugin required')
            }
        }
    }

    private static GenerateFromJsonSchemaTask createJS2DTask(Project project, String taskName, ConfigurableFileCollection sourceFiles, String targetDirectory) {
        GenerateFromJsonSchemaTask task = (GenerateFromJsonSchemaTask) project.task(
                [
                        type       : GenerateFromJsonSchemaTask,
                        description: 'Generates Java classes from a json schema',
                        group      : 'Build'
                ],
                taskName
        )
        task.sourceFiles.setFrom(sourceFiles)
        task.targetDirectory.set(
                project.layout.buildDirectory.dir(targetDirectory)
        )
        task.inputs.files({ task.sourceFiles.filter({ it.exists() }) })
                .skipWhenEmpty()
        task.outputs.dir(task.targetDirectory)
        return task
    }
}
