package xyz.ronella.gradle.plugin.simple.keytool

import org.gradle.api.provider.Property

interface SimpleKeytoolPluginExtension {

    Property<Boolean> getVerbose()

    Property<Boolean> getNoop()

    Property<File> getJavaHome()

}
