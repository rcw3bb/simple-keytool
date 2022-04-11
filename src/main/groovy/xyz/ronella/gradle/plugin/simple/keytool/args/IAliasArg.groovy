package xyz.ronella.gradle.plugin.simple.keytool.args

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * The interface to add the alias argument for the command.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
interface IAliasArg {

    /**
     * Must hold the target alias.
     *
     * @return An alias.
     */
    @Optional @Input
    Property<String> getAlias()

}