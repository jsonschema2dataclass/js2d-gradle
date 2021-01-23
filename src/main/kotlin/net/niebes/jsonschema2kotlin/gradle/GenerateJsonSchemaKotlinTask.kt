package net.niebes.jsonschema2kotlin.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import wu.seal.jsontokotlin.DefaultValueStrategy
import wu.seal.jsontokotlin.PropertyTypeStrategy
import wu.seal.jsontokotlin.TargetJsonConverter
import wu.seal.jsontokotlin.library.JsonToKotlinBuilder

open class GenerateJsonSchemaKotlinTask : DefaultTask() {

    @Internal
    lateinit var configuration: JsonSchemaExtension

    init {
        description = "Generates Java classes from a json schema."
        group = "Build"
        project.afterEvaluate {
            configuration = project.extensions.getByType(JsonSchemaExtension::class.java)

            if (project.plugins.hasPlugin("org.jetbrains.kotlin.jvm")) {
                // TODO: add source set
            } else {
                throw GradleException("generateJsonSchema: Kotlin plugin is required")
            }
            outputs.dir(configuration.targetDirectory ?: project.file("${project.buildDir}/generated-sources/js2p"))

            inputs.property("configuration", configuration.toString())
            inputs.files(project.files(configuration.sourceFiles))
        }
    }


    @TaskAction
    fun generate() {
        print("indent: " + configuration.indent)
        val json1 = """{ "programmers": [
                { "isFirstName": "Brett", "lastName":"McLaughlin", "email": "aaaa" },
                { "firstName": "Jason", "lastName":"Hunter", "email": "bbbb" },
                { "firstName": "Elliotte", "lastName":"Harold", "email": "cccc" }
                ],
                "authors": [
                { "firstName": null, "lastName": "Asimov", "genre": "science fiction" },
                { "firstName": "Tad", "lastName": "Williams", "genre": "fantasy" },
                { "firstName": "Frank", "lastName": "Peretti", "genre": "christian fiction" }
                ],
                "musicians": [
                { "firstName": "Eric", "lastName": "Clapton", "instrument": "guitar" },
                { "firstName": "Sergei", "lastName": "Rachmaninoff", "instrument": "piano" }
                ] } """

        val output = JsonToKotlinBuilder()
            .setPackageName("com.my.package.name")
            .enableVarProperties(false) // optional, default : false
            .setPropertyTypeStrategy(PropertyTypeStrategy.NotNullable) // optional, default :  PropertyTypeStrategy.NotNullable
            .setDefaultValueStrategy(DefaultValueStrategy.AvoidNull) // optional, default : DefaultValueStrategy.AvoidNull
            .setAnnotationLib(TargetJsonConverter.None) // optional, default: TargetJsonConverter.None
            .enableComments(true) // optional, default : false
            .enableOrderByAlphabetic(false) // optional : default : false
            .enableInnerClassModel(true) // optional, default : false
            .enableMapType(true)// optional, default : false
            .enableCreateAnnotationOnlyWhenNeeded(false) // optional, default : false
            .setIndent(4)// optional, default : 4
            //.setParentClassTemplate("android.os.Parcelable") // optional, default : ""
            .enableKeepAnnotationOnClass(false) // optional, default : false
            .enableKeepAnnotationOnClassAndroidX(false) // optional, default : false
            .enableAnnotationAndPropertyInSameLine(false) // optional, default : false
            .enableParcelableSupport(false) // optional, default : false
            //.setPropertyPrefix("MyPrefix") // optional, default : ""
            //.setPropertySuffix("MySuffix") // optional, default : ""
            //.setClassSuffix("MyClassSuffix")// optional, default : ""
            .enableForceInitDefaultValueWithOriginJsonValue(false) // optional, default : false
            .enableForcePrimitiveTypeNonNullable(false) // optional, default : false
            .build(json1, "ProgrammersModel") // finally, get KotlinClassCode string

        println(output)
    }
}

