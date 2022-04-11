package xyz.ronella.gradle.plugin.simple.keytool;

import org.gradle.api.GradleException;

/**
 * The base exception to the KeytoolPlugin.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public class KeytoolException extends GradleException {

    /**
     * Creates an instance of KeytoolException.
     */
    public KeytoolException() {
        super();
    }

    /**
     * Creates an instance of KeytoolException.
     *
     * @param message The message of the exception.
     */
    public KeytoolException(String message) {
        super(message);
    }
}
