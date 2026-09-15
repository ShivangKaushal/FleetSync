package com.fleetmanager.exceptions;

/**
 * Exception thrown when an invalid route assignment operation is attempted,
 * such as assigning a route that is already assigned or scheduling conflicting routes.
 *
 * @author Shivang
 */
public class RouteAssignmentException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new RouteAssignmentException with a default error message.
     */
    public RouteAssignmentException() {
        super("Invalid route assignment operation: The route is already assigned or cannot be processed.");
    }

    /**
     * Constructs a new RouteAssignmentException with the specified detail message.
     *
     * @param message the detail message explaining the route assignment failure
     */
    public RouteAssignmentException(String message) {
        super(message);
    }

    /**
     * Constructs a new RouteAssignmentException with the specified detail message and cause.
     *
     * @param message the detail message explaining the route assignment failure
     * @param cause the underlying cause of this exception
     */
    public RouteAssignmentException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new RouteAssignmentException with the specified cause.
     *
     * @param cause the underlying cause of this exception
     */
    public RouteAssignmentException(Throwable cause) {
        super(cause);
    }
}
