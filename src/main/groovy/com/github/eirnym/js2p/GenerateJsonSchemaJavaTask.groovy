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

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.jsonschema2pojo.GenerationConfig
import org.jsonschema2pojo.Jsonschema2Pojo

/**
 * A task that performs code generation.
 *
 * @author Ben Manes (ben.manes@gmail.com)
 */
class GenerateJsonSchemaJavaTask extends DefaultTask {
    @Internal
    GenerationConfig configuration

    GenerateJsonSchemaJavaTask() {
        description = 'Generates Java classes from a json schema.'
        group = 'Build'
        def extension = project.getExtensions().getByType(JsonSchemaExtension)
        outputs.dir extension.targetDirectoryProperty

        dependsOn project.tasks.named('processResources')
        project.tasks.named('compileJava').configure {
            it.dependsOn(this)
        }
        project.plugins.withId('java', {
            project.sourceSets.main.java.srcDirs extension.targetDirectoryProperty
        })
        inputs.files({ extension.sourceFiles.filter({ it.exists() }) })
                .skipWhenEmpty()
        project.afterEvaluate {
            configuration = project.jsonSchema2Pojo

            if (project.plugins.hasPlugin('java')) {
                configureJava()
            } else {
                throw new GradleException('generateJsonSchema: Java plugin is required')
            }

            inputs.property("configuration", configuration.toString())
        }
    }

    def configureJava() {
        if (!configuration.source.hasNext()) {
            configuration.sourceFiles.setFrom project.files("${project.sourceSets.main.output.resourcesDir}/json")
            configuration.sourceFiles.each { it.mkdir() }
        }
    }

    @TaskAction
    def generate() {
        if (Boolean.TRUE == configuration.properties.get("useCommonsLang3")) {
            logger.warn 'useCommonsLang3 is deprecated. Please remove it from your config.'
        }

        logger.info 'Using this configuration:\n{}', configuration

        Jsonschema2Pojo.generate(configuration, new GradleRuleLogger(logger))
    }
}
