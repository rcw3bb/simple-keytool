package xyz.ronella.gradle.plugin.simple.keytool.tool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandOutputFilterTest {

    @Test
    public void commandWithStorepassArg() {
        var input = "C:\\Program Files\\OpenJDK\\jdk-17.0.1\\bin\\keytool.exe -list -cacerts -storepass dummy";
        var expectation = "C:\\Program Files\\OpenJDK\\jdk-17.0.1\\bin\\keytool.exe -list -cacerts -storepass ***";
        var output = CommandOutputFilter.filter(input);
        assertEquals(expectation, output);
    }

    @Test
    public void commandWithStorepassBetweenArgs() {
        var input = "C:\\Program Files\\OpenJDK\\jdk-17.0.1\\bin\\keytool.exe -list -cacerts -storepass dummy -v";
        var expectation = "C:\\Program Files\\OpenJDK\\jdk-17.0.1\\bin\\keytool.exe -list -cacerts -storepass *** -v";
        var output = CommandOutputFilter.filter(input);
        assertEquals(expectation, output);
    }

    @Test
    public void commandWithoutStorepassArg() {
        var input = "C:\\Program Files\\OpenJDK\\jdk-17.0.1\\bin\\keytool.exe -list -cacerts";
        var output = CommandOutputFilter.filter(input);
        assertEquals(input, output);
    }

}
