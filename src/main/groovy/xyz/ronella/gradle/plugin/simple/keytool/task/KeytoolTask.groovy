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
import xyz.ronella.gradle.plugin.simple.keytool.tool.OSType
import xyz.ronella.gradle.plugin.simple.keytool.tool.RunAsChecker

abstract class KeytoolTask extends DefaultTask {

    final SimpleKeytoolPluginExtension EXTENSION

    protected Property<Boolean> isAdminMode

    protected Property<String> internalCommand

    protected ListProperty<String> internalArgs

    protected ListProperty<String> internalZArgs

    @Optional @Input
    abstract Property<String> getCommand()

    @Optional @Input
    abstract ListProperty<String> getArgs()

    @Optional @Input
    abstract ListProperty<String> getZArgs()

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

    protected void writeln(String text) {
        if (EXTENSION.verbose.get()) {
            println(text)
        }
    }

    @Internal
    protected ListProperty<String> getAllArgs() {
        def newArgs = args.get()
        def allTheArgs = project.objects.listProperty(String.class)
        if ((command.getOrElse("").length()>0 || newArgs.size() > 0)) {
            allTheArgs.addAll(newArgs)
        }
        else {
            allTheArgs.add('--help')
        }

        return allTheArgs
    }

    @TaskAction
    String executeCommand() {

        var executor = KeytoolExecutor.getBuilder()
                .addNoop(EXTENSION.noop.getOrElse(false))
                .addOSType(OSType.identify())
                .addJavaHome(EXTENSION.javaHome.getOrNull())
                .addAdminMode(isAdminMode.get())
                .addCommand(internalCommand.isPresent() ? internalCommand.get() : command.getOrNull())
                .addArgs(internalArgs.get().toArray((String[])[]))
                .addArgs(allArgs.get().toArray((String[])[]))
                .addZArgs(internalZArgs.get().toArray((String[])[]))
                .addZArgs(getZArgs().get().toArray((String[])[]))
                .addRunningInAdminMode(RunAsChecker.isElevatedMode())
                .build()

        executor.execute()
    }
}
