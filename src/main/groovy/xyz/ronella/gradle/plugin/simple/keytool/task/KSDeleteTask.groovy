package xyz.ronella.gradle.plugin.simple.keytool.task

import xyz.ronella.gradle.plugin.simple.keytool.args.IAliasRequiredArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IKeyPassArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IKeyStoreArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IStorePassArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IStoreTypeArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IVerboseArg

/**
 * The task for deleting a cert from keystore.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
abstract class KSDeleteTask extends KeytoolTask
        implements IAliasRequiredArg, IVerboseArg, IStorePassArg, IKeyPassArg, IStoreTypeArg,
                IKeyStoreArg {

    /**
     * Creates an instance of KSDeleteTask.
     */
    KSDeleteTask() {
        super()
        description = 'Convenience task to delete a certificate from keystore.'
        internalCommand.convention('-delete')
        isAdminMode.convention(true)
    }
}