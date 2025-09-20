package xyz.ronella.gradle.plugin.simple.keytool

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xyz.ronella.gradle.plugin.simple.keytool.task.KeytoolTask
import xyz.ronella.gradle.plugin.simple.keytool.tool.PSCommandDecoder

import static org.junit.jupiter.api.Assertions.*

class KeytoolTaskTest {
    private Project project

    @BeforeEach
    void initProject() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'xyz.ronella.simple-keytool'
        project.extensions.simple_keytool.noop = true
    }

    @Test
    void testKeytoolTask() {
        project.tasks.keytoolTask.executeCommand()
        assertTrue(project.extensions.simple_keytool.noop.get())
    }

    @Test
    void noCommandParam() {
        var command = project.tasks.keytoolTask.executeCommand()
        assertTrue(command.endsWith('keytool.exe --help'))
    }

    @Test
    void withCommandParam() {
        def task = (KeytoolTask) project.tasks.keytoolTask
        task.command = 'command'
        def command = project.tasks.keytoolTask.executeCommand()
        assertTrue(command.endsWith('keytool.exe command'))
    }

    @Test
    void withCommandSingleArgParam() {
        def task = (KeytoolTask) project.tasks.keytoolTask
        task.command = 'command'
        task.args = ['arg1']
        def command = project.tasks.keytoolTask.executeCommand()
        assertTrue(command.endsWith('keytool.exe command arg1'))
    }

    @Test
    void withCommandMultipleArgsParam() {
        def task = (KeytoolTask) project.tasks.keytoolTask
        task.command = 'command'
        task.args = ['arg1', 'arg2']
        def command = project.tasks.keytoolTask.executeCommand()
        assertTrue(command.endsWith('keytool.exe command arg1 arg2'))
    }

    @Test
    void withCommandSingleZArgParam() {
        def task = (KeytoolTask) project.tasks.keytoolTask
        task.command = 'command'
        task.args = ['arg1', 'arg2']
        task.getZArgs().add('zarg1')
        def command = project.tasks.keytoolTask.executeCommand()
        assertTrue(command.endsWith('keytool.exe command arg1 arg2 zarg1'))
    }

    @Test
    void withCommandSingleZArgParamAsAdmin() {
        def task = (KeytoolTask) project.tasks.keytoolTask
        task.command = 'command'
        task.isAdminMode = true
        task.args = ['arg1', 'arg2']
        task.getZArgs().add('zarg1')
        def command = project.tasks.keytoolTask.executeCommand()
        def adminCommand = PSCommandDecoder.decode(command)
        assertTrue(adminCommand.contains("keytool.exe") && adminCommand.contains("command")
            && adminCommand.contains("RunAs") && adminCommand.contains("arg1")
            && adminCommand.contains("arg2") && adminCommand.contains("zarg1")
        )
    }

    @Test
    void withCommandMultipleZArgsParam() {
        def task = (KeytoolTask) project.tasks.keytoolTask
        task.command = 'command'
        task.args = ['arg1', 'arg2']
        task.getZArgs().addAll('zarg1', 'zarg2')
        def command = project.tasks.keytoolTask.executeCommand()
        assertTrue(command.endsWith('keytool.exe command arg1 arg2 zarg1 zarg2'))
    }

    @Test
    void testExtensionProviderFallback() {
        // Create a task without plugin registration to test the null-safety mechanism
        def project2 = ProjectBuilder.builder().build()
        project2.pluginManager.apply 'xyz.ronella.simple-keytool'
        project2.extensions.simple_keytool.noop = true
        
        // Create task directly without configureExtension() call
        def task = project2.tasks.create('testTask', KeytoolTask)
        
        // This should work without throwing NullPointerException
        // The fallback mechanism should handle the null extensionProvider
        task.command = 'list'
        def command = task.executeCommand()
        
        // Verify it works and falls back correctly
        assertNotNull(command)
        assertTrue(command.contains('keytool.exe'))
        assertTrue(command.contains('list'))
    }

}