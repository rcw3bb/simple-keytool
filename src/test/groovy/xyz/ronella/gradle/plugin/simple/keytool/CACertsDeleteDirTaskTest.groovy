package xyz.ronella.gradle.plugin.simple.keytool

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xyz.ronella.gradle.plugin.simple.keytool.tool.PSCommandDecoder

import static org.junit.jupiter.api.Assertions.*

class CACertsDeleteDirTaskTest {
    private Project project

    @BeforeEach
    void initProject() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'xyz.ronella.simple-keytool'
        project.extensions.simple_keytool.noop = true
    }

    @Test
    void noCommandParam() {
        assertThrows(KeytoolNoCommandException.class, {project.tasks.cacertsDeleteDir.executeCommand()})
    }

    @Test
    void withVerboseParam() {
        project.tasks.cacertsDeleteDir.verbose=true
        project.tasks.cacertsDeleteDir.dir = project.file("../../../../resources/test/certs")
        var script = project.tasks.cacertsDeleteDir.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains("-Command") && command.contains("keytool.exe") && command.contains("delete")
                && command.contains("cacerts") && command.contains("-v")
                && !command.contains("-file") && command.contains("cert1.cer [sk]")
                && command.contains("cert2.cer [sk]"))
    }

    @Test
    void withStoreTypeParam() {
        project.tasks.cacertsDeleteDir.storeType='storeType'
        project.tasks.cacertsDeleteDir.dir = project.file("../../../../resources/test/certs")
        var script = project.tasks.cacertsDeleteDir.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains("-Command") && command.contains("keytool.exe") && command.contains("delete")
                && !command.contains("-file") && command.contains("cacerts")
                && command.contains("cert1.cer [sk]") && command.contains("cert2.cer [sk]"))
    }

}
