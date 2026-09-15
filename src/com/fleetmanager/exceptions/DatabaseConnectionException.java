package com.fleetmanager.exceptions;

/**
 * Exception thrown to wrap low-level database errors (such as {@link java.sql.SQLException})
 * to provide cleaner abstraction and propagation across fleet management service layers.
 *
 * @author Shivang
 */
public class DatabaseConnectionException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new DatabaseConnectionException with a default error message.
     */
    public DatabaseConnectionException() {
        super("Failed to establish or maintain a connection to the fleet database.");
    }

    /**
     * Constructs a new DatabaseConnectionException with the specified detail message.
     *
     * @param message the detail message explaining the database connection failure
     */
    public DatabaseConnectionException(String message) {
        super(message);
    }

    /**
     * Constructs a new DatabaseConnectionException with the specified detail message and cause.
     *
     * @param message the detail message explaining the database connection failure
     * @param cause the underlying low-level cause (e.g., SQLException)
     */
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DatabaseConnectionException with the specified cause.
     *
     * @param cause the underlying low-level cause (e.g., SQLException)
     */
    public DatabaseConnectionException(Throwable cause) {
        super(cause);
    }
}
