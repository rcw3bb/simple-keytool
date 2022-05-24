package xyz.ronella.gradle.plugin.simple.keytool;

import xyz.ronella.gradle.plugin.simple.keytool.tool.CommandOutputFilter;
import xyz.ronella.trivial.handy.CommandRunner;
import xyz.ronella.trivial.handy.NoCommandException;
import xyz.ronella.trivial.handy.OSType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private final boolean isScriptMode;
    private final File dir;
    private final Map<String, List<String>> fileArgs;
    private final String dirAliasPrefix;
    private final String dirAliasSuffix;

    private KeytoolExecutor(KeytoolExecutorBuilder builder) {
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
        isAdminMode = !builder.runningInAdminMode && builder.isAdminMode;
        dirAliasPrefix = builder.dirAliasPrefix;
        dirAliasSuffix = builder.dirAliasSuffix;

        initExecutables();
    }

    private File getExecutableAuto() {
        var sbFQFN = new StringBuilder();

        try {
            CommandRunner.runCommand((___output, ___error) -> {
                try(var output = new BufferedReader(new InputStreamReader(___output)))  {
                    sbFQFN.append(output.lines().collect(Collectors.joining("\n")));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, "where", EXECUTABLE);
        } catch (NoCommandException e) {
            throw new RuntimeException(e);
        }

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

    private void initExecutables() {
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

    private List<String> getPowershellArgs() {
        var shellArgs = new ArrayList<String>();
        shellArgs.add("-NoProfile");
        shellArgs.add("-InputFormat");
        shellArgs.add("None");
        shellArgs.add("-ExecutionPolicy");
        shellArgs.add("Bypass");

        return new ArrayList<>(shellArgs);
    }

    private List<String> getPowershellCommand() {
        var shellCommand = new ArrayList<String>();
        shellCommand.add("powershell.exe");
        shellCommand.addAll(getPowershellArgs());
        shellCommand.add("-EncodedCommand");

        return new ArrayList<>(shellCommand);
    }

    private List<String> getScriptLines() {
        var script = new ArrayList<String>();
        script.add("$ProgressPreference = 'SilentlyContinue'");
        return new ArrayList<>(script);
    }

    private String tripleQuote(String text) {
        return String.format("\"\"\"%s\"\"\"", text);
    }

    private String quote(String text) {
        return String.format("\"%s\"", text);
    }

    private String singleQuote(String text) {
        return String.format("'%s'", text);
    }

    private void manageImportCertParam(List<String> commands, String ktCommand, Path certPath) {
        if ("-importcert".equalsIgnoreCase(ktCommand)) {
            commands.add("-file");
            var certFile = certPath.toFile().getAbsolutePath();
            commands.add(certFile);
        }
    }

    private void manageAliasParam(List<String> commands, String filename) {
        if (!commands.contains("-alias")) {
            commands.add("-alias");
            commands.add(String.format("%s %s %s", dirAliasPrefix, filename, dirAliasSuffix).trim());
        }
    }

    private List<String> buildCommands(String executable, List<String> allArgs) {
        var commands = new ArrayList<String>();
        commands.add(executable);
        commands.addAll(allArgs);
        return commands;
    }

    private List<String> buildScriptCommand() {
        var scriptCommand = new ArrayList<String>();
        if (isAdminMode) {
            scriptCommand.add("&");
        }
        return scriptCommand;
    }

    private List<String> createScriptCommands(String executable, List<String> allArgs) {
        var scriptCommands = new ArrayList<String>();

        if (null!=dir && dir.exists()) {
            var ktCommand = allArgs.stream().findFirst();
            try (var entries = Files.list(dir.toPath())) {
                entries.forEach(___path -> {
                    var filename = ___path.toFile().getName();
                    var commands = buildCommands(executable, allArgs);
                    manageFileSpecificParam(commands, filename);
                    manageImportCertParam(commands, ktCommand.orElse(""), ___path);

                    var scriptCommand = buildScriptCommand();
                    scriptCommand.addAll(commands.stream().map(___command -> isAdminMode ? singleQuote(___command) : quote(___command)).collect(Collectors.toList()));
                    scriptCommands.add(String.join(" ", scriptCommand));
                });
            } catch (IOException e) {
                throw new KeytoolException(e.getMessage());
            }
        }

        return scriptCommands;
    }

    private void manageFileSpecificParam(List<String> commands, String filename) {
        var fArgs = fileArgs.getOrDefault(filename, Collections.emptyList());
        commands.addAll(fArgs);
        manageAliasParam(commands, filename);
    }

    private List<String> buildScript(String executable, List<String> allArgs) {
        var sbArgs = new StringBuilder();
        getPowershellArgs().forEach(___arg -> sbArgs.append(sbArgs.length()>0 ? ",": "").append(tripleQuote(___arg)));

        var scriptCommands = createScriptCommands(executable, allArgs);

        if (scriptCommands.size()>0) {
            sbArgs.append(",").append(tripleQuote("-Command"));
            sbArgs.append(",").append(String.format("{%n%s%n}", String.join("\n", scriptCommands)));
        }
        else {
            throw new KeytoolNoCommandException("Command(s) not found");
        }
        return isAdminMode ? Arrays.asList(sbArgs.toString()) : scriptCommands;
    }

    private List<String> adminModeScript(List<String> commands) {
        return adminModeCommand("powershell.exe", null, commands);
    }

    private List<String> adminModeCommand(String executable, List<String> allArgs) {
        return adminModeCommand(executable, allArgs, null);
    }

    private List<String> completeAdminCommand(List<String> scriptLines) {
        var fullCommand = getPowershellCommand();
        var script = String.join("\n", scriptLines);
        var encodedCommand = Base64.getEncoder().encodeToString(script.getBytes(StandardCharsets.UTF_16LE));

        fullCommand.add(encodedCommand);

        return fullCommand;
    }

    private String buildAdminCommand(String executable, List<String> allArgs, List<String> commands) {
        var sbArgs = new StringBuilder();

        if (null!=allArgs) {
            allArgs.forEach(___arg -> sbArgs.append(sbArgs.length()>0 ? ",": "").append(tripleQuote(___arg)));
        }

        if (null!=commands) {
            commands.forEach(sbArgs::append);
        }

        return String.format("Exit (Start-Process %s -Wait -PassThru -Verb RunAs%s%s).ExitCode",
                quote(executable), (sbArgs.length() == 0 ? "" : " -argumentlist "), sbArgs);
    }

    private List<String> adminModeCommand(String executable, List<String> allArgs, List<String> commands) {
        var scriptLines = getScriptLines();
        var sbActualCommand = buildAdminCommand(executable, allArgs, commands);

        var command = String.join(" ", sbActualCommand);
        scriptLines.add(command);

        return completeAdminCommand(scriptLines);
    }

    private List<String> commandToRun(String executable, List<String> allArgs) {
        var commandToRun = new ArrayList<String>();
        commandToRun.add(executable.contains(" ") ? quote(executable) : executable);
        commandToRun.addAll(allArgs.stream()
                .map(___arg -> ___arg.contains(" ") ? quote(___arg) : ___arg)
                .collect(Collectors.toList())
        );
        return commandToRun;
    }

    private void finalizedCommand(List<String> fullCommand, String executable, List<String> allArgs) {
        if (!isScriptMode) {
            if (isAdminMode) {
                fullCommand.addAll(adminModeCommand(executable, allArgs));
            } else {
                fullCommand.add(executable);
                fullCommand.addAll(allArgs);
            }
        }
    }

    private List<String> buildCommand(File keytoolExecutable) {
        var executable = keytoolExecutable.getAbsolutePath();
        var allArgs = new ArrayList<String>();
        var fullCommand = new ArrayList<String>();

        Optional.ofNullable(command).ifPresent(allArgs::add);
        allArgs.addAll(args);
        allArgs.addAll(zArgs);

        if (isScriptMode) {
            var script = buildScript(executable, allArgs);
            fullCommand.addAll(isAdminMode ? adminModeScript(script) : script);
            System.out.println(CommandOutputFilter.filter(fullCommand, "\n"));
        }
        else {
            var commandToRun = commandToRun(executable, allArgs);
            System.out.println(CommandOutputFilter.filter(commandToRun));
        }

        finalizedCommand(fullCommand, executable, allArgs);

        return fullCommand;
    }

    private String executeSingleCommand() {
        var sbCommand = new StringBuilder();
        executable().ifPresent(___executable -> {
            var fullCommand = buildCommand(___executable);
            sbCommand.append(String.join(" ", fullCommand).trim());
            runCommand(fullCommand.toArray(new String[]{}));
        });
        return sbCommand.toString();
    }

    private void nonBlankText(String text, Consumer<String> nonBlankLogic) {
        Optional.ofNullable(text).ifPresent(___text -> {
            if (!___text.isBlank()) {
                nonBlankLogic.accept(___text);
            }
        });
    }

    private void runCommand(String ... commands) {
        if (!isNoop) {
            int exitCode;
            try {

                exitCode = CommandRunner.runCommand((___output, ___error) -> {
                    try(var output = new BufferedReader(new InputStreamReader(___output));
                        var error = new BufferedReader(new InputStreamReader(___error))) {
                        var errorText = error.lines().collect(Collectors.joining("\n"));
                        var outputText = output.lines().collect(Collectors.joining("\n"));;

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
        var sbCommand = new StringBuilder();
        executable().ifPresent(___executable -> {
            var fullCommand = buildCommand(___executable);
            sbCommand.append(String.join("\n", fullCommand).trim());
            if (isAdminMode) {
                runCommand(fullCommand.toArray(new String[]{}));
            }
            else {
                for (var command : fullCommand) {
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
        if (isScriptMode) {
            return executeScriptCommands();
        }
        else {
            return executeSingleCommand();
        }
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
        private boolean runningInAdminMode;

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

        /**
         * Add the location of the certificates.
         *
         * @param dir Add the directory where the certificates can be found.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addDirectory(File dir) {
            this.dir = dir;
            return this;
        }

        /**
         * Indicates that the command will be run in script mode. This means multiple sequence of commands.
         *
         * @param isScriptMode Set to true to run the command in script mode.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addScriptMode(boolean isScriptMode) {
            this.isScriptMode = isScriptMode;
            return this;
        }

        /**
         * Add the alias prefix of the directory process certificates.
         *
         * @param prefix The terminal arguments.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addDirAliasPrefix(String prefix) {
            this.dirAliasPrefix = prefix;
            return this;
        }


        /**
         * Add the alias suffix of the directory process certificates.
         *
         * @param suffix The terminal arguments.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addDirAliasSuffix(String suffix) {
            this.dirAliasSuffix = suffix;
            return this;
        }

        /**
         * Add arguments for a particular certificate file.
         *
         * @param fileArgs The certificate files arguments.
         * @return An instance of KeytoolExecutorBuilder
         */
        public KeytoolExecutorBuilder addFileArgs(Map<String, List<String>> fileArgs) {
            this.fileArgs.putAll(fileArgs);
            return this;
        }
    }
}
