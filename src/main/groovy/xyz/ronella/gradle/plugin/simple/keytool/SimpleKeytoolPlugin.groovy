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
        project.task('cacertsDelete', type: CACertsDeleteTask)
        project.task('cacertsImport', type: CACertsImportTask)
        project.task('cacertsList', type: CACertsListTask)
        project.task('keytoolTask', type: KeytoolTask)
        project.task('ksDelete', type: KSDeleteTask)
        project.task('ksImport', type: KSImportTask)
        project.task('ksList', type: KSListTask)
    }
}