package org.jsonschema2dataclass.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JType;
import java.util.Base64;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.FormatRule;
import org.jsonschema2pojo.rules.RuleFactory;

public class CustomFormatRule extends FormatRule {

  protected CustomFormatRule(RuleFactory ruleFactory) {
    super(ruleFactory);
  }

  @Override
  public JType apply(
      String nodeName, JsonNode node, JsonNode parent, JType baseType, Schema schema) {
    if ("base64".equals(node.asText())) {
      return baseType.owner()._ref(Base64.class);
    } else {
      return super.apply(nodeName, node, parent, baseType, schema);
    }
  }
}
