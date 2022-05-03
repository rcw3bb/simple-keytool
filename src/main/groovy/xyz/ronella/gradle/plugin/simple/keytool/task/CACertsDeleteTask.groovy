package xyz.ronella.gradle.plugin.simple.keytool.task

import xyz.ronella.gradle.plugin.simple.keytool.args.*

/**
 * The task for deleting a cacert.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
abstract class CACertsDeleteTask extends KeytoolTask
        implements IAliasRequiredArg, IVerboseArg, IStorePassArg, IStoreTypeArg {

    /**
     * Creates an instance of CACertsListTask.
     */
    CACertsDeleteTask() {
        super()
        description = 'Convenience task to delete a certificate from cacerts.'
        internalCommand.convention('-delete')
        internalArgs.set(['-cacerts'])
        isAdminMode.convention(true)
    }
}
