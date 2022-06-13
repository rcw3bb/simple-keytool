package xyz.ronella.gradle.plugin.simple.keytool.args

import org.gradle.api.provider.ListProperty
import xyz.ronella.gradle.plugin.simple.keytool.SimpleKeytoolPluginExtension
import xyz.ronella.gradle.plugin.simple.keytool.task.KeytoolTask

/**
 * The class the manages the interface arguments when any of them was added.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
final class ArgumentManager {

    final private static List<Closure> ARGUMENTS = []
    final private static String ALIAS_ARG = '-alias'

    static {
        addVerboseArg()
        addAliasArg()
        addAliasRequiredArg()
        addKeyStoreArg()
        addKeyPassArg()
        addStoreTypeArg()
        addStorePassArg()
        addFileArg()
    }

    private ArgumentManager() {}

    private static void processArg(Closure instanceCheck, Closure casting, Closure argsConfiguration) {
        if (instanceCheck.call()) {
            argsConfiguration.call(casting.call())
        }
    }

    private static addAliasArg() {
        ARGUMENTS.add {___task, ___args, ___ext ->
            processArg({___task instanceof IAliasArg},{___task as IAliasArg}) {
                if (it.alias.present) {
                    ___args.addAll(ALIAS_ARG, "${it.alias.get()}")
                }
            }
        }
    }

    private static addAliasRequiredArg() {
        ARGUMENTS.add {___task, ___args, ___ext ->
            processArg({___task instanceof IAliasRequiredArg},{___task as IAliasRequiredArg}) {
                if (it.alias.present) {
                    ___args.addAll(ALIAS_ARG, "${it.alias.get()}")
                }
            }
        }
    }

    private static addKeyPassArg() {
        ARGUMENTS.add {___task, ___args, ___ext ->
            processArg({___task instanceof IKeyPassArg},{___task as IKeyPassArg}) {
                if (it.keyPass.present) {
                    ___args.addAll('-keypass', "${it.keyPass.get()}")
                }
            }
        }
    }

    private static addStoreTypeArg() {
        ARGUMENTS.add {___task, ___args, ___ext ->
            processArg({___task instanceof IStoreTypeArg},{___task as IStoreTypeArg}) {
                if (it.storeType.present) {
                    ___args.addAll('-storetype', "${it.storeType.get()}")
                }
            }
        }
    }

    private static addVerboseArg() {
        ARGUMENTS.add {___task, ___args, ___ext ->
            processArg({___task instanceof IVerboseArg}, {___task as IVerboseArg}) {
                if (it.verbose.present) {
                    ___args.addAll('-v')
                }
            }
        }
    }

    private static addStorePassArg() {
        ARGUMENTS.add {___task, ___args, ___ext ->
            processArg({ ___task instanceof IStorePassArg}, { ___task as IStorePassArg }) {
                ___args.addAll('-storepass', "${it.storePass.getOrElse(___ext.storePass.get())}")
            }
        }
    }

    private static addKeyStoreArg() {
        ARGUMENTS.add {___task, ___args, ___ext ->
            processArg({ ___task instanceof IKeyStoreArg}, { ___task as IKeyStoreArg }) {
                if (it.keyStore.present) {
                    ___args.addAll('-keystore', "${it.keyStore.asFile.get().absolutePath}")
                }
            }
        }
    }

    private static addFileArg() {
        ARGUMENTS.add {___task, ___args, ___ext ->
            processArg({ ___task instanceof IFileArg}, { ___task as IFileArg }) {
                if (it.file.present) {
                    ___args.addAll('-file', "${it.file.asFile.get().absolutePath}")
                }
            }
        }
    }

    /**
     * Process the arguments of a particular task.
     *
     * @param task An instance of the task that is being processed.
     * @param args An instance of the collection of arguments of the task.
     * @param ext An instance of SimpleKeytoolPluginExtension.
     */
    static processArgs(KeytoolTask task, ListProperty<String> args, SimpleKeytoolPluginExtension ext) {
        Collections.unmodifiableList(ARGUMENTS).forEach{it.call(task, args, ext)}
    }

}
