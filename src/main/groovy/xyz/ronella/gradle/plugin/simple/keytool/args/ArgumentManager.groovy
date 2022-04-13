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

    static {
        addVerboseArg()
        addAliasArg()
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

    private static def addAliasArg() {
        ARGUMENTS.add({___task, ___args, ___ext ->
            processArg({___task instanceof IAliasArg},{___task as IAliasArg},{
                if (it.alias.isPresent()) {
                    ___args.addAll('-alias', "${it.alias.get()}")
                }
            })
        })
    }

    private static def addKeyPassArg() {
        ARGUMENTS.add({___task, ___args, ___ext ->
            processArg({___task instanceof IKeyPassArg},{___task as IKeyPassArg},{
                if (it.keyPass.isPresent()) {
                    ___args.addAll('-keypass', "${it.keyPass.get()}")
                }
            })
        })
    }

    private static def addStoreTypeArg() {
        ARGUMENTS.add({___task, ___args, ___ext ->
            processArg({___task instanceof IStoreTypeArg},{___task as IStoreTypeArg},{
                if (it.storeType.isPresent()) {
                    ___args.addAll('-storetype', "${it.storeType.get()}")
                }
            })
        })
    }

    private static def addVerboseArg() {
        ARGUMENTS.add({___task, ___args, ___ext ->
            processArg({___task instanceof IVerboseArg}, {___task as IVerboseArg}, {
                if (it.verbose.isPresent()) {
                    ___args.addAll('-v')
                }
            })
        })
    }

    private static def addStorePassArg() {
        ARGUMENTS.add({___task, ___args, ___ext ->
            processArg({ ___task instanceof IStorePassArg}, { ___task as IStorePassArg }, {
                ___args.addAll('-storepass', "${it.storePass.getOrElse(___ext.storePass.get())}")
            })
        })
    }

    private static def addKeyStoreArg() {
        ARGUMENTS.add({___task, ___args, ___ext ->
            processArg({ ___task instanceof IKeyStoreArg}, { ___task as IKeyStoreArg }, {
                if (it.keyStore.isPresent()) {
                    ___args.addAll('-keystore', "${it.keyStore.getAsFile().get().absolutePath}")
                }
            })
        })
    }

    private static def addFileArg() {
        ARGUMENTS.add({___task, ___args, ___ext ->
            processArg({ ___task instanceof IFileArg}, { ___task as IFileArg }, {
                if (it.file.isPresent()) {
                    ___args.addAll('-file', "${it.file.getAsFile().get().absolutePath}")
                }
            })
        })
    }

    /**
     * Process the arguments of a particular task.
     *
     * @param task An instance of the task that is being processed.
     * @param args An instance of the collection of arguments of the task.
     * @param ext An instance of SimpleKeytoolPluginExtension.
     */
    static def processArgs(KeytoolTask task, ListProperty<String> args, SimpleKeytoolPluginExtension ext) {
        Collections.unmodifiableList(ARGUMENTS).forEach({it.call(task, args, ext)})
    }

}
