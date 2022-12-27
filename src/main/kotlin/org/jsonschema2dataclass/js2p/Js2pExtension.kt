package org.jsonschema2dataclass.js2p

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

abstract class Js2pExtension{

    @get: Optional
    abstract val executions: NamedDomainObjectContainer<Js2pConfiguration>

    @get: PathSensitive(PathSensitivity.RELATIVE)
    abstract val targetDirectoryPrefix: DirectoryProperty
}
