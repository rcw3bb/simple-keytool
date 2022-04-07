package xyz.ronella.gradle.plugin.simple.keytool;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import xyz.ronella.gradle.plugin.simple.keytool.tool.OSType;

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

        assertTrue(executor.execute().endsWith("keytool.exe\"\"\"\" -Wait -Verb runas -argumentlist \"\"\"\"command\"\"\"\",\"\"\"\"arg1\"\"\"\"\""));
    }

}
