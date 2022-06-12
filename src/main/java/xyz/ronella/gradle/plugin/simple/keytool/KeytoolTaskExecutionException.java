package xyz.ronella.gradle.plugin.simple.keytool;

/**
 * The exception that indicates the task failed to complete.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public class KeytoolTaskExecutionException extends KeytoolException {

    private static final long serialVersionUID = -8209044682550556478L;

    /**
     * Creates an instance of KeytoolTaskExecutionException.
     *
     * @param message Accepts a custom message.
     */
    public KeytoolTaskExecutionException(final String message) {
        super(message);
    }
}
