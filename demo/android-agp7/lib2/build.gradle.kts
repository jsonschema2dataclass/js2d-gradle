plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jsonschema2dataclass")
}

android {
    namespace = "org.jsonschema2dataclass.lib2"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.current()
        targetCompatibility = JavaVersion.current()
    }
    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(JavaVersion.current().majorVersion.toInt()))
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.code.gson:gson:2.11.0")
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

jsonSchema2Pojo {
    executions {
        create("main") {
            io {
                source.setFrom(files("${project.rootDir}/schema"))
                sourceType.set("jsonschema")
                outputEncoding.set("UTF-8")
                delimitersPropertyWord.set("")
            }

            klass {
                targetPackage.set("com.oosocial.clarityn.rest.clarityn.model")
                annotationStyle.set("gson")
                customAnnotatorClass.set("org.jsonschema2pojo.NoopAnnotator")
            }

            methods {
                builders.set(false)
                hashcodeAndEquals.set(true)
                toStringMethod.set(true)
            }

            fields {
                usePrimitives.set(false)
                integerUseLong.set(false)
                floatUseDouble.set(true)
                initializeCollections.set(true)
            }

            dateTime.jodaDate.set(false)
        }
    }
}
