package xyz.ronella.gradle.plugin.simple.keytool.tool;

/**
 * The enumerator that identifies the OSType.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public enum OSType {

    /**
     * Indicates that the plugin is running on Windows.
     */
    Windows,

    /**
     * Indicates that the plugin is running on Linux.
     */
    Linux,

    /**
     * Indicates that the plugin is running on Mac.
     */
    Mac,

    /**
     * Indicates that the plugin cannot identify the OS.
     */
    Unknown;

    /**
     * Identifies the OSType.
     * @return An instance of OSType.
     */
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
