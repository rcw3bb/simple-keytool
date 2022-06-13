package xyz.ronella.gradle.plugin.simple.keytool

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

/**
 * The Keytool Extension class.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
abstract class SimpleKeytoolPluginExtension {

    /**
     * Display the command to be executed but not actually executes the command.
     *
     * @return True to suppress execution.
     */
    abstract Property<Boolean> getNoop()

    /**
     * Must hold a valid location of Java distribution.
     *
     * @return The location of the Java distribution.
     */
    abstract Property<File> getJavaHome()

    /**
     * Must hold the value of the storepass if not the default.
     * This is for all the tasks that implements IStorePassArg.
     *
     * @return The password for the keystore.
     */
    abstract Property<String> getStorePass()

    /**
     * Output the actual code executed.
     *
     * @return The actual code executed when true.
     */
    abstract Property<Boolean> getShowExecCode()

    /**
     * Must hold the alias prefix of the directory processed certificates.
     *
     * @return The desired prefix.
     */
    abstract Property<String> getDirAliasPrefix()

    /**
     * Must hold the alias suffix of the directory processed certificates.
     *
     * @return The desired suffix.
     */
    abstract Property<String> getDirAliasSuffix()

    /**
     * Must hold the default fileArgs that the directory based task can use.
     *
     * @return The file args.
     * @since 1.1.0
     */
    abstract MapProperty<String, List<String>> getDefaultFileArgs()

    /**
     * Must hold the default certificates directory that the directory based task can use.
     *
     * @return The file args.
     * @since 1.1.0
     */
    abstract DirectoryProperty getDefaultCertsDir()

}
