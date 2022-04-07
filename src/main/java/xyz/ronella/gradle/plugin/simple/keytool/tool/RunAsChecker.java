package xyz.ronella.gradle.plugin.simple.keytool.tool;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Paths;

public final class RunAsChecker {

    private RunAsChecker() {}

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

