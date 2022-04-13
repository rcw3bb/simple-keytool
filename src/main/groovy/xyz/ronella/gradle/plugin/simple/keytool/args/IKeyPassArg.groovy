package xyz.ronella.gradle.plugin.simple.keytool.args

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * The interface to add the keypass argument for the command.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
interface IKeyPassArg {

    /**
     * Must hold the keypass.
     *
     * @return The keypass.
     */
    @Optional @Input
    Property<String> getKeyPass()

}
