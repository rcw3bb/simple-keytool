package xyz.ronella.gradle.plugin.simple.keytool.task

import xyz.ronella.gradle.plugin.simple.keytool.args.*

abstract class CACertsDeleteTask extends KeytoolTask
        implements IAliasRequiredArg, IVerboseArg, IStorePassArg, IKeyPassArg, IStoreTypeArg {

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
