allprojects {
    tasks.withType<JavaCompile>().configureEach {
        this.sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        this.targetCompatibility = JavaVersion.VERSION_1_8.toString()
    }
}
