package xyz.ronella.gradle.plugin.simple.keytool.task

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject
import xyz.ronella.command.arrays.windows.RunAsChecker
import xyz.ronella.gradle.plugin.simple.keytool.KeytoolExecutor
import xyz.ronella.gradle.plugin.simple.keytool.SimpleKeytoolPluginExtension
import xyz.ronella.gradle.plugin.simple.keytool.args.ArgumentManager
import xyz.ronella.gradle.plugin.simple.keytool.args.IDirArg
import xyz.ronella.trivial.handy.OSType

/**
 * The main keytool task implementation that holds the default behaviour.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
abstract class KeytoolTask extends DefaultTask {

    protected Provider<SimpleKeytoolPluginExtension> extensionProvider

    @Inject
    abstract ObjectFactory getObjects()

    protected Property<String> internalCommand

    protected ListProperty<String> internalArgs

    protected ListProperty<String> internalZArgs

    protected Property<Boolean> isScriptMode

    /**
     * Specifies if the command to be generated is to be run in elevated mode.
     * @return True to run in elevated mode.
     */
    @Optional @Input
    abstract Property<Boolean> getIsAdminMode()

    /**
     * Must hold the command to execute.
     *
     * @return The command to execute.
     */
    @Optional @Input
    abstract Property<String> getCommand()

    /**
     * Must hold the target java home.
     *
     * @return The target java home.
     */
    @Optional @Input
    abstract Property<File> getJavaHome()


    /**
     * The arguments to be passed to the command.
     *
     * @return The arguments.
     */
    @Optional @Input
    abstract ListProperty<String> getArgs()

    /**
     * The terminal arguments to be passed to the command.
     * Theses arguments becomes the series of last argument of the command.
     *
     * @return The arguments.
     */
    @Optional @Input
    abstract ListProperty<String> getZArgs()

    /**
     * Creates an instance of the KeytoolTask.
     */
    KeytoolTask() {        
        group = 'Simple Keytool'
        description = 'Executes any valid java keytool command.'

        args.convention([])
        ZArgs.convention([])

        isScriptMode = objects.property(Boolean)
        internalArgs = objects.listProperty(String)
        internalZArgs = objects.listProperty(String)
        internalCommand = objects.property(String)

        isAdminMode.convention(false)
        isScriptMode.convention(false)
    }
    
    /**
     * Configure the extension provider during task configuration (not construction)
     */
    void configureExtension(Project project) {
        extensionProvider = project.provider { project.extensions.simple_keytool }
    }

    /**
     * Get the extension provider, initializing it if necessary (fallback for direct task usage)
     */
    private Provider<SimpleKeytoolPluginExtension> getExtensionProviderSafe() { //codenarc-disable MethodName
        if (extensionProvider == null) {
            // Fallback: create provider directly from project (this will show deprecation warning)
            // This should only happen if task is used outside of the plugin registration
            logger.warn('Extension provider not configured. Task should be created through SimpleKeytoolPlugin.')
            extensionProvider = project.provider { project.extensions.simple_keytool }
        }
        extensionProvider
    }

    @Internal
    protected ListProperty<String> getAllArgs() {
        ArgumentManager.processArgs(this, internalArgs, getExtensionProviderSafe().get()) //codenarc-disable UnnecessaryGetter

        def newArgs = []
        newArgs.addAll(internalArgs.get())
        newArgs.addAll(args.get())
        newArgs.addAll(internalZArgs.get())
        newArgs.addAll(ZArgs.get())

        def allTheArgs = objects.listProperty(String)
        if ((command.getOrElse('').length()>0 || newArgs.size() > 0)) {
            allTheArgs.addAll(newArgs)
        }
        else {
            allTheArgs.add('--help')
        }

        return allTheArgs //codenarc-disable UnnecessaryReturnKeyword
    }

    /**
     * The method that holds the logic of executing the command.
     *
     * @return The actual command to be executed.
     */
    @TaskAction
    String executeCommand() {
        var ext = getExtensionProviderSafe().get() //codenarc-disable UnnecessaryGetter
        var builder = KeytoolExecutor.builder
                .addNoop(ext.noop.getOrElse(false))
                .addOSType(OSType.identify())
                .addJavaHome(javaHome.getOrElse(ext.javaHome.orNull))
                .addAdminMode(isAdminMode.get())
                .addCommand(internalCommand.present ? internalCommand.get() : command.orNull)
                .addArgs(allArgs.get().toArray((String[])[]))
                .addRunningInAdminMode(RunAsChecker.elevatedMode)
                .addScriptMode(isScriptMode.get())
                .addDirAliasPrefix(ext.dirAliasPrefix.get())
                .addDirAliasSuffix(ext.dirAliasSuffix.get())

        if (this instanceof IDirArg) {
            var dirArg = (IDirArg) this
            var certsDir = dirArg.dir.asFile.orNull
            builder.addDirectory(certsDir==null ? ext.defaultCertsDir.asFile.orNull : certsDir)
            var fileArgs = dirArg.fileArgs
            //codenarc-disable UnnecessaryGetter
            builder.addFileArgs(fileArgs.isPresent() ? fileArgs.get() : ext.defaultFileArgs.get())
            //codenarc-enable UnnecessaryGetter
        }

        var executor = builder.build()

        var command = executor.execute()
        if (ext.showExecCode.getOrElse(false)) {
            println "Command executed: ${command}" // codenarc-disable Println
        }

        return command //codenarc-disable UnnecessaryReturnKeyword

    }
}
