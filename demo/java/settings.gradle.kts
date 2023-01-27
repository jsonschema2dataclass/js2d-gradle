rootProject.name = "Json Schema 2 Data Class Java demos"

// Groovy DSL & Groovy language
include(":groovy")
// Kotlin DSL & Kotlin language
include(":kotlin")
// Example, how to publish models and schemas
include(":model-publish")
// schema for schema-reference example
include(":classpath:schema")
// Use schemas from classpath
include(":classpath:schema-reference")
// Custom rule factory
include(":classpath:custom-rule-factory")
// How to apply custom rule factory
include(":classpath:custom-rule-factory-apply")
