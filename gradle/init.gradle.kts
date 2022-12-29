allprojects {
    buildscript {
        repositories {
            maven {
                url = uri("https://plugins.gradle.org/m2/")
            }
        }
        dependencies {
            val a = fileTree("../../../build/libs") {
                include("**/*.jar")
                exclude("**/*-sources.jar")
                exclude("**/*-javadoc.jar")
            }
            val result_files = a.getFiles()
            val result_file = result_files.firstOrNull()
            if (result_files.size > 1) {
                project.logger.warn("Development files found: ${result_files}.")
            }
            if (result_files.size == 0) {
                project.logger.warn("Root project never built.")
            }
            if (result_files.size == 1) {  // skip this
                classpath(files(result_file))
                classpath("org.jsonschema2pojo:jsonschema2pojo-core:1.1.2")
            }
        }
    }
}
