package org.jsonschema2dataclass.internal.js2p

import org.gradle.api.tasks.CacheableTask
import org.gradle.workers.WorkQueue
import org.gradle.workers.WorkerExecutor
import org.jsonschema2dataclass.ext.Js2pConfiguration
import org.jsonschema2dataclass.internal.task.Js2dGeneratorTaskBase
import javax.inject.Inject

@CacheableTask
internal abstract class Js2pGenerationTask @Inject constructor(
    workerExecutor: WorkerExecutor,
) : Js2dGeneratorTaskBase<Js2pConfiguration>(workerExecutor) {

    override fun submit(workQueue: WorkQueue) {
        val js2pConfig = Js2pWorkerConfig.fromConfig(uuid, targetDirectory.asFile.get(), configuration)
        workQueue.submit(Js2pWorker::class.java) {
            config = js2pConfig
        }
    }
}
