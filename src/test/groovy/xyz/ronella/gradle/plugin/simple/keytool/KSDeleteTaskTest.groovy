package xyz.ronella.gradle.plugin.simple.keytool

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xyz.ronella.gradle.plugin.simple.keytool.tool.PSCommandDecoder

import static org.junit.jupiter.api.Assertions.assertTrue

class KSDeleteTaskTest {
    private Project project

    @BeforeEach
    void initProject() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'xyz.ronella.simple-keytool'
        project.extensions.simple_keytool.noop = true
    }

    @Test
    void testKeytoolTask() {
        project.tasks.ksDelete.executeCommand()
        assertTrue(project.extensions.simple_keytool.noop.get())
    }

    @Test
    void noCommandParam() {
        var script = project.tasks.ksDelete.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"-delete","-storepass"'))
    }

    @Test
    void withVerboseParam() {
        project.tasks.ksDelete.verbose=true
        var script = project.tasks.ksDelete.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"-delete","-v","-storepass"'))
    }

    @Test
    void withAliasParam() {
        project.tasks.ksDelete.alias='alias'
        var script = project.tasks.ksDelete.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"-delete","-alias","alias","-storepass"'))
    }

    @Test
    void withStoreTypeParam() {
        project.tasks.ksDelete.storeType='storeType'
        var script = project.tasks.ksDelete.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"-delete","-storetype","storeType","-storepass"'))
    }

    @Test
    void withKeyStoreParam() {
        project.tasks.ksDelete.keyStore = project.file('keystore')
        var script = project.tasks.ksDelete.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"-keystore"'))
    }

}
