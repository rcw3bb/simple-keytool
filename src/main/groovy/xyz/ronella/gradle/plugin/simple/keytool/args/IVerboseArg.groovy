package xyz.ronella.gradle.plugin.simple.keytool.args

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * The interface to add the verbose argument for the command.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
interface IVerboseArg {

    /**
     * Must be true to make the command output more detailed.
     *
     * @return true make a detailed output.
     */
    @Optional @Input
    Property<Boolean> getVerbose()

}
