package xyz.ronella.gradle.plugin.simple.keytool;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import xyz.ronella.gradle.plugin.simple.keytool.tool.OSType;

import java.io.File;
import java.util.function.Supplier;

public class KeytoolExecutorTest {

    private Supplier<KeytoolExecutor.KeytoolExecutorBuilder> winExecutor = () -> KeytoolExecutor.getBuilder()
            .addIsNoop(true)
            .addOSType(OSType.Windows)
            .addJavaHome(new File("C:\\Program Files\\Java\\jdk-17.0.2\\"));

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testWinCommand() {
        var executor = winExecutor.get()
                .addCommand("command")
                .build();

        assertEquals("C:\\Program Files\\Java\\jdk-17.0.2\\bin\\keytool.exe command", executor.execute());
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testWinCommandSingleArg() {
        var executor = winExecutor.get()
                .addCommand("command")
                .addArgs("arg1")
                .build();

        assertEquals("C:\\Program Files\\Java\\jdk-17.0.2\\bin\\keytool.exe command arg1", executor.execute());
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testWinCommandMultipleArgs() {
        var executor = winExecutor.get()
                .addCommand("command")
                .addArgs("arg1", "arg2")
                .build();

        assertEquals("C:\\Program Files\\Java\\jdk-17.0.2\\bin\\keytool.exe command arg1 arg2", executor.execute());
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testWinCommandSingleZArg() {
        var executor = winExecutor.get()
                .addCommand("command")
                .addZArgs("zarg1")
                .addArgs("arg1", "arg2")
                .build();

        assertEquals("C:\\Program Files\\Java\\jdk-17.0.2\\bin\\keytool.exe command arg1 arg2 zarg1", executor.execute());
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testWinCommandMultipleZArgs() {
        var executor = winExecutor.get()
                .addCommand("command")
                .addZArgs("zarg1", "zarg2")
                .addArgs("arg1", "arg2")
                .build();

        assertEquals("C:\\Program Files\\Java\\jdk-17.0.2\\bin\\keytool.exe command arg1 arg2 zarg1 zarg2", executor.execute());
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testWinAdminMode() {
        var executor = winExecutor.get()
                .addCommand("command")
                .addArgs("arg1")
                .addIsAdminMode(true)
                .build();

        var expectation = "powershell.exe -NoProfile -InputFormat None -ExecutionPolicy Bypass -Command \"Start-Process \"\"\"\"C:\\Program Files\\Java\\jdk-17.0.2\\bin\\keytool.exe\"\"\"\" -Wait -Verb runas -argumentlist \"\"\"\"command\"\"\"\",\"\"\"\"arg1\"\"\"\"\"";

        assertEquals(expectation, executor.execute());
    }

}
