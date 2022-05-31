package xyz.ronella.gradle.plugin.simple.keytool.task

import xyz.ronella.gradle.plugin.simple.keytool.args.*

/**
 * The task for importing certificates from a directory.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
abstract class CACertsImportDirTask extends KeytoolTask
        implements IDirArg, IVerboseArg, IStorePassArg, IStoreTypeArg {

    /**
     * Creates an instance of CACertsListTask.
     */
    CACertsImportDirTask() {
        super()
        description = 'Convenience task to import certificates from a directory to cacerts.'
        internalCommand.convention('-importcert')
        internalArgs.set(['-cacerts', '-noprompt'])
        isAdminMode.convention(true)
        isScriptMode.convention(true)
    }

}
