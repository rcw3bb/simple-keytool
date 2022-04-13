package xyz.ronella.gradle.plugin.simple.keytool.tool;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Paths;

/**
 * The class that check if gradle is already running in admin mode.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public final class RunAsChecker {

    private RunAsChecker() {}

    /**
     * Check if running in elevated mode.
     *
     * @return True when running in elevated mode.
     */
    public static boolean isElevatedMode() {
        String pid = ManagementFactory.getRuntimeMXBean().getName().replace("@", "-");
        String fileName = String.format("runas-checker-%s.dummy", pid);
        File file = Paths.get(System.getenv("SystemRoot"), fileName).toFile();
        try {
            if (file.createNewFile()) {
                file.delete();
                return true;
            }
        } catch (IOException e) {
            if ("Access is denied".equalsIgnoreCase(e.getMessage())) {
                return false;
            }
            else {
                e.printStackTrace(System.err);
            }
        }
        return false;
    }

}

