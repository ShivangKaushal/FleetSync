package com.fleetmanager.core;

/**
 * Represents a refrigerated van in the fleet.
 * 
 * @author Shivang
 */
public class RefrigeratedVan extends Vehicle {

    private double minTemperatureCelsius;

    /**
     * Constructs a new RefrigeratedVan with the specified details.
     * 
     * @param id The unique identifier of the van
     * @param licensePlate The license plate of the van
     * @param model The model of the van
     * @param minTemperatureCelsius The minimum temperature the van can maintain in Celsius
     */
    public RefrigeratedVan(String id, String licensePlate, String model, double minTemperatureCelsius) {
        super(id, licensePlate, model);
        this.minTemperatureCelsius = minTemperatureCelsius;
    }

    /**
     * Gets the minimum temperature the van can maintain.
     * 
     * @return The minimum temperature in Celsius
     */
    public double getMinTemperatureCelsius() {
        return minTemperatureCelsius;
    }

    /**
     * Sets the minimum temperature the van can maintain.
     * 
     * @param minTemperatureCelsius The new minimum temperature in Celsius
     */
    public void setMinTemperatureCelsius(double minTemperatureCelsius) {
        this.minTemperatureCelsius = minTemperatureCelsius;
    }

    /**
     * Gets the specific type of the vehicle.
     * 
     * @return The string "Refrigerated Van"
     */
    @Override
    public String getVehicleType() {
        return "Refrigerated Van";
    }

    /**
     * Returns a string representation of the refrigerated van.
     * 
     * @return Formatted refrigerated van information
     */
    @Override
    public String toString() {
        return super.toString() + ", minTemperatureCelsius=" + minTemperatureCelsius;
    }
}
