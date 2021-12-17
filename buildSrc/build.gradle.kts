plugins {
    `kotlin-dsl`
}

if (JavaVersion.current() > JavaVersion.VERSION_11) {
    tasks.withType<JavaCompile> {
        options.release.set(8)
    }
} else {
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

repositories {
    mavenCentral()
}
