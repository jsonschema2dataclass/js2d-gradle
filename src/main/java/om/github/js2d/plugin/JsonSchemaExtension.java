package om.github.js2d.plugin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

import static om.github.js2d.plugin.PluginDefinitions.DEFAULT_EXECUTION_NAME;

@Getter
@Setter
@ToString
public class JsonSchemaExtension extends JsonSchema2dPluginConfiguration {
    private final NamedDomainObjectContainer<JsonSchema2dPluginConfiguration> executions;
    private final DirectoryProperty targetDirectoryPrefix;

    @Inject
    public JsonSchemaExtension(ObjectFactory objectFactory) {
        super(DEFAULT_EXECUTION_NAME, objectFactory);
        targetDirectoryPrefix = objectFactory.directoryProperty();
        executions = objectFactory.domainObjectContainer(JsonSchema2dPluginConfiguration.class);
    }
}