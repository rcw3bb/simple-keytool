package xyz.ronella.gradle.plugin.simple.keytool

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xyz.ronella.gradle.plugin.simple.keytool.tool.PSCommandDecoder

import static org.junit.jupiter.api.Assertions.assertTrue

class CACertsDeleteTaskTest {
    private Project project

    @BeforeEach
    void initProject() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'xyz.ronella.simple-keytool'
        project.extensions.simple_keytool.noop = true
    }

    @Test
    void testKeytoolTask() {
        project.tasks.cacertsDelete.executeCommand()
        assertTrue(project.extensions.simple_keytool.noop.get())
    }

    @Test
    void noCommandParam() {
        var script = project.tasks.cacertsDelete.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"""-delete""","""-cacerts""","""-storepass""'))
    }

    @Test
    void withVerboseParam() {
        project.tasks.cacertsDelete.verbose=true
        var script = project.tasks.cacertsDelete.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"""-delete""","""-cacerts""","""-v""","""-storepass"""'))
    }

    @Test
    void withAliasParam() {
        project.tasks.cacertsDelete.alias='alias'
        var script = project.tasks.cacertsDelete.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"""-delete""","""-cacerts""","""-alias""","""alias""","""-storepass"""'))
    }

    @Test
    void withStoreTypeParam() {
        project.tasks.cacertsDelete.storeType='storeType'
        var script = project.tasks.cacertsDelete.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains('"""-delete""","""-cacerts""","""-storetype""","""storeType""","""-storepass"""'))
    }

}
