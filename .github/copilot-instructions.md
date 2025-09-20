# AI Coding Agent Instructions

## Project Overview
This is a Gradle plugin that wraps Java's `keytool` command for certificate management. The plugin provides convenience tasks for common keytool operations (import, delete, list) for both CACerts and custom keystores.

## Architecture Patterns

### Task Hierarchy
- **Base Task**: `KeytoolTask` - Core task that executes keytool commands
- **Convenience Tasks**: Extend `KeytoolTask` with predefined commands/arguments
  - `CACerts*` tasks: For Java's default certificate store (uses `-cacerts` argument)
  - `KS*` tasks: For custom keystores (requires keystore file path)
  - `*Dir` tasks: Process entire directories of certificates

### Interface-Based Arguments
The plugin uses trait-like interfaces for task properties:
- `IAliasArg`, `IFileArg`, `IStorePassArg`, etc. define specific keytool arguments
- `ArgumentManager` automatically processes these interfaces to build command arguments
- Tasks implement relevant interfaces: `class CACertsImportTask extends KeytoolTask implements IAliasRequiredArg, IFileArg, IVerboseArg`

### Extension Configuration
All shared configuration lives in `SimpleKeytoolPluginExtension`:
- `simple_keytool.noop = true` - Display commands without executing (critical for testing)
- `simple_keytool.storePass = "password"` - Default keystore password
- `simple_keytool.defaultCertsDir` - Default certificate directory for batch operations

## Development Workflows

### Testing
- **Standard Pattern**: Use `ProjectBuilder` to create test projects
- **Required Setup**: Always set `project.extensions.simple_keytool.noop = true` in tests
- **Command Validation**: Use `PSCommandDecoder.decode()` to parse PowerShell scripts for assertion
- **Example**:
```groovy
@BeforeEach
void initProject() {
    project = ProjectBuilder.builder().build()
    project.pluginManager.apply 'xyz.ronella.simple-keytool'
    project.extensions.simple_keytool.noop = true
}
```

### Version Consistency
Maintain version synchronization across:
- `gradle.properties` (line 2): `version=1.1.1`
- `README.md` (line 16): `id "xyz.ronella.simple-keytool" version "1.1.1"`
- `CHANGELOG.md` (line 3): `## 1.1.1 : 2024-12-09`

### Build Commands
- `gradlew clean check` - Run all tests and code quality checks
- `gradlew jar` - Build plugin JAR
- `gradlew tasks --group "Simple Keytool"` - List available plugin tasks

## Key Components

### KeytoolExecutor
Central command execution class with builder pattern:
- Handles Windows/PowerShell command generation
- Manages admin mode elevation when needed
- Supports both single commands and script mode (multiple certificates)

### Plugin Registration
Tasks are registered in `SimpleKeytoolPlugin.apply()`:
```groovy
task('cacertsImport', type:CACertsImportTask)
task('ksImportDir', type:KSImportDirTask)
```

## Windows-Specific Considerations
- Plugin is Windows-only (uses PowerShell for command execution)
- Admin mode automatically detected and elevated when needed
- Certificate paths and PowerShell escaping handled by `KeytoolExecutor`

## Creating New Tasks
1. Extend `KeytoolTask`
2. Implement relevant `I*Arg` interfaces
3. Set `internalCommand.convention()` for default keytool command
4. Register in `SimpleKeytoolPlugin`
5. Create corresponding test with `noop = true`

## File Patterns
- Task implementations: `src/main/groovy/*/task/*Task.groovy`
- Argument interfaces: `src/main/groovy/*/args/I*Arg.groovy`
- Tests: `src/test/groovy/*/*Test.groovy` (mirrors main structure)
- Configuration: `gradle.properties`, `build.gradle` (Shadow plugin for fat JAR)