package xyz.ronella.gradle.plugin.simple.keytool

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xyz.ronella.gradle.plugin.simple.keytool.task.KeytoolTask

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
    void withCommandMultipleZArgsParam() {
        def task = (KeytoolTask) project.tasks.keytoolTask
        task.command = 'command'
        task.args = ['arg1', 'arg2']
        task.getZArgs().addAll('zarg1', 'zarg2')
        def command = project.tasks.keytoolTask.executeCommand()
        assertTrue(command.endsWith('keytool.exe command arg1 arg2 zarg1 zarg2'))
    }

}