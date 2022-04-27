package xyz.ronella.gradle.plugin.simple.keytool.task

import xyz.ronella.gradle.plugin.simple.keytool.args.*

/**
 * The task for deleting certificates based on the content of a directory.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
abstract class CACertsDeleteDirTask extends KeytoolTask
    implements IDirArg, IVerboseArg, IStorePassArg, IKeyPassArg, IStoreTypeArg {

    /**
     * Creates an instance of CACertsListTask.
     */
    CACertsDeleteDirTask() {
        super()
        description = 'Convenience task to delete the certificates based on the content of a directory from cacerts.'
        internalCommand.convention('-delete')
        internalArgs.set(['-cacerts'])
        isAdminMode.convention(true)
        isScriptMode.convention(true)
    }
}