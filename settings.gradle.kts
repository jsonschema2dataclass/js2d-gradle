rootProject.name = "JsonSchema2DataClass"

include(":plugin-gradle:plugin")

include(":plugin-gradle:compat:android")
include(":plugin-gradle:compat:agp34")

// If is for testing on Java 1.8
if (JavaVersion.current() > JavaVersion.VERSION_1_8) {
    include(":plugin-gradle:compat:agp7")
}
include(":plugin-gradle:processors:common")
include(":plugin-gradle:processors:jsonschema2pojo")
include(":plugin-gradle:compat:java")

include(":plugin-gradle:commons:kotlin-compat")
include(":plugin-gradle:commons:test-common")

includeBuild("internal")
