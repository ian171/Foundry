package cn.epicmc.foundry.api.registry;

import org.bukkit.NamespacedKey;

/**
 * Thrown when a registration operation fails.
 * <p>
 * Common causes:
 * <ul>
 *   <li>Attempting to register a duplicate key</li>
 *   <li>Registering outside the allowed phase</li>
 *   <li>Registering an invalid object</li>
 * </ul>
 */
public class RegistrationException extends RuntimeException {

    private final NamespacedKey key;

    /**
     * Create a new registration exception.
     *
     * @param message The error message
     */
    public RegistrationException(String message) {
        super(message);
        this.key = null;
    }

    /**
     * Create a new registration exception with an associated key.
     *
     * @param message The error message
     * @param key The key that caused the error
     */
    public RegistrationException(String message, NamespacedKey key) {
        super(message);
        this.key = key;
    }

    /**
     * Create a new registration exception with a cause.
     *
     * @param message The error message
     * @param cause The underlying cause
     */
    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
        this.key = null;
    }

    /**
     * Create a new registration exception with a key and cause.
     *
     * @param message The error message
     * @param key The key that caused the error
     * @param cause The underlying cause
     */
    public RegistrationException(String message, NamespacedKey key, Throwable cause) {
        super(message, cause);
        this.key = key;
    }

    /**
     * Get the key that caused this exception, if available.
     *
     * @return The key, or null if not available
     */
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public String getMessage() {
        if (key != null) {
            return super.getMessage() + " (key: " + key + ")";
        }
        return super.getMessage();
    }
}
