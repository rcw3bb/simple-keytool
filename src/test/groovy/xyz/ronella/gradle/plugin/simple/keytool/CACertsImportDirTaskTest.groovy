package xyz.ronella.gradle.plugin.simple.keytool

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xyz.ronella.gradle.plugin.simple.keytool.tool.PSCommandDecoder

import static org.junit.jupiter.api.Assertions.assertThrows
import static org.junit.jupiter.api.Assertions.assertTrue

class CACertsImportDirTaskTest {
    private Project project

    @BeforeEach
    void initProject() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'xyz.ronella.simple-keytool'
        project.extensions.simple_keytool.noop = true
    }

    @Test
    void noCommandParam() {
        assertThrows(KeytoolNoCommandException.class, {project.tasks.cacertsImportDir.executeCommand()})
    }

    @Test
    void withVerboseParam() {
        project.tasks.cacertsImportDir.verbose=true
        project.tasks.cacertsImportDir.storePass="changeit"
        project.tasks.cacertsImportDir.dir = project.file("../../../../resources/test/certs")
        var script = project.tasks.cacertsImportDir.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains("-Command") && command.contains("keytool.exe") && command.contains("importcert")
                && command.contains("cacerts") && command.contains("-v") && command.contains("-file")
                && command.contains("cert1.cer") && command.contains("cert2.cer"))
    }

    @Test
    void withStoreTypeParam() {
        project.tasks.cacertsImportDir.storeType='storeType'
        project.tasks.cacertsImportDir.dir = project.file("../../../../resources/test/certs")
        var script = project.tasks.cacertsImportDir.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains("-Command") && command.contains("keytool.exe") && command.contains("importcert")
                && command.contains("cacerts") && command.contains("-file")
                && command.contains("cert1.cer") && command.contains("cert2.cer"))
    }

    @Test
    void differentCert1Args() {
        project.tasks.cacertsImportDir.storeType='storeType'
        project.tasks.cacertsImportDir.dir = project.file("../../../../resources/test/certs")
        project.tasks.cacertsImportDir.fileArgs = ["cert1.cer": ["-customArg"]]
        var script = project.tasks.cacertsImportDir.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains("-Command") && command.contains("keytool.exe") && command.contains("importcert")
                && command.contains("cacerts") && command.contains("-file")
                && command.contains("-customArg") && command.contains("cert1.cer")
                && command.contains("cert2.cer"))
    }

    @Test
    void differentCert1ArgsUsingDefaultFileArgs() {
        project.extensions.simple_keytool.defaultFileArgs = ["cert1.cer": ["-customArg"]]
        project.tasks.cacertsImportDir.storeType='storeType'
        project.tasks.cacertsImportDir.dir = project.file("../../../../resources/test/certs")
        var script = project.tasks.cacertsImportDir.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains("-Command") && command.contains("keytool.exe") && command.contains("importcert")
                && command.contains("cacerts") && command.contains("-file")
                && command.contains("-customArg") && command.contains("cert1.cer")
                && command.contains("cert2.cer"))
    }

    @Test
    void differentCert1ArgsUsingDefaultDir() {
        project.extensions.simple_keytool.defaultFileArgs = ["cert1.cer": ["-customArg"]]
        project.extensions.simple_keytool.defaultCertsDir = project.file("../../../../resources/test/certs")
        project.tasks.cacertsImportDir.storeType='storeType'
        var script = project.tasks.cacertsImportDir.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains("-Command") && command.contains("keytool.exe") && command.contains("importcert")
                && command.contains("cacerts") && command.contains("-file")
                && command.contains("-customArg") && command.contains("cert1.cer")
                && command.contains("cert2.cer"))
    }

    @Test
    void differentCert1ArgsNoDirectory() {
        project.extensions.simple_keytool.defaultFileArgs = ["cert1.cer": ["-customArg"]]
        project.tasks.cacertsImportDir.storeType='storeType'
        assertThrows(KeytoolNoCommandException.class) {
            project.tasks.cacertsImportDir.executeCommand()
        }
    }

    @Test
    void differentCert1Alias() {
        project.tasks.cacertsImportDir.storeType='storeType'
        project.tasks.cacertsImportDir.dir = project.file("../../../../resources/test/certs")
        project.tasks.cacertsImportDir.fileArgs = [
                "cert1.cer": ["-alias", "myalias"],
                "cert2.cer": ["-alias", "mycert2"]
        ]
        var script = project.tasks.cacertsImportDir.executeCommand()
        var command = PSCommandDecoder.decode(script)
        assertTrue(command.contains("-Command") && command.contains("keytool.exe")
                && command.contains("importcert") && command.contains("cacerts")
                && command.contains("-file") && command.contains("cert1.cer")
                && !command.contains("cert1.cer [sk]") && !command.contains("cert2.cer [sk]")
                && command.contains("myalias") && command.contains("cert2.cer")
                && command.contains("mycert2"))
    }
}
