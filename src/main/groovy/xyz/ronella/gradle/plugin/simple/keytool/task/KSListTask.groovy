package xyz.ronella.gradle.plugin.simple.keytool.task

import xyz.ronella.gradle.plugin.simple.keytool.args.IAliasArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IKeyStoreArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IStorePassArg
import xyz.ronella.gradle.plugin.simple.keytool.args.IVerboseArg

/**
 * The task for listing the content of the keystore.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
abstract class KSListTask extends KeytoolTask
        implements IAliasArg, IStorePassArg, IVerboseArg, IKeyStoreArg {

    /**
     * Creates an instance of KSListTask.
     */
    KSListTask() {
        super()
        description = 'Convenience task to display keystore content.'
        internalCommand.convention('-list')
    }
}
