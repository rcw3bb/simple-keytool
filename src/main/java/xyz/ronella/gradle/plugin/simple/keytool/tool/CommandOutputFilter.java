package xyz.ronella.gradle.plugin.simple.keytool.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * The class the filters the output of the command.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public final class CommandOutputFilter {

    private final static List<String> REGISTRY_FILTERS;

    static {
        REGISTRY_FILTERS = new ArrayList<>();
        REGISTRY_FILTERS.add("-storepass");
        REGISTRY_FILTERS.add("-keypass");
    }

    private CommandOutputFilter() {}

    /**
     * Holds the logic that filters the output of the command.
     *
     * @param args The arguments to build the command from.
     * @return The filtered command output.
     */
    public static String filter(final List<String> args) {
        return filter(args, " ");
    }

    private static void filterIndividualContent(final List<String> args) {
        for (var idx=0; idx<args.size(); idx++) {
            if (REGISTRY_FILTERS.contains(args.get(idx).toLowerCase(Locale.ROOT))) {
                args.set(idx+1, "***");
            }
        }
    }

    private static void filterWithinText(final List<String> args) {
        for (final var filter : REGISTRY_FILTERS) {
            final var pattern = String.format(".*[\"']%s[\"']\\s([\"'].*?[\"']).*", filter);
            final var compiledPattern = Pattern.compile(pattern);
            for (var idx=0; idx<args.size(); idx++) {
                final var text = args.get(idx);
                final var matcher = compiledPattern.matcher(text);
                if (matcher.find()) {
                    final var newText = text.replace(matcher.group(1), "***");
                    args.set(idx, newText);
                }
            }
        }
    }

    private static String updateText(final String text) {
        var newText = text;
        for (final var filter : REGISTRY_FILTERS) {
            final var pattern = String.format(".*[\"']%s[\"']\\s([\"'].*?[\"']).*", filter);
            final var compiledPattern = Pattern.compile(pattern);
            var matcher = compiledPattern.matcher(newText);
            while (matcher.find()) {
                newText = newText.replace(matcher.group(1), "***");
                matcher = compiledPattern.matcher(newText);
            }
        }
        return newText;
    }

    private static String removePrefix(final String text) {
        final var newText = new StringBuilder();
        try(var scanner = new Scanner(text)) {
            var tracker = 0;
            while (scanner.hasNextLine()) {
                final var line = scanner.nextLine();
                if (line.matches("&.*")) {
                    if (tracker > 0) {
                        newText.append('\n');
                    }
                    newText.append(line.replaceFirst("^&\\s", ""));
                    tracker++;
                }
            }
        }
        return newText.toString();
    }

    private static void filterEncodedCommand(final List<String> args) {
        final var localArgs = new ArrayList<String>();
        var encodedIdx = -1;
        for (var idx=0; idx<args.size(); idx++) {
            final var value = args.get(idx);
            if (value.contains("-EncodedCommand")) {
                encodedIdx = idx+1;
                localArgs.add(value);
                localArgs.add(args.get(encodedIdx));
            }
        }

        if (encodedIdx>-1) {
            args.clear();
            final var decodedCommand = PSCommandDecoder.decode(String.join(" ", localArgs));
            final var text = updateText(decodedCommand);
            final var newText = removePrefix(text);
            args.add(newText);
        }
    }

    /**
     * Holds the logic that filters the output of the command.
     *
     * @param args The arguments to build the command from.
     * @param delim The delimiter to use to combine the arguments.
     * @return The filtered command output.
     */
    public static String filter(final List<String> args, final String delim) {
        final var localArgs = new ArrayList<>(args);

        filterIndividualContent(localArgs);
        filterWithinText(localArgs);
        filterEncodedCommand(localArgs);

        return String.join(delim, localArgs);
    }

}
