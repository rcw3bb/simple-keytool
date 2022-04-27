package xyz.ronella.gradle.plugin.simple.keytool.args

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory

/**
 * The interface to add the dir argument for the command.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
interface IDirArg {

    /**
     * Must point to a valid keystore file.
     *
     * @return A keystore file.
     */
    @InputDirectory
    DirectoryProperty getDir()

}