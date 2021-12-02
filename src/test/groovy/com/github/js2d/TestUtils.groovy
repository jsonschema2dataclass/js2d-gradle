package com.github.js2d

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

import java.nio.file.Path

import static com.github.js2d.JsonSchemaPlugin.TASK_NAME
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertTrue

class TestUtils {
	public static final String COLON_TASK_NAME = ':' + TASK_NAME
	/**
	 * Supported gradle releases
	 */
	public static final List<String> GRADLE_RELEASES = [
		'7.0.2',
		'7.1',
		'7.2',
		'7.3', // 7.x
		//'6.9', '6.8.3', '6.8', '6.7.1', '6.6.1', '6.5.1', '6.4.1', '6.3', '6.2.2', '6.2.1', '6.1.1', '6.0.1',  // 6.x
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
		int major = parts[0].toInteger()
		int minor = parts[1].toInteger()

		switch ((int) javaVersion) {
			case 14: return major >= 7 || (major == 6 && minor >= 3)
			case 15: return major >= 7 || (major == 6 && minor >= 6)
			case 16: return major >= 7
			default: return false // no official information on Gradle compatibility with further versions of Java
		}
	}
}
