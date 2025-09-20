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

    private static void initExtension(Project project) {
        project.with {
            extensions.create('simple_keytool', SimpleKeytoolPluginExtension)
            extensions.getByType(SimpleKeytoolPluginExtension).with {
                storePass.convention('changeit')
                dirAliasPrefix.convention('')
                dirAliasSuffix.convention('[sk]')
            }
        }
    }

    private static void initTasks(Project project) {
        project.with {
            task('cacertsDelete', type:CACertsDeleteTask) {
                it.configureExtension(project)
            }
            task('cacertsDeleteDir', type:CACertsDeleteDirTask) {
                it.configureExtension(project)
            }
            task('cacertsImport', type:CACertsImportTask) {
                it.configureExtension(project)
            }
            task('cacertsImportDir', type:CACertsImportDirTask) {
                it.configureExtension(project)
            }
            task('cacertsList', type:CACertsListTask) {
                it.configureExtension(project)
            }
            task('cacertsListDir', type:CACertsListDirTask) {
                it.configureExtension(project)
            }
            task('keytoolTask', type:KeytoolTask) {
                it.configureExtension(project)
            }
            task('ksDelete', type:KSDeleteTask) {
                it.configureExtension(project)
            }
            task('ksDeleteDir', type:KSDeleteDirTask) {
                it.configureExtension(project)
            }
            task('ksImport', type:KSImportTask) {
                it.configureExtension(project)
            }
            task('ksImportDir', type:KSImportDirTask) {
                it.configureExtension(project)
            }
            task('ksList', type:KSListTask) {
                it.configureExtension(project)
            }
            task('ksListDir', type:KSListDirTask) {
                it.configureExtension(project)
            }
        }
    }

    @Override
    void apply(Project project) {
        initExtension(project)
        initTasks(project)
    }

}
