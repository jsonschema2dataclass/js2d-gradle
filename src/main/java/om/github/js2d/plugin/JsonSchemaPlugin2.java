package om.github.js2d.plugin;

import com.github.js2d.plugin.PluginRegisterAndroid;
import org.gradle.api.*;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.util.GradleVersion;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class JsonSchemaPlugin2 implements Plugin<Project>, PluginDefinitions {

    @Override
    public void apply(Project project) {
        verifyGradleVersion();

        project.getExtensions().create("jsonSchema2Pojo", JsonSchemaExtension.class, new Object[]{project.getObjects()});
        JsonSchemaExtension extension = (JsonSchemaExtension) project.getExtensions().getByType(JsonSchemaExtension.class);
        extension.getTargetDirectoryPrefix().convention(project.getLayout().getBuildDirectory().dir(TARGET_FOLDER_BASE));
        project.afterEvaluate(prj -> {
            if (project.getPlugins().hasPlugin("java")) {
                applyJava(extension, project);
            } else if (project.getPlugins().hasPlugin("com.android.application") ||
                    project.getPlugins().hasPlugin("com.android.library")) {
                PluginRegisterAndroid.applyAndroid(extension, project, JsonSchemaPlugin2::setupConfigExecutions, JsonSchemaPlugin2::createJS2DTask);
            } else {
                throw new ProjectConfigurationException("${TASK_NAME}: Java or Android plugin required", (Throwable) null);
            }
        });
    }

    private static void applyJava(JsonSchemaExtension extension, Project project) {
        project.getPlugins().withId("java", plugin -> {
            JavaPluginExtension javaPluginExtension = (JavaPluginExtension) project.getExtensions().findByType(JavaPluginExtension.class);

            setupConfigExecutions(
                    project.getObjects(),
                    extension,
                    getJavaJsonPath(javaPluginExtension),
                    false
            );
            Task js2pTask = createJS2DTask(project, extension, "", "",
                    execution -> {
                        execution.dependsOn("processResources");
                        javaPluginExtension.getSourceSets().getByName("main").getAllJava()
                                .srcDir(execution.getTargetDirectory());
                    });
            project.getTasks().named("compileJava").configure(task -> {
                task.dependsOn(js2pTask);
            });
        });
    }

    private static Path getJavaJsonPath(JavaPluginExtension javaPluginExtension) {

        return javaPluginExtension
                .getSourceSets()
                .getByName("main")
                .getOutput()
                .getResourcesDir()
                .toPath()
                .resolve("json");
    }

    private static Map<String, Object> taskParams(String description, Class<? extends Task> taskClass) {
        Map<String, Object> params = new HashMap<>();
        params.put("description", description);
        params.put("group", "Build");
        if (taskClass != null) {
            params.put("type", taskClass);
        }
        return params;
    }

    private static Task createJS2DTask(
            Project project,
            JsonSchemaExtension extension,
            String taskNameSuffix,
            String targetDirectorySuffix,
            Consumer<GenerateFromJsonSchema2Task> postConfigure
    ) {

        Task js2dTask = project.task(
                taskParams("Generates Java classes from a json schema using JsonSchema2Pojo", null),
                "${TASK_NAME}${taskNameSuffix}"
        );

        extension.getExecutions().forEach(
                execution -> {
                    GenerateFromJsonSchema2Task task = createJS2DTaskExecution(
                            project,
                            extension,
                            taskNameSuffix,
                            execution.getName(),
                            execution.getSource(),
                            extension.getTargetDirectoryPrefix(),
                            targetDirectorySuffix
                    );
                    postConfigure.accept(task);
                    js2dTask.dependsOn(task);
                });
        return js2dTask;
    }

    private static GenerateFromJsonSchema2Task createJS2DTaskExecution(
            Project project,
            JsonSchemaExtension extension,
            String taskNameSuffix,
            String executionName,
            ConfigurableFileCollection source,
            DirectoryProperty targetDirectoryPrefix,
            String targetDirectorySuffix
    ) {

        GenerateFromJsonSchema2Task task = (GenerateFromJsonSchema2Task) project.task(
                taskParams("Generates Java classes from a json schema using JsonSchema2Pojo. Execution ${executionId}",
                        GenerateFromJsonSchema2Task.class),
               String.format("%s,%s%s", TASK_NAME, executionName, taskNameSuffix)
        );
        task.setExecution(executionName);
        task.getSourceFiles().setFrom(source);
        task.getTargetDirectory().set(
                targetDirectoryPrefix.dir(String.format("%s%s", executionName, targetDirectorySuffix))
        );
        task.getInputs().files(task.getSourceFiles().filter(File::exists)).skipWhenEmpty();
        task.getOutputs().dir(task.getTargetDirectory());
        return task;
    }

    private static void setupConfigExecutions(
            ObjectFactory objectFactory, JsonSchemaExtension config, Path path, boolean excludeGenerated) {
        if (config.source.isEmpty()) {
            config.source.from(path);
        }
        if (config.getExecutions().isEmpty()) {
            config.getExecutions().add(new JsonSchema2dPluginConfiguration(DEFAULT_EXECUTION_NAME, objectFactory));
        }

        for (JsonSchema2dPluginConfiguration execution : config.getExecutions()) {
            if (execution.source.isEmpty()) {
                execution.source.from(config.source);
            }
            if (excludeGenerated) {
                execution.includeGeneratedAnnotation = false;
                // Temporary fixes #71 and upstream issue #1212 (used Generated annotation is not compatible with AGP 7+)
            }
        }
    }

    private static void verifyGradleVersion() {
        if (GradleVersion.current().compareTo(GradleVersion.version(MINIMUM_GRADLE_VERSION)) < 0) {
            throw new GradleException("Plugin ${PLUGIN_ID} requires at least Gradle $MINIMUM_GRADLE_VERSION, but you are using ${GradleVersion.current().version}");
        }
    }
}
