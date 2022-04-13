package xyz.ronella.gradle.plugin.simple.keytool

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertTrue

class CACertsListTaskTest {
    private Project project

    @BeforeEach
    void initProject() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'xyz.ronella.simple-keytool'
        project.extensions.simple_keytool.noop = true
    }

    @Test
    void testKeytoolTask() {
        project.tasks.cacertsList.executeCommand()
        assertTrue(project.extensions.simple_keytool.noop.get())
    }

    @Test
    void noCommandParam() {
        var command = project.tasks.cacertsList.executeCommand()
        assertTrue(command.contains('keytool.exe -list -cacerts -storepass'))
    }

    @Test
    void withVerboseParam() {
        project.tasks.cacertsList.verbose=true
        var command = project.tasks.cacertsList.executeCommand()
        assertTrue(command.contains('keytool.exe -list -cacerts -v -storepass'))
    }

    @Test
    void withAliasParam() {
        project.tasks.cacertsList.alias='alias'
        var command = project.tasks.cacertsList.executeCommand()
        assertTrue(command.contains('keytool.exe -list -cacerts -alias alias -storepass'))
    }

    @Test
    void withStoreTypeParam() {
        project.tasks.cacertsList.storeType='storeType'
        var command = project.tasks.cacertsList.executeCommand()
        assertTrue(command.contains('keytool.exe -list -cacerts -storetype storeType -storepass'))
    }
}
