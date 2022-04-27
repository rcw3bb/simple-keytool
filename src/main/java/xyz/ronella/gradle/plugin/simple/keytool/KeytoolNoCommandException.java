package xyz.ronella.gradle.plugin.simple.keytool;

/**
 * The exception that indicates the task has no command to execute.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public class KeytoolNoCommandException  extends KeytoolException {

    /**
     * Creates an instance of KeytoolNoCommandException.
     *
     * @param message Accepts a custom message.
     */
    public KeytoolNoCommandException(String message) {
        super(message);
    }
}