package org.jsonschema2dataclass.example;

import com.sun.codemodel.JType;
import org.jsonschema2pojo.Annotator;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;

public class CustomRuleFactory extends RuleFactory {
  public CustomRuleFactory(
      GenerationConfig generationConfig, Annotator annotator, SchemaStore schemaStore) {
    super(generationConfig, annotator, schemaStore);
  }

  public CustomRuleFactory() {}

  public Rule<JType, JType> getFormatRule() {
    return new CustomFormatRule(this);
  }
}
