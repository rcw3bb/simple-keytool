package xyz.ronella.gradle.plugin.simple.keytool.task

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import xyz.ronella.gradle.plugin.simple.keytool.KeytoolExecutor
import xyz.ronella.gradle.plugin.simple.keytool.SimpleKeytoolPluginExtension
import xyz.ronella.gradle.plugin.simple.keytool.args.ArgumentManager
import xyz.ronella.gradle.plugin.simple.keytool.tool.OSType
import xyz.ronella.gradle.plugin.simple.keytool.tool.RunAsChecker

/**
 * The main keytool task implementation that holds the default behaviour.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
abstract class KeytoolTask extends DefaultTask {

    final protected SimpleKeytoolPluginExtension EXTENSION

    protected Property<Boolean> isAdminMode

    protected Property<String> internalCommand

    protected ListProperty<String> internalArgs

    protected ListProperty<String> internalZArgs

    /**
     * Must hold the command to execute.
     *
     * @return The command to execute.
     */
    @Optional @Input
    abstract Property<String> getCommand()

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
        EXTENSION = project.extensions.simple_keytool

        group = 'Simple Keytool'
        description = 'Executes any valid java keytool command.'

        args.convention([])
        getZArgs().convention([])

        var objects = project.objects
        isAdminMode = objects.property(Boolean.class)
        internalArgs = objects.listProperty(String.class)
        internalZArgs = objects.listProperty(String.class)
        internalCommand = objects.property(String.class)

        isAdminMode.convention(false)
    }

    @Internal
    protected ListProperty<String> getAllArgs() {

        ArgumentManager.processArgs(this, internalArgs, EXTENSION)

        def newArgs = new ArrayList<String>()
        newArgs.addAll(internalArgs.get())
        newArgs.addAll(args.get())
        newArgs.addAll(internalZArgs.get())
        newArgs.addAll(getZArgs().get())

        def allTheArgs = project.objects.listProperty(String.class)
        if ((command.getOrElse("").length()>0 || newArgs.size() > 0)) {
            allTheArgs.addAll(newArgs)
        }
        else {
            allTheArgs.add('--help')
        }

        return allTheArgs
    }

    /**
     * The method that holds the logic of executing the command.
     *
     * @return The actual command to be executed.
     */
    @TaskAction
    String executeCommand() {
        var executor = KeytoolExecutor.getBuilder()
                .addNoop(EXTENSION.noop.getOrElse(false))
                .addOSType(OSType.identify())
                .addJavaHome(EXTENSION.javaHome.getOrNull())
                .addAdminMode(isAdminMode.get())
                .addCommand(internalCommand.isPresent() ? internalCommand.get() : command.getOrNull())
                .addArgs(allArgs.get().toArray((String[])[]))
                .addRunningInAdminMode(RunAsChecker.isElevatedMode())
                .build()

        executor.execute()
    }
}
