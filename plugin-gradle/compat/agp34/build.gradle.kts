plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

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

dependencies {
    compileOnly(project(":plugin-gradle:processors:common"))

    compileOnly("com.android.tools.build:gradle:4.2.2") {
        exclude(group = "org.jetbrains.kotlin")
    }
}
