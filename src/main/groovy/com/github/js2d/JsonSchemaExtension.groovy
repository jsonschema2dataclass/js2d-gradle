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

import groovy.transform.ToString
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory

import javax.inject.Inject

import static JsonSchemaPlugin.DEFAULT_EXECUTION_NAME

@ToString
class JsonSchemaExtension extends JsonSchema2dPluginConfiguration {
    final NamedDomainObjectContainer<JsonSchema2dPluginConfiguration> executions
    final DirectoryProperty targetDirectoryPrefix

    @Inject
    JsonSchemaExtension(ObjectFactory objectFactory) {
        super(DEFAULT_EXECUTION_NAME, objectFactory)
        targetDirectoryPrefix = objectFactory.directoryProperty()
        executions = objectFactory.domainObjectContainer(JsonSchema2dPluginConfiguration)
    }
}
