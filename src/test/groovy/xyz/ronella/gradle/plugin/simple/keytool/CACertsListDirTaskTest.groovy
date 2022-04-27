package xyz.ronella.gradle.plugin.simple.keytool

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

class CACertsListDirTaskTest {
    private Project project

    @BeforeEach
    void initProject() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'xyz.ronella.simple-keytool'
        project.extensions.simple_keytool.noop = true
    }

    @Test
    void noCommandParam() {
        assertThrows(KeytoolNoCommandException.class, { project.tasks.cacertsListDir.executeCommand() })
    }

    @Test
    void withVerboseParam() {
        project.tasks.cacertsListDir.verbose = true
        project.tasks.cacertsListDir.dir = project.file("../../../../resources/test/certs")
        var script = project.tasks.cacertsListDir.executeCommand()
        assertTrue(!script.contains("-Command") && script.contains("keytool.exe") && script.contains("list")
                && script.contains("cacerts") && script.contains("-v")
                && script.contains("cert1.cer") && script.contains("cert2.cer"))
    }

    @Test
    void withStoreTypeParam() {
        project.tasks.cacertsListDir.storeType = 'storeType'
        project.tasks.cacertsListDir.dir = project.file("../../../../resources/test/certs")
        var script = project.tasks.cacertsListDir.executeCommand()
        assertTrue(!script.contains("-Command") && script.contains("keytool.exe") && script.contains("list")
                && script.contains("cacerts") && script.contains("cert1.cer [sk]")
                && script.contains("cert2.cer [sk]"))
    }
}