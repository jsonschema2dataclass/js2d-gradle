plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

group = "org.jsonschema2dataclass"
version = gitVersion(project)

repositories {
    google().content {
        includeGroup("com.android")
        includeGroup("android.arch.lifecycle")
        includeGroup("android.arch.core")
        includeGroupByRegex("com\\.android\\..*")
        includeGroupByRegex("com\\.google\\..*")
        includeGroupByRegex("androidx\\..*")
    }
    mavenCentral()
}

val provided: Configuration by configurations.creating
sourceSets {
    main {
        this.compileClasspath += provided
    }
}

dependencies {
    compileOnly(project(":plugin-gradle:processors:common"))

    compileOnly("com.android.tools.build:gradle:4.2.1") {
        exclude(group = "org.jetbrains.kotlin")
    }
}
