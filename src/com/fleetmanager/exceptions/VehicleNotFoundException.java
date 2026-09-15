package com.fleetmanager.exceptions;

/**
 * Exception thrown when a lookup operation for a vehicle by its ID or identifier fails
 * because the vehicle does not exist in the fleet inventory.
 *
 * @author Shivang
 */
public class VehicleNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new VehicleNotFoundException with a default error message.
     */
    public VehicleNotFoundException() {
        super("Vehicle with the specified ID could not be found in the fleet system.");
    }

    /**
     * Constructs a new VehicleNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining the lookup failure
     */
    public VehicleNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new VehicleNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message explaining the lookup failure
     * @param cause the underlying cause of this exception
     */
    public VehicleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new VehicleNotFoundException with the specified cause.
     *
     * @param cause the underlying cause of this exception
     */
    public VehicleNotFoundException(Throwable cause) {
        super(cause);
    }
}
