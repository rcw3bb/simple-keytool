package xyz.ronella.gradle.plugin.simple.keytool.tool;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

    @Test
    public void commandWithStorepassInStringEmbedded() {
        var input = Arrays.asList("\"C:\\Program Files\\OpenJDK\\jdk-17.0.1\\bin\\keytool.exe\" \"-list\" \"-cacerts\" \"-storetype\" \"storeType\" \"-storepass\" \"changeit\" \"-alias\" \"cert1.cer [sk]\"");
        var expectation = "\"C:\\Program Files\\OpenJDK\\jdk-17.0.1\\bin\\keytool.exe\" \"-list\" \"-cacerts\" \"-storetype\" \"storeType\" \"-storepass\" *** \"-alias\" \"cert1.cer [sk]\"";
        var output = CommandOutputFilter.filter(input);
        assertEquals(expectation, output);
    }

    @Test
    public void commandWithStorepassInString() {
        var input = Arrays.asList("\"C:\\Program Files\\OpenJDK\\jdk-17.0.1\\bin\\keytool.exe\" \"-list\" \"-cacerts\" \"-storetype\" \"storeType\" \"-storepass\" \"changeit\"");
        var expectation = "\"C:\\Program Files\\OpenJDK\\jdk-17.0.1\\bin\\keytool.exe\" \"-list\" \"-cacerts\" \"-storetype\" \"storeType\" \"-storepass\" ***";
        var output = CommandOutputFilter.filter(input);
        assertEquals(expectation, output);
    }

    @Test
    public void commandWithEncodedCommand() {
        var input = Arrays.asList("-EncodedCommand","JABQAHIAbwBnAHIAZQBzAHMAUAByAGUAZgBlAHIAZQBuAGMAZQAgAD0AIAAnAFMAaQBsAGUAbgB0AGwAeQBDAG8AbgB0AGkAbgB1AGUAJwAKAEUAeABpAHQAIAAoAFMAdABhAHIAdAAtAFAAcgBvAGMAZQBzAHMAIAAiAHAAbwB3AGUAcgBzAGgAZQBsAGwALgBlAHgAZQAiACAALQBXAGEAaQB0ACAALQBQAGEAcwBzAFQAaAByAHUAIAAtAFYAZQByAGIAIABSAHUAbgBBAHMAIAAtAGEAcgBnAHUAbQBlAG4AdABsAGkAcwB0ACAAIgAiACIALQBOAG8AUAByAG8AZgBpAGwAZQAiACIAIgAsACIAIgAiAC0ASQBuAHAAdQB0AEYAbwByAG0AYQB0ACIAIgAiACwAIgAiACIATgBvAG4AZQAiACIAIgAsACIAIgAiAC0ARQB4AGUAYwB1AHQAaQBvAG4AUABvAGwAaQBjAHkAIgAiACIALAAiACIAIgBCAHkAcABhAHMAcwAiACIAIgAsACIAIgAiAC0AQwBvAG0AbQBhAG4AZAAiACIAIgAsAHsADQAKACYAIAAnAEMAOgBcAFAAcgBvAGcAcgBhAG0AIABGAGkAbABlAHMAXABPAHAAZQBuAEoARABLAFwAagBkAGsALQAxADcALgAwAC4AMQBcAGIAaQBuAFwAawBlAHkAdABvAG8AbAAuAGUAeABlACcAIAAnAC0AaQBtAHAAbwByAHQAYwBlAHIAdAAnACAAJwAtAGMAYQBjAGUAcgB0AHMAJwAgACcALQB2ACcAIAAnAC0AcwB0AG8AcgBlAHAAYQBzAHMAJwAgACcAYwBoAGEAbgBnAGUAaQB0ACcAIAAnAC0AYQBsAGkAYQBzACcAIAAnAGMAZQByAHQAMQAuAGMAZQByACAAWwBzAGsAXQAnACAAJwAtAGYAaQBsAGUAJwAgACcAQwA6AFwAZABlAHYAXABjAG8AZABlAHMAXABzAGkAbQBwAGwAZQAtAGsAZQB5AHQAbwBvAGwAXABiAHUAaQBsAGQAXAByAGUAcwBvAHUAcgBjAGUAcwBcAHQAZQBzAHQAXABjAGUAcgB0AHMAXABjAGUAcgB0ADEALgBjAGUAcgAnAAoAJgAgACcAQwA6AFwAUAByAG8AZwByAGEAbQAgAEYAaQBsAGUAcwBcAE8AcABlAG4ASgBEAEsAXABqAGQAawAtADEANwAuADAALgAxAFwAYgBpAG4AXABrAGUAeQB0AG8AbwBsAC4AZQB4AGUAJwAgACcALQBpAG0AcABvAHIAdABjAGUAcgB0ACcAIAAnAC0AYwBhAGMAZQByAHQAcwAnACAAJwAtAHYAJwAgACcALQBzAHQAbwByAGUAcABhAHMAcwAnACAAJwBjAGgAYQBuAGcAZQBpAHQAJwAgACcALQBhAGwAaQBhAHMAJwAgACcAYwBlAHIAdAAyAC4AYwBlAHIAIABbAHMAawBdACcAIAAnAC0AZgBpAGwAZQAnACAAJwBDADoAXABkAGUAdgBcAGMAbwBkAGUAcwBcAHMAaQBtAHAAbABlAC0AawBlAHkAdABvAG8AbABcAGIAdQBpAGwAZABcAHIAZQBzAG8AdQByAGMAZQBzAFwAdABlAHMAdABcAGMAZQByAHQAcwBcAGMAZQByAHQAMgAuAGMAZQByACcADQAKAH0AKQAuAEUAeABpAHQAQwBvAGQAZQA=");
        var output = CommandOutputFilter.filter(input);
        assertFalse(output.contains("changeit"));
    }
}

