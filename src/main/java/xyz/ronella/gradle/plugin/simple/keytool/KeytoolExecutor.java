package xyz.ronella.gradle.plugin.simple.keytool;

import xyz.ronella.command.arrays.windows.PowerShell;
import xyz.ronella.gradle.plugin.simple.keytool.tool.CommandOutputFilter;
import xyz.ronella.trivial.handy.CommandRunner;
import xyz.ronella.trivial.handy.NoCommandException;
import xyz.ronella.trivial.handy.OSType;
import xyz.ronella.trivial.handy.impl.CommandArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The main class that actually execute the keytool command.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
@SuppressWarnings({"PMD.GodClass", "PMD.SystemPrintln", "PMD.TooManyMethods", "PMD.AvoidThrowingRawExceptionTypes"})
public final class KeytoolExecutor {

    /**
     * The default binary directory.
     */
    public static final String BIN_DIR = "bin";

    /**
     * The keytool executable file.
     */
    public static final String EXEC = "keytool.exe";

    /**
     * The import certificate command.
     */
    private static final String IMPORT_CERT_CMD = "-importcert";

    private final List<Supplier<File>> executables;
    private final File javaHome;
    private final OSType osType;
    private final boolean isNoop;
    private final String command;
    private final List<String> args;
    private final List<String> zArgs;
    private final boolean isAdminMode;
    private final boolean isScriptMode;
    private final File dir;
    private final Map<String, List<String>> fileArgs;
    private final String dirAliasPrefix;
    private final String dirAliasSuffix;

    private KeytoolExecutor(final KeytoolExecutorBuilder builder) {
        executables = new ArrayList<>();
        javaHome = builder.javaHome;
        osType = builder.osType;
        isNoop = builder.isNoop;
        command = builder.command;
        args = builder.args;
        zArgs = builder.zArgs;
        dir = builder.dir;
        fileArgs = builder.fileArgs;
        isScriptMode = builder.isScriptMode;
        isAdminMode = !builder.inAdminMode && builder.isAdminMode;
        dirAliasPrefix = builder.dirAliasPrefix;
        dirAliasSuffix = builder.dirAliasSuffix;

        initExecutables();
    }

    private File getExecutableAuto() {
        final var sbFQFN = new StringBuilder();
        try {
            CommandRunner.runCommand((___output, ___error) -> {
                try(var output = new BufferedReader(new InputStreamReader(___output, "UTF-8")))  {
                    sbFQFN.append(output.lines().collect(Collectors.joining("\n")));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, "where", EXEC);
        } catch (NoCommandException e) {
            throw new RuntimeException(e);
        }

        var fqfn = sbFQFN.toString();

        File execOutput = null;

        if (fqfn.length() > 0) {
            fqfn = fqfn.split("\\r\\n")[0]; //Just the first valid entry of where.
            final File fileExec = new File(fqfn);
            if (fileExec.exists()) {
                execOutput = fileExec;
            }
        }

        return execOutput;
    }

    private void initExecutables() {
        final Consumer<Supplier<File>> addExecLogic = executables::add;

        Optional.ofNullable(javaHome).ifPresent(___javaHome ->
                addExecLogic.accept(() -> Paths.get(___javaHome.getAbsolutePath(), BIN_DIR, EXEC).toFile()));

        Optional.ofNullable(System.getenv("JAVA_HOME")).ifPresent(___javaHome ->
                addExecLogic.accept(() -> Paths.get(___javaHome, BIN_DIR, EXEC).toFile()));

        if (executables.isEmpty()) {
            Optional.ofNullable(getExecutableAuto()).ifPresent(___executable -> executables.add(() -> ___executable));
        }
    }

    private Optional<File> executable() {
        if (OSType.Windows.equals(osType)) {
            final var executable = executables.stream().filter(___execLogic -> ___execLogic.get().exists()).findFirst();
            if (executable.isPresent()) {
                final var exec = executable.get();
                return Optional.of(exec.get());
            }
        }
        else {
            System.err.printf("%s OS is required.%n", OSType.Windows);
        }

        throw new KeytoolExecutableException();
    }

    private String tripleQuote(final String text) {
        return String.format("\"\"\"%s\"\"\"", text);
    }

    private String quote(final String text) {
        return String.format("\"%s\"", text);
    }

    private String singleQuote(final String text) {
        return String.format("'%s'", text);
    }

    private void manageImportCertParam(final List<String> commands, final String ktCommand, final Path certPath) {
        if (IMPORT_CERT_CMD.equalsIgnoreCase(ktCommand)) {
            commands.add("-file");
            final var certFile = certPath.toFile().getAbsolutePath();
            commands.add(certFile);
        }
    }

    private void manageAliasParam(final List<String> commands, final String filename) {
        if (!commands.contains("-alias")) {
            commands.add("-alias");
            commands.add(String.format("%s %s %s", dirAliasPrefix, filename, dirAliasSuffix).trim());
        }
    }

    private List<String> buildCommands(final String executable, final List<String> allArgs) {
        final var commands = new ArrayList<String>();
        commands.add(executable);
        commands.addAll(allArgs);
        return commands;
    }

    private List<String> buildScriptCommand() {
        final var scriptCommand = new ArrayList<String>();
        if (isAdminMode) {
            scriptCommand.add("&");
        }
        return scriptCommand;
    }

    private List<String> createScriptCommands(final String executable, final List<String> allArgs) {
        final var scriptCommands = new ArrayList<String>();

        if (null!=dir && dir.exists()) {
            final var ktCommand = allArgs.stream().findFirst();
            try (var entries = Files.list(dir.toPath())) {
                entries.forEach(___path -> {
                    final var filename = ___path.toFile().getName();
                    final var commands = buildCommands(executable, allArgs);
                    manageFileSpecificParam(commands, filename);
                    manageImportCertParam(commands, ktCommand.orElse(""), ___path);

                    final var scriptCommand = buildScriptCommand();
                    scriptCommand.addAll(commands.stream().map(___command -> isAdminMode ? singleQuote(___command) : quote(___command)).collect(Collectors.toList()));
                    scriptCommands.add(String.join(" ", scriptCommand));
                });
            } catch (IOException e) {
                throw new KeytoolException(e.getMessage(), e);
            }
        }

        return scriptCommands;
    }

    private void manageFileSpecificParam(final List<String> commands, final String filename) {
        final var fArgs = fileArgs.getOrDefault(filename, Collections.emptyList());
        commands.addAll(fArgs);
        manageAliasParam(commands, filename);
    }

    private List<String> buildScript(final String executable, final List<String> allArgs) {
        final var psBuilder = PowerShell.getBuilder()
                .enableDefaultArgs(true)
                .suppressProgramName(true);

        final var scriptCommands = createScriptCommands(executable, allArgs);

        if (scriptCommands.isEmpty()) {
            throw new KeytoolNoCommandException("No command(s) to execute.");
        }
        else {
            psBuilder.addArg("literal:-Command")
                    .addArg(String.format("{%n%s%n}", String.join("\n", scriptCommands)));
        }

        final var powerShell = psBuilder.build();
        final var psCommand = Stream.of(powerShell.getCommand())
                .map(___arg -> ___arg.startsWith("{") ? ___arg: tripleQuote(___arg))
                .collect(Collectors.joining(","));
        return isAdminMode ? List.of(psCommand) : scriptCommands;
    }


    private List<String> adminModeScript(final List<String> commands) {
        return buildAdminCommand("powershell.exe", null, commands);
    }

    private List<String> adminModeCommand(final String executable, final List<String> allArgs) {
        return buildAdminCommand(executable, allArgs, null);
    }

    private List<String> buildAdminCommand(final String executable, final List<String> allArgs,
                                           final List<String> commands) {
        final var cmdBuilder = PowerShell.getBuilder()
                .enableDefaultArgs(true)
                .setAdminMode(true)
                .setPreferNonAdminMode(true)
                .addAdminModeHeader("$ProgressPreference = 'SilentlyContinue'");

        if (null!=allArgs) {
            cmdBuilder.setCommand(executable);
            allArgs.forEach(cmdBuilder::addArg);
        }

        if (null!=commands) {
            cmdBuilder.setRawArgs(true);
            commands.forEach(cmdBuilder::addArg);
        }

        final var adminCommand = cmdBuilder.build();

        return List.of(adminCommand.getCommand());
    }

    private List<String> commandToRun(final String executable, final List<String> allArgs) {
        final var commandToRun = CommandArray.getBuilder()
                .setCommand(executable.contains(" ") ? quote(executable) : executable)
                .addArgs(() -> !allArgs.isEmpty(), allArgs.stream()
                        .map(___arg -> ___arg.contains(" ") ? quote(___arg) : ___arg)
                        .collect(Collectors.toList()))
                .build();
        return List.of(commandToRun.getCommand());
    }

    private void finalizedCommand(final List<String> fullCommand, final String executable, final List<String> allArgs) {
        if (!isScriptMode) {
            if (isAdminMode) {
                fullCommand.addAll(adminModeCommand(executable, allArgs));
            } else {
                fullCommand.add(executable);
                fullCommand.addAll(allArgs);
            }
        }
    }

    private List<String> buildCommand(final File keytoolExecutable) {
        final var executable = keytoolExecutable.getAbsolutePath();
        final var allArgs = new ArrayList<String>();
        final var fullCommand = new ArrayList<String>();

        Optional.ofNullable(command).ifPresent(allArgs::add);
        allArgs.addAll(args);
        allArgs.addAll(zArgs);

        if (isScriptMode) {
            final var script = buildScript(executable, allArgs);
            fullCommand.addAll(isAdminMode ? adminModeScript(script) : script);
            System.out.println(CommandOutputFilter.filter(fullCommand, "\n"));
        }
        else {
            //This is just for logging the command to run.
            final var commandToRun = commandToRun(executable, allArgs);
            System.out.println(CommandOutputFilter.filter(commandToRun));
        }

        finalizedCommand(fullCommand, executable, allArgs);

        return fullCommand;
    }

    private String executeSingleCommand() {
        final var sbCommand = new StringBuilder();
        executable().ifPresent(___executable -> {
            final var fullCommand = buildCommand(___executable);
            sbCommand.append(String.join(" ", fullCommand).trim());
            runCommand(fullCommand.toArray(new String[]{}));
        });
        return sbCommand.toString();
    }

    private void nonBlankText(final String text, final Consumer<String> nonBlankLogic) {
        Optional.ofNullable(text).ifPresent(___text -> {
            if (!___text.isBlank()) {
                nonBlankLogic.accept(___text);
            }
        });
    }

    private void runCommand(final String ... commands) {
        if (!isNoop) {
            final int exitCode;
            try {
                exitCode = CommandRunner.runCommand((___output, ___error) -> {
                    try(var output = new BufferedReader(new InputStreamReader(___output, "UTF-8"));
                        var error = new BufferedReader(new InputStreamReader(___error, "UTF-8"))) {
                        final var outputText = output.lines().collect(Collectors.joining("\n"));
                        final var errorText = error.lines().collect(Collectors.joining("\n"));

                        nonBlankText(outputText, System.out::println);
                        nonBlankText(errorText, System.err::println);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, commands);
            } catch (NoCommandException e) {
                throw new RuntimeException(e);
            }

            if (exitCode != 0) {
                throw new KeytoolTaskExecutionException("Error performing the task.");
            }
        }
    }

    private String executeScriptCommands() {
        final var sbCommand = new StringBuilder();
        executable().ifPresent(___executable -> {
            final var fullCommand = buildCommand(___executable);
            sbCommand.append(String.join("\n", fullCommand).trim());
            if (isAdminMode) {
                runCommand(fullCommand.toArray(new String[]{}));
            }
            else {
                for (final var command : fullCommand) {
                    runCommand(command);
                }
            }
        });
        return sbCommand.toString();
    }

    /**
     * Do the actual execution of the command.
     *
     * @return The command executed.
     */
    public String execute() {
        final String output;
        if (isScriptMode) {
            output = executeScriptCommands();
        }
        else {
            output = executeSingleCommand();
        }
        return output;
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
    public static final class KeytoolExecutorBuilder {
        private final List<String> args;
        private final List<String> zArgs;
        private final Map<String, List<String>> fileArgs;
        private String dirAliasPrefix;
        private String dirAliasSuffix;
        private File dir;
        private boolean isScriptMode;
        private boolean isAdminMode;
        private String command;
        private boolean isNoop;
        private OSType osType;
        private File javaHome;
        private boolean inAdminMode;

        private KeytoolExecutorBuilder() {
            args = new ArrayList<>();
            zArgs = new ArrayList<>();
            fileArgs = new HashMap<>();
            dirAliasPrefix = "";
            dirAliasSuffix = "";
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
        public KeytoolExecutorBuilder addJavaHome(final File javaHome) {
            this.javaHome = javaHome;
            return this;
        }

        /**
         * Add of the value of the OSType.
         *
         * @param osType An instance of OSType.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addOSType(final OSType osType) {
            this.osType = osType;
            return this;
        }

        /**
         * Instruct the executor not actually execute the command but just display it.
         *
         * @param isNoop Pass in true not execute the command.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addNoop(final boolean isNoop) {
            this.isNoop = isNoop;
            return this;
        }

        /**
         * Add the command to run.
         *
         * @param command The command to run.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addCommand(final String command) {
            this.command = command;
            return this;
        }

        /**
         * Add the arguments for the command.
         *
         * @param args The arguments for the command.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addArgs(final String ... args) {
            this.args.addAll(Arrays.asList(args));
            return this;
        }

        /**
         * Add a terminal arguments that are added as the last arguments for the command.
         *
         * @param zArgs The terminal arguments.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addZArgs(final String ... zArgs) {
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
        public KeytoolExecutorBuilder addAdminMode(final boolean isAdminMode) {
            this.isAdminMode = isAdminMode;
            return this;
        }

        /**
         * Indicates that gradle is already running in administration mode.
         *
         * @param inAdminMode Set to true it the task is already running in administration mode.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addRunningInAdminMode(final boolean inAdminMode) {
            this.inAdminMode = inAdminMode;
            return this;
        }

        /**
         * Add the location of the certificates.
         *
         * @param dir Add the directory where the certificates can be found.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addDirectory(final File dir) {
            this.dir = dir;
            return this;
        }

        /**
         * Indicates that the command will be run in script mode. This means multiple sequence of commands.
         *
         * @param isScriptMode Set to true to run the command in script mode.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addScriptMode(final boolean isScriptMode) {
            this.isScriptMode = isScriptMode;
            return this;
        }

        /**
         * Add the alias prefix of the directory process certificates.
         *
         * @param prefix The terminal arguments.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addDirAliasPrefix(final String prefix) {
            this.dirAliasPrefix = prefix;
            return this;
        }


        /**
         * Add the alias suffix of the directory process certificates.
         *
         * @param suffix The terminal arguments.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addDirAliasSuffix(final String suffix) {
            this.dirAliasSuffix = suffix;
            return this;
        }

        /**
         * Add arguments for a particular certificate file.
         *
         * @param fileArgs The certificate files arguments.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addFileArgs(final Map<String, List<String>> fileArgs) {
            this.fileArgs.putAll(fileArgs);
            return this;
        }
    }
}
