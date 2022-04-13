package xyz.ronella.gradle.plugin.simple.keytool.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * The class that do the actual running of the command.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public final class CommandRunner {

    /**
     * The output information after running the command.
     */
    public enum Output {

        /**
         * The standard message by the executed command.
         */
        STD,

        /**
         * The error message by the executed command.
         */
        ERR
    }

    private CommandRunner() {}

    /**
     * Run the command received.
     *
     * @param outputSet Holds logic for handling the exitCode, output and error informations.
     * @param command The command received.
     */
    public static void runCommand(BiConsumer<Integer, Map<Output, String>> outputSet, String ... command) {
        try {
            var process = new ProcessBuilder(command).start();
            var output = new BufferedReader(new InputStreamReader(process.getInputStream()));
            var error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            var outputStr = output.lines().collect(Collectors.joining("\n"));
            var errorStr = error.lines().collect(Collectors.joining("\n"));
            var outputResult = Map.of(Output.STD, outputStr, Output.ERR, errorStr);
            Optional.ofNullable(outputSet).ifPresent(___outputSet -> ___outputSet.accept(process.exitValue(), outputResult));
        }
        catch(IOException ioe) {
            ioe.printStackTrace(System.err);
        }
    }

}
