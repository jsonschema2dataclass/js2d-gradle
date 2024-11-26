plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jsonschema2dataclass")
}

android {
    namespace = "org.jsonschema2dataclass.lib"

    compileSdkVersion = "android-35"

    defaultConfig {
        minSdk = 24
        targetSdk = 35
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            consumerProguardFiles("proguard-rules.pro")
        }
    }

    packagingOptions {
        resources.excludes.add("META-INF/LICENSE.txt")
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
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Required for @Generated annotation
    implementation("org.glassfish:javax.annotation:10.0-b28")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.squareup.moshi:moshi:1.15.1")
    // Required if generating JSR-303 annotations
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.1")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
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
                annotateJsr303.set(true)
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
