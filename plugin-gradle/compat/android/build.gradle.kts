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
    // for Android build tools version :)
    compileOnly("com.android.tools:common:30.4.0") {
        exclude(group = "org.jetbrains.kotlin")
    }

    implementation(project(":plugin-gradle:commons:kotlin-compat"))

    implementation(project(":plugin-gradle:processors:common"))
    implementation(project(":plugin-gradle:compat:agp34"))
    if (JavaVersion.current() > JavaVersion.VERSION_1_8) {
        implementation(project(":plugin-gradle:compat:agp7"))
    }
}
