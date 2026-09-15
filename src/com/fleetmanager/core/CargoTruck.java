package com.fleetmanager.core;

/**
 * Represents a cargo truck in the fleet.
 * 
 * @author Shivang
 */
public class CargoTruck extends Vehicle {

    private double maxPayloadTons;

    /**
     * Constructs a new CargoTruck with the specified details.
     * 
     * @param id The unique identifier of the truck
     * @param licensePlate The license plate of the truck
     * @param model The model of the truck
     * @param maxPayloadTons The maximum payload capacity in tons
     */
    public CargoTruck(String id, String licensePlate, String model, double maxPayloadTons) {
        super(id, licensePlate, model);
        this.maxPayloadTons = maxPayloadTons;
    }

    /**
     * Gets the maximum payload capacity of the truck.
     * 
     * @return The maximum payload in tons
     */
    public double getMaxPayloadTons() {
        return maxPayloadTons;
    }

    /**
     * Sets the maximum payload capacity of the truck.
     * 
     * @param maxPayloadTons The new maximum payload in tons
     */
    public void setMaxPayloadTons(double maxPayloadTons) {
        this.maxPayloadTons = maxPayloadTons;
    }

    /**
     * Gets the specific type of the vehicle.
     * 
     * @return The string "Cargo Truck"
     */
    @Override
    public String getVehicleType() {
        return "Cargo Truck";
    }

    /**
     * Returns a string representation of the cargo truck.
     * 
     * @return Formatted cargo truck information
     */
    @Override
    public String toString() {
        return super.toString() + ", maxPayloadTons=" + maxPayloadTons;
    }
}
