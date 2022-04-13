package xyz.ronella.gradle.plugin.simple.keytool.tool;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandOutputFilterTest {

    @Test
    public void commandWithStorepassArg() {
        var input = Arrays.asList("keytool.exe -list -cacerts -storepass dummy".split("\\s"));
        var expectation = "keytool.exe -list -cacerts -storepass ***";
        var output = CommandOutputFilter.filter(input);
        assertEquals(expectation, output);
    }

    @Test
    public void commandWithStorepassBetweenArgs() {
        var input = Arrays.asList("keytool.exe -list -cacerts -storepass dummy -v".split("\\s"));
        var expectation = "keytool.exe -list -cacerts -storepass *** -v";
        var output = CommandOutputFilter.filter(input);
        assertEquals(expectation, output);
    }

    @Test
    public void commandWithKeypassBetweenArgs() {
        var input = Arrays.asList("keytool.exe -list -cacerts -keypass dummy -v".split("\\s"));
        var expectation = "keytool.exe -list -cacerts -keypass *** -v";
        var output = CommandOutputFilter.filter(input);
        assertEquals(expectation, output);
    }

    @Test
    public void commandWithoutStorepassArg() {
        var input = Arrays.asList("keytool.exe -list -cacerts".split("\\s"));
        var output = CommandOutputFilter.filter(input);
        assertEquals(String.join(" ", input), output);
    }

    @Test
    public void commandWithStorepassDoubleQuote() {
        var input = Arrays.asList("keytool.exe","-list","-cacerts","-storepass","\"dummy asdf\"","-v");
        var expectation = "keytool.exe -list -cacerts -storepass *** -v";
        var output = CommandOutputFilter.filter(input);
        assertEquals(expectation, output);
    }

    @Test
    public void commandWithBothStorepassAndKeypass() {
        var input = Arrays.asList("keytool.exe","-list","-cacerts","-storepass","\"dummy asdf\"","-keypass","\"dummy keypass\"","-v");
        var expectation = "keytool.exe -list -cacerts -storepass *** -keypass *** -v";
        var output = CommandOutputFilter.filter(input);
        assertEquals(expectation, output);
    }
}
