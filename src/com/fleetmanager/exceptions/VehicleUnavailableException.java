package com.fleetmanager.exceptions;

/**
 * Exception thrown when a vehicle is already on an active route and cannot be double-booked
 * or assigned to another route concurrently.
 *
 * @author Shivang
 */
public class VehicleUnavailableException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new VehicleUnavailableException with a default error message.
     */
    public VehicleUnavailableException() {
        super("The requested vehicle is currently unavailable or already assigned to an active route.");
    }

    /**
     * Constructs a new VehicleUnavailableException with the specified detail message.
     *
     * @param message the detail message explaining why the vehicle is unavailable
     */
    public VehicleUnavailableException(String message) {
        super(message);
    }

    /**
     * Constructs a new VehicleUnavailableException with the specified detail message and cause.
     *
     * @param message the detail message explaining why the vehicle is unavailable
     * @param cause the underlying cause of this exception
     */
    public VehicleUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new VehicleUnavailableException with the specified cause.
     *
     * @param cause the underlying cause of this exception
     */
    public VehicleUnavailableException(Throwable cause) {
        super(cause);
    }
}
