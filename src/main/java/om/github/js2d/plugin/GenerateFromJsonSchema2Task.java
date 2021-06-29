package om.github.js2d.plugin;

import lombok.Getter;
import lombok.Setter;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.jsonschema2pojo.Jsonschema2Pojo;

import java.io.IOException;

import com.github.js2d.plugin.Js2dConfig;

@Getter
@Setter
class GenerateFromJsonSchema2Task extends DefaultTask {
    /**
     * Directory to save generated files to.
     */
    @OutputDirectory
    public final DirectoryProperty targetDirectory = getProject().getObjects().directoryProperty();
    /**
     * Directory to save generated files to.
     */
    @InputFiles
    public final ConfigurableFileCollection sourceFiles = getProject().getObjects().fileCollection();

    @Input
    public String execution;

    @TaskAction
    public void generate() throws IOException {
        JsonSchemaExtension extension = (JsonSchemaExtension) getProject().getExtensions().getByType(JsonSchemaExtension.class);

        Js2dConfig js2pConfig = new Js2dConfig(extension, extension.getExecutions().getByName(execution), targetDirectory, sourceFiles);
        getLogger().info("Using this configuration:\n{}", js2pConfig);
        Jsonschema2Pojo.generate(js2pConfig, new GradleRuleLogger(getLogger()));
    }
}