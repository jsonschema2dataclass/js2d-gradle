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

import groovy.transform.ToString
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

import javax.inject.Inject

import static com.github.eirnym.js2p.JsonSchemaPlugin.TARGET_FOLDER_BASE

@ToString
class JsonSchemaExtension extends JsonSchema2PojoPluginConfiguration {
    final NamedDomainObjectContainer<JsonSchema2PojoPluginConfiguration> executions
    final Property<String> targetDirectoryPrefix

    @Inject
    JsonSchemaExtension(ObjectFactory objectFactory) {
        super("main", objectFactory)
        targetDirectoryPrefix = objectFactory.property(String.class).convention(TARGET_FOLDER_BASE)
        executions = objectFactory.domainObjectContainer(JsonSchema2PojoPluginConfiguration)
    }
}
