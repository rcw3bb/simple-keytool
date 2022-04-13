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

    /**
     * Decode the script.
     *
     * @param script The encoded script.
     * @return The decoded script.
     */
    public static String decode(String script) {

        var base64Command = "";
        var pattern = ".*-EncodedCommand\\s(.*)";
        var compiledPattern = Pattern.compile(pattern);
        var matcher = compiledPattern.matcher(script);

        if (matcher.find()) {
            base64Command = matcher.group(1);
        }

        var decodedBytes = Base64.getDecoder().decode(base64Command);

        return new String(decodedBytes, StandardCharsets.UTF_16LE);
    }
}
