package xyz.ronella.gradle.plugin.simple.keytool.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

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

    private static void filterIndividualContent(List<String> args) {
        for (var idx=0; idx<args.size(); idx++) {
            if (REGISTRY_FILTERS.contains(args.get(idx).toLowerCase())) {
                args.set(idx+1, "***");
            }
        }
    }

    private static void filterWithinText(List<String> args) {
        for (var filter : REGISTRY_FILTERS) {
            var pattern = String.format(".*[\"']%s[\"']\\s([\"'].*?[\"']).*", filter);
            var compiledPattern = Pattern.compile(pattern);
            for (var idx=0; idx<args.size(); idx++) {
                var text = args.get(idx);
                var matcher = compiledPattern.matcher(text);
                if (matcher.find()) {
                    var newText = text.replace(matcher.group(1), "***");
                    args.set(idx, newText);
                }
            }
        }
    }

    private static String updateText(String text) {
        for (var filter : REGISTRY_FILTERS) {
            var pattern = String.format(".*[\"']%s[\"']\\s([\"'].*?[\"']).*", filter);
            var compiledPattern = Pattern.compile(pattern);
            var matcher = compiledPattern.matcher(text);
            while (matcher.find()) {
                text = text.replace(matcher.group(1), "***");
                matcher = compiledPattern.matcher(text);
            }
        }
        return text;
    }

    private static String removePrefix(String text) {
        var newText = new StringBuilder();
        var scanner = new Scanner(text);
        var tracker = 0;
        while (scanner.hasNextLine()) {
            var line = scanner.nextLine();
            if (line.matches("&.*")) {
                if (tracker>0) {
                    newText.append("\n");
                }
                newText.append(line.replaceFirst("^&\\s", ""));
                tracker++;
            }
        }
        return newText.toString();
    }

    private static void filterEncodedCommand(List<String> args) {
        var localArgs = new ArrayList<String>();
        var encodedIdx = -1;
        for (var idx=0; idx<args.size(); idx++) {
            var value = args.get(idx);
            if (value.contains("-EncodedCommand")) {
                encodedIdx = idx+1;
                localArgs.add(value);
                localArgs.add(args.get(encodedIdx));
            }
        }

        if (encodedIdx>-1) {
            args.clear();
            var decodedCommand = PSCommandDecoder.decode(String.join(" ", localArgs));
            var text = updateText(decodedCommand);
            var newText = removePrefix(text);
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
        var localArgs = new ArrayList<>(args);

        filterIndividualContent(localArgs);
        filterWithinText(localArgs);
        filterEncodedCommand(localArgs);

        return String.join(delim, localArgs);
    }

}
