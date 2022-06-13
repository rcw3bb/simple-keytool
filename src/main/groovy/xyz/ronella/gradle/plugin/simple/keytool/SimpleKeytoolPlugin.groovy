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
            task('cacertsDelete', type:CACertsDeleteTask)
            task('cacertsDeleteDir', type:CACertsDeleteDirTask)
            task('cacertsImport', type:CACertsImportTask)
            task('cacertsImportDir', type:CACertsImportDirTask)
            task('cacertsList', type:CACertsListTask)
            task('cacertsListDir', type:CACertsListDirTask)
            task('keytoolTask', type:KeytoolTask)
            task('ksDelete', type:KSDeleteTask)
            task('ksDeleteDir', type:KSDeleteDirTask)
            task('ksImport', type:KSImportTask)
            task('ksImportDir', type:KSImportDirTask)
            task('ksList', type:KSListTask)
            task('ksListDir', type:KSListDirTask)
        }
    }

    @Override
    void apply(Project project) {
        initExtension(project)
        initTasks(project)
    }

}
