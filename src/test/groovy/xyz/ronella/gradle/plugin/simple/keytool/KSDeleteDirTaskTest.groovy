package xyz.ronella.gradle.plugin.simple.keytool

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xyz.ronella.gradle.plugin.simple.keytool.tool.PSCommandDecoder

import static org.junit.jupiter.api.Assertions.*

class KSDeleteDirTaskTest {
    private Project project

    @BeforeEach
    void initProject() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'xyz.ronella.simple-keytool'
        project.extensions.simple_keytool.noop = true
    }

    @Test
    void noCommandParam() {
        assertThrows(KeytoolNoCommandException.class, {project.tasks.ksDeleteDir.executeCommand()})
    }

    @Test
    void withVerboseParam() {
        project.tasks.ksDeleteDir.verbose=true
        project.tasks.ksDeleteDir.keyStore=project.file("keystore")
        project.tasks.ksDeleteDir.dir = project.file("../../../../resources/test/certs")
        var script = project.tasks.ksDeleteDir.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains("-Command") && command.contains("keytool.exe")
                && command.contains("delete") && !command.contains("cacerts")
                && command.contains("-v") && !command.contains("-file")
                && command.contains("cert1.cer [sk]") && command.contains("cert2.cer [sk]")
                && command.contains("-keystore"))
    }

    @Test
    void withStoreTypeParam() {
        project.tasks.ksDeleteDir.storeType='storeType'
        project.tasks.ksDeleteDir.keyStore=project.file("keystore")
        project.tasks.ksDeleteDir.dir = project.file("../../../../resources/test/certs")
        var script = project.tasks.ksDeleteDir.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains("-Command") && command.contains("keytool.exe")
                && command.contains("delete") && !command.contains("-file")
                && !command.contains("cacerts") && command.contains("-keystore")
                && command.contains("cert1.cer [sk]") && command.contains("cert2.cer [sk]"))
    }

}
