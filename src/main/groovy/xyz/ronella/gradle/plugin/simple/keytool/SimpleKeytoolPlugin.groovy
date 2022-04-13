package xyz.ronella.gradle.plugin.simple.keytool

import org.gradle.api.Plugin
import org.gradle.api.Project
import xyz.ronella.gradle.plugin.simple.keytool.task.*

/**
 * The implementation class.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
class SimpleKeytoolPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create('simple_keytool', SimpleKeytoolPluginExtension)
        project.task('keytoolTask', type: KeytoolTask)
        project.task('cacertsList', type: CACertsListTask)
        project.task('ksList', type: KSListTask)
        project.task('cacertsImport', type: CACertsImportTask)
    }
}