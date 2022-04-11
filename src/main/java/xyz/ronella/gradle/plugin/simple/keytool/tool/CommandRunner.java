package xyz.ronella.gradle.plugin.simple.keytool.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private CommandRunner() {}

    /**
     * Run the command received.
     *
     * @param outputSet Holds the logic for the output and error streams.
     * @param command The command received.
     */
    public static void runCommand(BiConsumer<String, String> outputSet, String ... command) {
        try {
            Process process = new ProcessBuilder(command).start();
            BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String outputStr = output.lines().collect(Collectors.joining("\n"));
            String errorStr = error.lines().collect(Collectors.joining("\n"));
            Optional.ofNullable(outputSet).ifPresent(___outputSet -> ___outputSet.accept(outputStr, errorStr));
        }
        catch(IOException ioe) {
            ioe.printStackTrace(System.err);
        }
    }

}
