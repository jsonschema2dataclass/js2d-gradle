plugins {
    `java-library`
    id("org.jsonschema2dataclass")
}

dependencies {
    jsonschema2dataclassPlugins(project(":classpath:schema"))

    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
}

jsonSchema2Pojo {
    executions {
        create("main") {
            io.source.setFrom(files("src/main/resources/schema/foo.json"))
            klass.annotationStyle.set("jackson2")
            klass.annotateGenerated.set(false)
            klass.targetPackage.set("org.test")
            klass.nameUseTitle.set(true)
        }
    }
}
