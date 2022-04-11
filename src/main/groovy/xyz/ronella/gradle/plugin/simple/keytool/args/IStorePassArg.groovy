package xyz.ronella.gradle.plugin.simple.keytool.args

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * The interface to add the storepass argument for the command.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
interface IStorePassArg {

    /**
     * Must hold the password of the keystore.
     *
     * @return The password of the keystore.
     */
    @Optional @Input
    Property<String> getStorePass()

}
