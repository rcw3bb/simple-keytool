package xyz.ronella.gradle.plugin.simple.keytool.tool;

import java.util.regex.Pattern;

/**
 * The class the filters the output of the command.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public final class CommandOutputFilter {

    private CommandOutputFilter() {}

    /**
     * Holds the logic that filters the output of the command.
     *
     * @param command The command received.
     * @return The filtered command output.
     */
    public static String filter(String command) {
        var pattern = ".*-storepass\\s*([^\\s]*).*";

        var compiledPattern = Pattern.compile(pattern);
        var matcher = compiledPattern.matcher(command);

        if (matcher.find()) {
            var secret = matcher.group(1);
            return matcher.group(0).replace(secret, "***");
        }

        return command;
    }

}
