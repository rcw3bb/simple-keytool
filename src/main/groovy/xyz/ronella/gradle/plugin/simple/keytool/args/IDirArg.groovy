package xyz.ronella.gradle.plugin.simple.keytool.args

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional

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

    /**
     * Must hold particular arguments associated to a particular file.
     *
     * @return A map of file against its arguments.
     */
    @Optional @Input
    MapProperty<String, List<String>> getFileArgs()

}