package xyz.ronella.gradle.plugin.simple.keytool.tool;

/**
 * The enumerator that identifies the OSType.
 *
 * @author Ron Webb
 * @since 2020-04-11
 */
public enum OSType {
    Windows,
    Linux,
    Mac,
    Unknown;

    public static OSType identify() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return OSType.Windows;
        }
        else if (osName.contains("mac")) {
            return OSType.Mac;
        }
        else if (osName.contains("nux") || osName.contains("nix") || osName.contains("aix")) {
            return OSType.Linux;
        }
        return OSType.Unknown;
    }
}
