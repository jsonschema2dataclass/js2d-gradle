plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":plugin-gradle:processors:common"))
}
