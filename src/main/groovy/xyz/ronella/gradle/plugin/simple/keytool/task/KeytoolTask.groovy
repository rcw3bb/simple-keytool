package xyz.ronella.gradle.plugin.simple.keytool.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import xyz.ronella.gradle.plugin.simple.keytool.SimpleKeytoolPluginExtension

abstract class KeytoolTask extends DefaultTask {

    final SimpleKeytoolPluginExtension EXTENSION

    KeytoolTask() {
        EXTENSION = project.extensions.simple_keytool

        group = 'Simple Keytool'
        description = 'Executes any valid java keytool command.'
    }

    protected void writeln(String text) {
        if (EXTENSION.verbose.get()) {
            println(text)
        }
    }

    @TaskAction
    def executeCommand() {
        writeln("Hello World")
        EXTENSION.verbose = false
    }
}
