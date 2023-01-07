plugins {
    `java-library`
    id ("org.jsonschema2dataclass")
}

repositories {
    mavenCentral()
}

dependencies {
    jsonschema2dataclassPlugins(project(":java:classpath:schema"))

    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.1")
}

jsonSchema2Pojo {
    executions {
        create("main") {
            io.source.setFrom(files("src/main/resources/schema/foo.json"))
            klass.annotationStyle.set("jackson2")
            klass.targetPackage.set("org.test")
            klass.nameUseTitle.set(true)
        }
    }
}

