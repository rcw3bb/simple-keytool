package xyz.ronella.gradle.plugin.simple.keytool

import org.gradle.api.provider.Property

/**
 * The Keytool Extension class.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
abstract class SimpleKeytoolPluginExtension {

    /**
     * Provides more detailed output when true if it implements IVerboseArg.
     *
     * @return True to have detailed output.
     */
    abstract Property<Boolean> getVerbose()

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
     * Create an instance of SimpleKeytoolPluginExtension.
     */
    SimpleKeytoolPluginExtension() {
        storePass.convention('changeit')
    }

}
