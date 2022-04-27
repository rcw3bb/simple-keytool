package xyz.ronella.gradle.plugin.simple.keytool.tool;

import java.util.ArrayList;
import java.util.List;

/**
 * The class the filters the output of the command.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public final class CommandOutputFilter {

    private CommandOutputFilter() {}

    private final static List<String> REGISTRY_FILTERS;

    static {
        REGISTRY_FILTERS = new ArrayList<>();
        REGISTRY_FILTERS.add("-storepass");
        REGISTRY_FILTERS.add("-keypass");
    }

    /**
     * Holds the logic that filters the output of the command.
     *
     * @param args The arguments to build the command from.
     * @return The filtered command output.
     */
    public static String filter(final List<String> args) {
        return filter(args, " ");
    }

    /**
     * Holds the logic that filters the output of the command.
     *
     * @param args The arguments to build the command from.
     * @param delim The delimiter to use to combine the arguments.
     * @return The filtered command output.
     */
    public static String filter(final List<String> args, final String delim) {

        for (var idx=0; idx<args.size(); idx++) {
            if (REGISTRY_FILTERS.contains(args.get(idx).toLowerCase())) {
                args.set(idx+1, "***");
            }
        }

        return String.join(delim, args);
    }

}
