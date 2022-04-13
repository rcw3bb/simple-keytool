package xyz.ronella.gradle.plugin.simple.keytool

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xyz.ronella.gradle.plugin.simple.keytool.tool.PSCommandDecoder

import static org.junit.jupiter.api.Assertions.assertTrue

class CACertsImportTaskTest {
    private Project project

    @BeforeEach
    void initProject() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'xyz.ronella.simple-keytool'
        project.extensions.simple_keytool.noop = true
    }

    @Test
    void testKeytoolTask() {
        project.tasks.cacertsImport.executeCommand()
        assertTrue(project.extensions.simple_keytool.noop.get())
    }

    @Test
    void noCommandParam() {
        var script = project.tasks.cacertsImport.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"-importcert","-cacerts","-storepass"'))
    }

    @Test
    void withVerboseParam() {
        project.tasks.cacertsImport.verbose=true
        var script = project.tasks.cacertsImport.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"-importcert","-cacerts","-v","-storepass"'))
    }

    @Test
    void withAliasParam() {
        project.tasks.cacertsImport.alias='alias'
        var script = project.tasks.cacertsImport.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"-importcert","-cacerts","-alias","alias","-storepass"'))
    }

    @Test
    void withStoreTypeParam() {
        project.tasks.cacertsImport.storeType='storeType'
        var script = project.tasks.cacertsImport.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"-importcert","-cacerts","-storetype","storeType","-storepass"'))
    }

    @Test
    void withFileParam() {
        project.tasks.cacertsImport.file=project.file('dummy')
        var script = project.tasks.cacertsImport.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"-file"'))
    }
}
