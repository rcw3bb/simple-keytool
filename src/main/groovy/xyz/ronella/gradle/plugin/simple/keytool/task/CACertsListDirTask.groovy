package xyz.ronella.gradle.plugin.simple.keytool.task

import xyz.ronella.gradle.plugin.simple.keytool.args.*

/**
 * The task for listing the content of the cacerts based on a directory.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
abstract class CACertsListDirTask extends KeytoolTask
        implements IDirArg, IStorePassArg, IVerboseArg, IStoreTypeArg {

    /**
     * Creates an instance of CACertsListTask.
     */
    CACertsListDirTask() {
        super()
        description = 'Convenience task to display cacerts content based on a directory.'
        internalCommand.convention('-list')
        internalArgs.set(['-cacerts'])
        isScriptMode.convention(true)
    }

}
