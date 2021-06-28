package om.github.js2d.plugin;

import org.gradle.api.model.ObjectFactory;

import java.nio.file.Path;

@FunctionalInterface
public interface SetupConfigExecutions {
    void apply(ObjectFactory objectFactory, JsonSchemaExtension config, Path path, boolean excludeGenerated);
}
