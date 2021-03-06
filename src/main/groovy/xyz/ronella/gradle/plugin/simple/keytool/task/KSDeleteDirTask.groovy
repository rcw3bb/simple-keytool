package xyz.ronella.gradle.plugin.simple.keytool.task

import xyz.ronella.gradle.plugin.simple.keytool.args.*

/**
 * The task for deleting certificates based on the content of a directory.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
abstract class KSDeleteDirTask extends KeytoolTask
        implements IDirArg, IVerboseArg, IStorePassArg, IStoreTypeArg, IKeyStoreArg {

    /**
     * Creates an instance of KSDeleteDirTask.
     */
    KSDeleteDirTask() {
        super()
        description = 'Convenience task to delete certificates based on a directory from a keystore.'
        internalCommand.convention('-delete')
        isAdminMode.convention(true)
        isScriptMode.convention(true)
        fileArgs.convention((Map<String, List<String>>) null)
    }
}
