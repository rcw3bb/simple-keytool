package xyz.ronella.gradle.plugin.simple.keytool.tool;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * The decoder of the PS Script.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public final class PSCommandDecoder {

    private PSCommandDecoder() {}

    /**
     * Decode the script.
     *
     * @param script The encoded script.
     * @return The decoded script.
     */
    public static String decode(final String script) {

        var base64Command = "";
        final var pattern = ".*-EncodedCommand\\s(.*)";
        final var compiledPattern = Pattern.compile(pattern);
        final var matcher = compiledPattern.matcher(script);

        if (matcher.find()) {
            base64Command = matcher.group(1);
        }

        final var decodedBytes = Base64.getDecoder().decode(base64Command);

        return new String(decodedBytes, StandardCharsets.UTF_16LE);
    }
}
