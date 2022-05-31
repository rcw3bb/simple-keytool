package xyz.ronella.gradle.plugin.simple.keytool.task

import xyz.ronella.gradle.plugin.simple.keytool.args.IAliasArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IAliasRequiredArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IFileArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IKeyPassArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IKeyStoreArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IStorePassArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IStoreTypeArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IVerboseArg

/**
 * The task for importing a cacert to keystore.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
abstract class KSImportTask extends KeytoolTask
        implements IAliasRequiredArg, IFileArg, IVerboseArg, IStorePassArg, IKeyPassArg, IStoreTypeArg,
                IKeyStoreArg {

    /**
     * Creates an instance of KSImportTask.
     */
    KSImportTask() {
        super()
        description = 'Convenience task to import a certificate to a keystore.'
        internalCommand.convention('-importcert')
        internalArgs.set(['-noprompt'])
        isAdminMode.convention(true)
    }
}