package xyz.ronella.gradle.plugin.simple.keytool;

/**
 * The exception that indicates the task failed to complete.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public class KeytoolTaskExecutionException extends KeytoolException {

    /**
     * Creates an instance of KeytoolTaskExecutionException.
     *
     * @param message Accepts a custom message.
     */
    public KeytoolTaskExecutionException(String message) {
        super(message);
    }
}
