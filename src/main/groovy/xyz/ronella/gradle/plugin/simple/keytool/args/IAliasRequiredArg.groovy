package xyz.ronella.gradle.plugin.simple.keytool.args

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

/**
 * The interface to add to require the alias argument for the command.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
interface IAliasRequiredArg {

    /**
     * Must hold the target alias.
     *
     * @return An alias.
     */
    @Input
    Property<String> getAlias()
}