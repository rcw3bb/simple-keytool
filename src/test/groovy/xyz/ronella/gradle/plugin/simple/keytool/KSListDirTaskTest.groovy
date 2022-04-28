package xyz.ronella.gradle.plugin.simple.keytool

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

class KSListDirTaskTest {
    private Project project

    @BeforeEach
    void initProject() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'xyz.ronella.simple-keytool'
        project.extensions.simple_keytool.noop = true
    }

    @Test
    void noCommandParam() {
        assertThrows(KeytoolNoCommandException.class, { project.tasks.ksListDir.executeCommand() })
    }

    @Test
    void withVerboseParam() {
        project.tasks.ksListDir.verbose = true
        project.tasks.ksListDir.keyStore=project.file("keystore")
        project.tasks.ksListDir.dir = project.file("../../../../resources/test/certs")
        var script = project.tasks.ksListDir.executeCommand()
        assertTrue(!script.contains("-Command") && script.contains("keytool.exe") && script.contains("list")
                && !script.contains("cacerts") && script.contains("-v")
                && script.contains("-keystore") && script.contains("cert1.cer")
                && script.contains("cert2.cer"))
    }

    @Test
    void withStoreTypeParam() {
        project.tasks.ksListDir.storeType = 'storeType'
        project.tasks.ksListDir.keyStore=project.file("keystore")
        project.tasks.ksListDir.dir = project.file("../../../../resources/test/certs")
        var script = project.tasks.ksListDir.executeCommand()
        assertTrue(!script.contains("-Command") && script.contains("keytool.exe") && script.contains("list")
                && !script.contains("cacerts") && script.contains("cert1.cer [sk]")
                && script.contains("-keystore") && script.contains("cert2.cer [sk]"))
    }
}