/*
 * Copyright © 2010-2014 Nokia
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.eirnym.js2p;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.io.FileMatchers.anExistingDirectory;
import static org.hamcrest.io.FileMatchers.anExistingFile;

public class JsonSchemaPluginSpec {
    @Test
    void java() throws URISyntaxException {
        GradleConnector connector = GradleConnector.newConnector();
        connector.useDistribution(new URI("https://services.gradle.org/distributions/gradle-5.6-bin.zip"));
        connector.forProjectDirectory(new File("example/java"));
        try (ProjectConnection connection = connector.connect()) {
            BuildLauncher launcher = connection.newBuild();
            launcher.forTasks("clean", "build");
            launcher.run();
        }

        File js2p = new File("example/java", "build/generated-sources/js2p");
        assertThat(js2p, is(anExistingDirectory()));
        File packageDir = new File(js2p, "example");
        assertThat(packageDir, is(anExistingDirectory()));
        assertThat(new File(packageDir, "Address.java"), is(anExistingFile()));
        assertThat(new File(packageDir, "ExternalDependencies.java"), is(anExistingFile()));
    }
}
