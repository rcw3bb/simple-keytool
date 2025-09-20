package xyz.ronella.gradle.plugin.simple.keytool;

import org.gradle.api.GradleException;

/**
 * The base exception to the KeytoolPlugin.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public class KeytoolException extends GradleException {

    private static final long serialVersionUID = -5914444361318429376L;

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
    public KeytoolException(final String message) {
        super(message);
    }

    /**
     * Creates an instance of KeytoolException.
     *
     * @param message The message of the exception.
     * @param cause The cause of the exception.
     */
    public KeytoolException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
