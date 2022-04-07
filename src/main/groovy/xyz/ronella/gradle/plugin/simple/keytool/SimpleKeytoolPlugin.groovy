package xyz.ronella.gradle.plugin.simple.keytool

import org.gradle.api.Plugin
import org.gradle.api.Project
import xyz.ronella.gradle.plugin.simple.keytool.task.KeytoolTask

class SimpleKeytoolPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create('simple_keytool', SimpleKeytoolPluginExtension)
        project.task('keytoolTask', type: KeytoolTask)
    }
}