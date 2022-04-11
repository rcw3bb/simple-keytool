package xyz.ronella.gradle.plugin.simple.keytool

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertTrue

class KSListTaskTest {

    private Project project

    @BeforeEach
    void initProject() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'xyz.ronella.simple-keytool'
        project.extensions.simple_keytool.noop = true
    }

    @Test
    void testKeytoolTask() {
        project.tasks.ksList.executeCommand()
        assertTrue(project.extensions.simple_keytool.noop.get())
    }

    @Test
    void noCommandParam() {
        var command = project.tasks.ksList.executeCommand()
        assertTrue(command.contains('keytool.exe -list -storepass'))
    }

    @Test
    void withVerboseParam() {
        project.tasks.ksList.verbose=true
        var command = project.tasks.ksList.executeCommand()
        assertTrue(command.contains('keytool.exe -list -v -storepass'))
    }

    @Test
    void withAliasParam() {
        project.tasks.ksList.alias='alias'
        var command = project.tasks.ksList.executeCommand()
        assertTrue(command.contains('keytool.exe -list -alias alias -storepass'))
    }

    @Test
    void withKeyStoreParam() {
        project.tasks.ksList.keyStore = project.file('keystore')
        var command = project.tasks.ksList.executeCommand()
        assertTrue(command.contains('keystore -storepass'))
    }

}
