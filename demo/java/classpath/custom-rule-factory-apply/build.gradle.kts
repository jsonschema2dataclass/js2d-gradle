plugins {
    `java-library`
    id("org.jsonschema2dataclass")
}

dependencies {
    jsonschema2dataclassPlugins(project(":classpath:custom-rule-factory"))

    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
}

jsonSchema2Pojo {
    executions {
        create("main") {
            klass.customRuleFactoryClass.set("org.jsonschema2dataclass.example.CustomRuleFactory")
            klass.annotationStyle.set("jackson2")
            klass.targetPackage.set("org.test")
            klass.nameUseTitle.set(true)
        }
    }
}
