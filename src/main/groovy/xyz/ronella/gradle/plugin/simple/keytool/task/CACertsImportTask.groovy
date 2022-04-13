package xyz.ronella.gradle.plugin.simple.keytool.task

import xyz.ronella.gradle.plugin.simple.keytool.args.IAliasArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IFileArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IKeyPassArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IStorePassArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IStoreTypeArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IVerboseArg

/**
 * The task for importing cacert
 *
 * @author Ron Webb
 * @since 1.0.0
 */
abstract class CACertsImportTask extends KeytoolTask
        implements IAliasArg, IFileArg, IVerboseArg, IStorePassArg, IKeyPassArg, IStoreTypeArg {

    /**
     * Creates an instance of CACertsListTask.
     */
    CACertsImportTask() {
        super()
        description = 'Convenience task to import certificate to cacert.'
        internalCommand.convention('-importcert')
        internalArgs.set(['-cacerts'])
        isAdminMode.convention(true)
    }
}
