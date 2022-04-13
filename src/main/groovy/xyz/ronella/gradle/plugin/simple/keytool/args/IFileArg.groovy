package xyz.ronella.gradle.plugin.simple.keytool.args

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile

/**
 * The interface to add the file argument for the command.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
interface IFileArg {

    /**
     * Must point to a valid keystore file.
     *
     * @return A keystore file.
     */
    @InputFile
    RegularFileProperty getFile()
}
