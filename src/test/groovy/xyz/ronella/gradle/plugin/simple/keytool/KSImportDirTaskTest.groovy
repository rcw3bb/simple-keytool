package xyz.ronella.gradle.plugin.simple.keytool

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xyz.ronella.gradle.plugin.simple.keytool.tool.PSCommandDecoder

import static org.junit.jupiter.api.Assertions.*

class KSImportDirTaskTest {
    private Project project

    @BeforeEach
    void initProject() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'xyz.ronella.simple-keytool'
        project.extensions.simple_keytool.noop = true
    }

    @Test
    void noCommandParam() {
        assertThrows(KeytoolNoCommandException.class, {project.tasks.ksImportDir.executeCommand()})
    }

    @Test
    void withVerboseParam() {
        project.tasks.ksImportDir.verbose=true
        project.tasks.ksImportDir.storePass="changeit"
        project.tasks.ksImportDir.keyStore=project.file("keystore")
        project.tasks.ksImportDir.dir = project.file("../../../../resources/test/certs")
        var script = project.tasks.ksImportDir.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains("-Command") && command.contains("keytool.exe")
                && command.contains("importcert") && !command.contains("cacerts")
                && command.contains("-v") && command.contains("-file")
                && command.contains("cert1.cer") && command.contains("cert2.cer")
                && command.contains("-keystore"))
    }

    @Test
    void withStoreTypeParam() {
        project.tasks.ksImportDir.storeType='storeType'
        project.tasks.ksImportDir.keyStore=project.file("keystore")
        project.tasks.ksImportDir.dir = project.file("../../../../resources/test/certs")
        var script = project.tasks.ksImportDir.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains("-Command") && command.contains("keytool.exe")
                && command.contains("importcert") && !command.contains("cacerts")
                && command.contains("-file") && command.contains("cert1.cer")
                && command.contains("cert2.cer") && command.contains("-keystore"))
    }

}
