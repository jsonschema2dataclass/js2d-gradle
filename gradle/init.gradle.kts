fun resolvePluginPath(rootPath: java.nio.file.Path): java.nio.file.Path? {
    if (rootPath.resolve("src").toFile().exists()) {
        return null
    }
    if (rootPath.resolve("demo").toFile().exists()) {
        return null
    }
    var path = rootPath
    for (n in 1..4) {
        val settingsFile = path.resolve("demo")
        if (settingsFile.toFile().exists()) {
            return path.toAbsolutePath()
            break
        } else {
            path = path.resolve("..")
            println("new path: ${path.toAbsolutePath()}")
        }
    }
    return null
}

gradle.settingsEvaluated {
    val property = "org.jsonschema2dataclass.local"
    if (extra.has(property) && extra[property].toString().toBoolean()) {
        println("root dir: ${getRootDir()}")
        val path = resolvePluginPath(getRootDir().toPath().toAbsolutePath())
        if (path != null) {
            println("Include build:\"$path\"")
            includeBuild(path)
        }
    }
}
