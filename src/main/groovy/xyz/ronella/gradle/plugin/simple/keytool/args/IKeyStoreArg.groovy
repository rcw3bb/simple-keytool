package xyz.ronella.gradle.plugin.simple.keytool.args

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile

/**
 * The interface to add the keystore argument for the command.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
interface IKeyStoreArg {

    /**
     * Must point to a valid keystore file.
     *
     * @return A keystore file.
     */
    @InputFile
    RegularFileProperty getKeyStore()

}