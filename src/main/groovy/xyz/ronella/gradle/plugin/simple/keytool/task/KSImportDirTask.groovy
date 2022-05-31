package xyz.ronella.gradle.plugin.simple.keytool.task

import xyz.ronella.gradle.plugin.simple.keytool.args.*

/**
 * The task for importing certificates from a directory.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
abstract class KSImportDirTask extends KeytoolTask
        implements IDirArg, IVerboseArg, IStorePassArg, IStoreTypeArg, IKeyStoreArg {

    /**
     * Creates an instance of KSImportDirTask.
     */
    KSImportDirTask() {
        super()
        description = 'Convenience task to import certificates from a directory to a keystore.'
        internalCommand.convention('-importcert')
        internalArgs.set(['-noprompt'])
        isAdminMode.convention(true)
        isScriptMode.convention(true)
    }

}

