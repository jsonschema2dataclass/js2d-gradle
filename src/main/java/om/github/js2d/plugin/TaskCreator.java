package om.github.js2d.plugin;

import org.gradle.api.Project;
import org.gradle.api.Task;

import java.util.function.Consumer;

@FunctionalInterface
public interface TaskCreator {
    Task apply(
            Project project,
            JsonSchemaExtension extension,
            String taskNameSuffix,
            String targetDirectorySuffix,
            Consumer<GenerateFromJsonSchema2Task> postConfigure
    );
}
