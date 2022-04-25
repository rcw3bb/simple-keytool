package xyz.ronella.gradle.plugin.simple.keytool

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xyz.ronella.gradle.plugin.simple.keytool.tool.PSCommandDecoder

import static org.junit.jupiter.api.Assertions.assertTrue

class KSImportTaskTest {
    private Project project

    @BeforeEach
    void initProject() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'xyz.ronella.simple-keytool'
        project.extensions.simple_keytool.noop = true
    }

    @Test
    void testKeytoolTask() {
        project.tasks.ksImport.executeCommand()
        assertTrue(project.extensions.simple_keytool.noop.get())
    }

    @Test
    void noCommandParam() {
        var script = project.tasks.ksImport.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"""-importcert""","""-storepass"""'))
    }

    @Test
    void withVerboseParam() {
        project.tasks.ksImport.verbose=true
        var script = project.tasks.ksImport.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"""-importcert""","""-v""","""-storepass"""'))
    }

    @Test
    void withAliasParam() {
        project.tasks.ksImport.alias='alias'
        var script = project.tasks.ksImport.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"""-importcert""","""-alias""","""alias""","""-storepass"""'))
    }

    @Test
    void withStoreTypeParam() {
        project.tasks.ksImport.storeType='storeType'
        var script = project.tasks.ksImport.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"""-importcert""","""-storetype""","""storeType""","""-storepass"""'))
    }

    @Test
    void withFileParam() {
        project.tasks.ksImport.file=project.file('dummy')
        var script = project.tasks.ksImport.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"-file"'))
    }

    @Test
    void withKeyStoreParam() {
        project.tasks.ksImport.keyStore = project.file('keystore')
        var script = project.tasks.ksImport.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"-keystore"'))
    }
}
