package xyz.ronella.gradle.plugin.simple.keytool.task

import xyz.ronella.gradle.plugin.simple.keytool.args.*

/**
 * The task for listing the content of the cacerts.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
abstract class CACertsListTask extends KeytoolTask
        implements IAliasArg, IStorePassArg, IVerboseArg {

    /**
     * Creates an instance of CACertsListTask.
     */
    CACertsListTask() {
        super()
        description = 'Convenience task to display cacerts content.'
        internalCommand.convention('-list')
        internalArgs.set(['-cacerts'])
    }

}
