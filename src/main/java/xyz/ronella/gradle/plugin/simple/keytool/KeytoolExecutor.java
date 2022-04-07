package xyz.ronella.gradle.plugin.simple.keytool;

import xyz.ronella.gradle.plugin.simple.keytool.tool.CommandRunner;
import xyz.ronella.gradle.plugin.simple.keytool.tool.OSType;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class KeytoolExecutor {

    public static final String BIN_DIR = "bin";
    public static final String EXECUTABLE = "keytool.exe";

    private final List<Supplier<File>> executables;
    private final File javaHome;
    private final OSType osType;
    private final boolean isNoop;
    private final String command;
    private final List<String> args;
    private final List<String> zArgs;
    private final boolean isAdminMode;

    private KeytoolExecutor(KeytoolExecutorBuilder builder) {
        executables = new ArrayList<>();
        javaHome = builder.javaHome;
        osType = builder.osType;
        isNoop = builder.isNoop;
        command = builder.command;
        args = builder.args;
        zArgs = builder.zArgs;
        isAdminMode = builder.isAdminMode;

        prepareExecutables();
    }

    private File getExecutableAuto() {
        var sbFQFN = new StringBuilder();
        CommandRunner.runCommand((___output, ___error) -> sbFQFN.append(___output),"where", EXECUTABLE);

        var fqfn = sbFQFN.toString();

        if (fqfn.length() > 0) {
            fqfn = fqfn.split("\\r\\n")[0]; //Just the first valid entry of where.
            File fileExec = new File(fqfn);
            if (fileExec.exists()) {
                return fileExec;
            }
        }

        return null;
    }

    private void prepareExecutables() {
        Consumer<Supplier<File>> addExecLogic = executables::add;

        Optional.ofNullable(javaHome).ifPresent(___javaHome ->
                addExecLogic.accept(() -> {
                    System.out.printf("manual=%s%n", ___javaHome);
                    return Paths.get(___javaHome.getAbsolutePath(), BIN_DIR, EXECUTABLE).toFile();
                }));

        Optional.ofNullable(System.getenv("JAVA_HOME")).ifPresent(___javaHome ->
                addExecLogic.accept(() -> {
                    System.out.printf("JAVA_HOME=%s%n", ___javaHome);
                    return Paths.get(___javaHome, BIN_DIR, EXECUTABLE).toFile();
                }));

        if (executables.size()==0) {
            Optional.ofNullable(getExecutableAuto()).ifPresent(___executable -> executables.add(() -> ___executable));
        }
    }

    private Optional<File> executable() {
        if (OSType.Windows.equals(osType)) {
            var executable = executables.stream().filter(___execLogic -> ___execLogic.get().exists()).findFirst();
            if (executable.isPresent()) {
                var exec = executable.get();
                return Optional.of(exec.get());
            }
        }
        else {
            System.err.printf("%s OS is required.%n", OSType.Windows);
        }

        throw new KeytoolExecutableException();
    }

    private List<String> prepareCommand(File keytoolExecutable) {
        return prepareCommand(keytoolExecutable, new ArrayList<>());
    }

    private List<String> getPowershellCommand() {
        var shellCommand = new ArrayList<String>();
        shellCommand.add("powershell.exe");
        shellCommand.add("-NoProfile");
        shellCommand.add("-InputFormat");
        shellCommand.add("None");
        shellCommand.add("-ExecutionPolicy") ;
        shellCommand.add("Bypass");
        shellCommand.add("-Command");

        return new ArrayList<>(shellCommand);
    }

    private String quadQuote(String text) {
        return String.format("\"\"\"\"%s\"\"\"\"", text);
    }

    private List<String> adminModeCommand(String executable, List<String> allArgs) {
        var fullCommand = getPowershellCommand();

        var sbArgs = new StringBuilder();
        allArgs.forEach(___arg -> sbArgs.append(sbArgs.length()>0 ? ",": "").append(quadQuote(___arg)));

        var sbActualCommand = String.format("\"Start-Process %s -Wait -Verb runas%s%s\"",
                quadQuote(executable), (sbArgs.length() == 0 ? "" : " -argumentlist "), sbArgs);

        fullCommand.add(sbActualCommand);
        return fullCommand;
    }

    private List<String> prepareCommand(File keytoolExecutable, List<String> packageInfo) {
        var executable = keytoolExecutable.getAbsolutePath();
        var allArgs = new ArrayList<String>();
        var fullCommand = new ArrayList<String>();

        Optional.ofNullable(command).ifPresent(allArgs::add);
        allArgs.addAll(packageInfo);
        allArgs.addAll(args);
        allArgs.addAll(zArgs);

        var commandToRun = new ArrayList<String>();
        commandToRun.add(executable);
        commandToRun.addAll(allArgs);

        System.out.println(String.join(" ", commandToRun));

        if (isAdminMode) {
            fullCommand.addAll(adminModeCommand(executable, allArgs));
        } else {
            fullCommand.add(executable);
            fullCommand.addAll(allArgs);
        }
        return fullCommand;
    }

    public String execute() {
        var sbCommand = new StringBuilder();
        executable().ifPresent(___executable -> {
            var fullCommand = prepareCommand(___executable);
            sbCommand.append(String.join(" ", fullCommand).trim());

            if (!isNoop) {
                CommandRunner.runCommand((___output, ___error)-> {
                    if (___error.length()>0) {
                        System.err.println(___error);
                    }
                    else {
                        System.out.println(___output);
                    }
                }, fullCommand.toArray(new String[]{}));
            }
        });
        return sbCommand.toString();
    }

    public static KeytoolExecutorBuilder getBuilder() {
        return new KeytoolExecutorBuilder();
    }

    public final static class KeytoolExecutorBuilder {
        private final List<String> args;
        private final List<String> zArgs;
        private boolean isAdminMode;
        private String command;
        private boolean isNoop;
        private OSType osType;
        private File javaHome;

        private KeytoolExecutorBuilder() {
            args = new ArrayList<>();
            zArgs = new ArrayList<>();
        }

        public KeytoolExecutor build() {
            return new KeytoolExecutor(this);
        }

        public KeytoolExecutorBuilder addJavaHome(File javaHome) {
            this.javaHome = javaHome;
            return this;
        }

        public KeytoolExecutorBuilder addOSType(OSType osType) {
            this.osType = osType;
            return this;
        }

        public KeytoolExecutorBuilder addIsNoop(boolean isNoop) {
            this.isNoop = isNoop;
            return this;
        }

        public KeytoolExecutorBuilder addCommand(String command) {
            this.command = command;
            return this;
        }

        public KeytoolExecutorBuilder addArgs(String ... args) {
            this.args.addAll(Arrays.asList(args));
            return this;
        }

        public KeytoolExecutorBuilder addZArgs(String ... zArgs) {
            this.zArgs.addAll(Arrays.asList(zArgs));
            return this;
        }

        public KeytoolExecutorBuilder addIsAdminMode(boolean isAdminMode) {
            this.isAdminMode = isAdminMode;
            return this;
        }
    }
}
