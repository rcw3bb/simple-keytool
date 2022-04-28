package xyz.ronella.gradle.plugin.simple.keytool;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import xyz.ronella.gradle.plugin.simple.keytool.tool.OSType;
import xyz.ronella.gradle.plugin.simple.keytool.tool.PSCommandDecoder;

import java.nio.file.Paths;
import java.util.function.Supplier;

public class KeytoolExecutorTest {

    private final Supplier<KeytoolExecutor.KeytoolExecutorBuilder> winExecutor = () -> KeytoolExecutor.getBuilder()
            .addNoop(true)
            .addOSType(OSType.Windows);

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testWinCommand() {
        var executor = winExecutor.get()
                .addCommand("command")
                .build();

        assertTrue(executor.execute().endsWith("keytool.exe command"));
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testWinCommandSingleArg() {
        var executor = winExecutor.get()
                .addCommand("command")
                .addArgs("arg1")
                .build();

        assertTrue(executor.execute().endsWith("keytool.exe command arg1"));
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testWinCommandMultipleArgs() {
        var executor = winExecutor.get()
                .addCommand("command")
                .addArgs("arg1", "arg2")
                .build();

        assertTrue(executor.execute().endsWith("keytool.exe command arg1 arg2"));
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testWinCommandSingleZArg() {
        var executor = winExecutor.get()
                .addCommand("command")
                .addZArgs("zarg1")
                .addArgs("arg1", "arg2")
                .build();

        assertTrue(executor.execute().endsWith("keytool.exe command arg1 arg2 zarg1"));
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testWinCommandMultipleZArgs() {
        var executor = winExecutor.get()
                .addCommand("command")
                .addZArgs("zarg1", "zarg2")
                .addArgs("arg1", "arg2")
                .build();

        assertTrue(executor.execute().endsWith("keytool.exe command arg1 arg2 zarg1 zarg2"));
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testWinAdminMode() {
        var executor = winExecutor.get()
                .addCommand("command")
                .addArgs("arg1")
                .addAdminMode(true)
                .build();
        var script = executor.execute();
        var command = PSCommandDecoder.decode(script);
        assertTrue(command.contains("\"\"\"command\"\"\",\"\"\"arg1\"\"\")"));
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testScriptCommandGeneratedAdminMode() {
        var executor = winExecutor.get()
                .addCommand("-importcert")
                .addArgs("arg1")
                .addAdminMode(true)
                .addScriptMode(true)
                .addDirectory(Paths.get(".",  "src", "test", "resources", "certs").toFile())
                .build();
        var script = executor.execute();
        var command = PSCommandDecoder.decode(script);
        assertTrue(command.contains("\"\"-Command\"\""));
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testScriptCommandGeneratedNonAdminMode() {
        var executor = winExecutor.get()
                .addCommand("-importcert")
                .addArgs("arg1")
                .addScriptMode(true)
                .addDirectory(Paths.get(".",  "src", "test", "resources", "certs").toFile())
                .build();
        var script = executor.execute();
        assertTrue(script.contains("keytool.exe") && script.contains("cert1.cer") && script.contains("cert2.cer"));
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testNoScriptCommandGenerated() {
        var executor = winExecutor.get()
                .addCommand("-importcert")
                .addArgs("arg1")
                .addAdminMode(true)
                .addScriptMode(true)
                .build();
        assertThrows(KeytoolNoCommandException.class, executor::execute);
    }


    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testScriptCommandGeneratedAdminModeWithCustomPrefixSuffix() {
        var executor = winExecutor.get()
                .addCommand("-importcert")
                .addArgs("arg1")
                .addAdminMode(true)
                .addScriptMode(true)
                .addDirAliasPrefix("[prefix]")
                .addDirAliasSuffix("[suffix]")
                .addDirectory(Paths.get(".",  "src", "test", "resources", "certs").toFile())
                .build();
        var script = executor.execute();
        var command = PSCommandDecoder.decode(script);
        assertTrue(command.contains("\"\"-Command\"\"") && command.contains("[prefix] cert1.cer [suffix]"));
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testScriptCommandGeneratedAdminModeWithSuffixRemoved() {
        var executor = winExecutor.get()
                .addCommand("-importcert")
                .addArgs("arg1")
                .addAdminMode(true)
                .addScriptMode(true)
                .addDirAliasSuffix("")
                .addDirectory(Paths.get(".",  "src", "test", "resources", "certs").toFile())
                .build();
        var script = executor.execute();
        var command = PSCommandDecoder.decode(script);
        assertTrue(command.contains("\"\"-Command\"\"") && command.contains("cert1.cer"));
    }
}
