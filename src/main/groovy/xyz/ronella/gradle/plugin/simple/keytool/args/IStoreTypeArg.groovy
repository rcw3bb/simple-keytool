package xyz.ronella.gradle.plugin.simple.keytool.args

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * The interface to add the storetype argument for the command.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
interface IStoreTypeArg {

    /**
     * Must hold the storetype.
     *
     * @return The storetype.
     */
    @Optional @Input
    Property<String> getStoreType()

}