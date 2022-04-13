package xyz.ronella.gradle.plugin.simple.keytool;

import xyz.ronella.gradle.plugin.simple.keytool.tool.CommandOutputFilter;
import xyz.ronella.gradle.plugin.simple.keytool.tool.CommandRunner;
import xyz.ronella.gradle.plugin.simple.keytool.tool.OSType;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * The main class that actually execute the keytool command.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public final class KeytoolExecutor {

    /**
     * The default binary directory.
     */
    public static final String BIN_DIR = "bin";

    /**
     * The keytool executable file.
     */
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
        isAdminMode = !builder.runningInAdminMode && builder.isAdminMode;

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
                addExecLogic.accept(() -> Paths.get(___javaHome.getAbsolutePath(), BIN_DIR, EXECUTABLE).toFile()));

        Optional.ofNullable(System.getenv("JAVA_HOME")).ifPresent(___javaHome ->
                addExecLogic.accept(() -> Paths.get(___javaHome, BIN_DIR, EXECUTABLE).toFile()));

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

    private List<String> getPowershellCommand() {
        var shellCommand = new ArrayList<String>();
        shellCommand.add("powershell.exe");
        shellCommand.add("-NoProfile");
        shellCommand.add("-InputFormat");
        shellCommand.add("None");
        shellCommand.add("-ExecutionPolicy") ;
        shellCommand.add("Bypass");
        shellCommand.add("-EncodedCommand");

        return new ArrayList<>(shellCommand);
    }

    private List<String> getScriptLines() {
        var script = new ArrayList<String>();
        script.add("$ProgressPreference = 'SilentlyContinue'");
        return new ArrayList<>(script);
    }

    private String quote(String text) {
        return String.format("\"%s\"", text);
    }

    private List<String> adminModeCommand(String executable, List<String> allArgs) {
        var fullCommand = getPowershellCommand();

        var sbArgs = new StringBuilder();
        allArgs.forEach(___arg -> sbArgs.append(sbArgs.length()>0 ? ",": "").append(quote(___arg)));

        var sbActualCommand = String.format("(Start-Process %s -Wait -WindowStyle Hidden -PassThru -Verb RunAs%s%s).ExitCode",
                quote(executable), (sbArgs.length() == 0 ? "" : " -argumentlist "), sbArgs);

        var command = String.join(" ", sbActualCommand);
        var scriptLines = getScriptLines();
        scriptLines.add(command);
        var script = String.join("\n", scriptLines);

        var encodedCommand = Base64.getEncoder().encodeToString(script.getBytes(StandardCharsets.UTF_16LE));

        fullCommand.add(encodedCommand);
        return fullCommand;
    }

    private List<String> prepareCommand(File keytoolExecutable) {
        var executable = keytoolExecutable.getAbsolutePath();
        var allArgs = new ArrayList<String>();
        var fullCommand = new ArrayList<String>();

        Optional.ofNullable(command).ifPresent(allArgs::add);

        if (args.size()>0) {
            allArgs.addAll(args);
        }

        if (zArgs.size()>0) {
            allArgs.addAll(zArgs);
        }

        var commandToRun = new ArrayList<String>();
        commandToRun.add(executable.contains(" ") ? quote(executable) : executable);
        commandToRun.addAll(allArgs.stream()
                .map(___arg -> ___arg.contains(" ") ? quote(___arg) : ___arg)
                .collect(Collectors.toList())
        );

        System.out.println(CommandOutputFilter.filter(commandToRun));

        if (isAdminMode) {
            fullCommand.addAll(adminModeCommand(executable, allArgs));
        } else {
            fullCommand.add(executable);
            fullCommand.addAll(allArgs);
        }
        return fullCommand;
    }

    /**
     * Do the actual execution of the command.
     *
     * @return The command executed.
     */
    public String execute() {
        var sbCommand = new StringBuilder();
        executable().ifPresent(___executable -> {
            var fullCommand = prepareCommand(___executable);
            sbCommand.append(String.join(" ", fullCommand).trim());

            if (!isNoop) {
                CommandRunner.runCommand((___output, ___error)-> {
                    if (___error.length()>0) {
                        System.err.println(___error);
                        if (isAdminMode) {
                            throw new KeytoolTaskExecutionException();
                        }
                    }
                    else {
                        if (isAdminMode) {
                            if (!"0".equals(___output)) {
                                throw new KeytoolTaskExecutionException();
                            }
                        }
                        else {
                            System.out.println(___output);
                        }
                    }
                }, fullCommand.toArray(new String[]{}));
            }
        });
        return sbCommand.toString();
    }

    /**
     * Creates the builder class of the KeytoolExecutor.
     *
     * @return An instance of KeytoolExecutorBuilder.
     */
    public static KeytoolExecutorBuilder getBuilder() {
        return new KeytoolExecutorBuilder();
    }

    /**
     * The only class that create an instance of KeytoolExecutor.
     */
    public final static class KeytoolExecutorBuilder {
        private final List<String> args;
        private final List<String> zArgs;
        private boolean isAdminMode;
        private String command;
        private boolean isNoop;
        private OSType osType;
        private File javaHome;
        private boolean runningInAdminMode;

        private KeytoolExecutorBuilder() {
            args = new ArrayList<>();
            zArgs = new ArrayList<>();
        }

        /**
         * Creates an instance of KeytoolExecutor.
         *
         * @return An instance of KeytoolExecutor.
         */
        public KeytoolExecutor build() {
            return new KeytoolExecutor(this);
        }

        /**
         * Add the java installation directory.
         *
         * @param javaHome The valid java installation directory.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addJavaHome(File javaHome) {
            this.javaHome = javaHome;
            return this;
        }

        /**
         * Add of the value of the OSType.
         *
         * @param osType An instance of OSType.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addOSType(OSType osType) {
            this.osType = osType;
            return this;
        }

        /**
         * Instruct the executor not actually execute the command but just display it.
         *
         * @param isNoop Pass in true not execute the command.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addNoop(boolean isNoop) {
            this.isNoop = isNoop;
            return this;
        }

        /**
         * Add the command to run.
         *
         * @param command The command to run.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addCommand(String command) {
            this.command = command;
            return this;
        }

        /**
         * Add the arguments for the command.
         *
         * @param args The arguments for the command.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addArgs(String ... args) {
            this.args.addAll(Arrays.asList(args));
            return this;
        }

        /**
         * Add a terminal arguments that are added as the last arguments for the command.
         *
         * @param zArgs The terminal arguments.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addZArgs(String ... zArgs) {
            this.zArgs.addAll(Arrays.asList(zArgs));
            return this;
        }

        /**
         * Indicates that the command will be run in administration mode if it is
         * not running yet in administration mode.
         *
         * @param isAdminMode Set to true to run the command in administration mode.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addAdminMode(boolean isAdminMode) {
            this.isAdminMode = isAdminMode;
            return this;
        }

        /**
         * Indicates that gradle is already running in administration mode.
         *
         * @param runningInAdminMode Set to true it the task is already running in administration mode.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addRunningInAdminMode(boolean runningInAdminMode) {
            this.runningInAdminMode = runningInAdminMode;
            return this;
        }
    }
}
