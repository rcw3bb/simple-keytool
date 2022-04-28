package xyz.ronella.gradle.plugin.simple.keytool.task

import xyz.ronella.gradle.plugin.simple.keytool.args.*

/**
 * The task for listing the content of a keystore based on a directory.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
abstract class KSListDirTask extends KeytoolTask
        implements IDirArg, IStorePassArg, IVerboseArg, IStoreTypeArg, IKeyStoreArg {

    /**
     * Creates an instance of KSListDirTask.
     */
    KSListDirTask() {
        super()
        description = 'Convenience task to display keystore certificates based on a directory.'
        internalCommand.convention('-list')
        isScriptMode.convention(true)
    }

}
