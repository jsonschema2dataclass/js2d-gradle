package com.github.eirnym.js2p

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

import java.nio.file.Path

import static com.github.eirnym.js2p.JsonSchemaPlugin.TASK_NAME
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertTrue

class TestUtils {
    public static final String COLON_TASK_NAME = ':' + TASK_NAME
    /**
     * Supported gradle releases
     */
    public static final List<String> GRADLE_RELEASES = [
            '6.8.3', '6.8', '6.7.1', '6.6.1', '6.5.1', '6.4.1', '6.3', '6.2.2', '6.2.1', '6.1.1', '6.0.1',  // 6.x
    ]

    static void assertExists(File file) {
        assertNotNull(file)
        assertTrue(file.exists())
    }

    static void addressJavaExists(Path testProjectDir, String targetDirectoryPrefix, String executionName, String subfolder) {
        def js2pDir = testProjectDir.resolve(targetDirectoryPrefix).resolve(executionName).resolve(subfolder)
        assertExists(js2pDir.toFile())
        assertExists(js2pDir.resolve("Address.java").toFile())
    }

    static BuildResult executeRunner(
            String gradleVersion, Path testProjectDir, String task = COLON_TASK_NAME, boolean shouldFail = false) {
        def arguments = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.toFile())
                .withArguments(task, '-S', '--info')

        if (gradleVersion) {
            arguments.withGradleVersion(gradleVersion)
        }
        if (shouldFail) {
            return arguments.buildAndFail()
        }
        return arguments.build()
    }

    static boolean gradleSupported(String gradleVersion, String javaSpecificationVersion) {
        def javaVersion = javaSpecificationVersion.toFloat()
        if (javaVersion <= 13) {  // this includes java '1.8' :)
            return true
        }

        def parts = gradleVersion.split(/\./)

        switch ((int) javaVersion) {
            case 14: return parts[1].toInteger() >= 3
            case 15: return parts[1].toInteger() >= 6
            default: return false // no official information on Gradle compatibility with further versions of Java
        }
    }
}
