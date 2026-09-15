package com.fleetmanager.core;

import com.fleetmanager.logging.FleetLogger;

/**
 * Abstract base class representing a generic vehicle in the fleet.
 * 
 * @author Shivang
 */
public abstract class Vehicle {
    
    /**
     * Represents the current operational status of the vehicle.
     */
    public enum VehicleStatus {
        AVAILABLE,
        ON_ROUTE,
        MAINTENANCE
    }

    private String id;
    private String licensePlate;
    private String model;
    private VehicleStatus status;
    private int tripCount;

    /**
     * Constructs a new Vehicle with the specified details.
     * 
     * @param id The unique identifier of the vehicle
     * @param licensePlate The license plate of the vehicle
     * @param model The model of the vehicle
     */
    public Vehicle(String id, String licensePlate, String model) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.model = model;
        this.status = VehicleStatus.AVAILABLE;
        this.tripCount = 0;
    }

    /**
     * Gets the unique identifier of the vehicle.
     * 
     * @return The vehicle ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the vehicle.
     * 
     * @param id The new vehicle ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the license plate of the vehicle.
     * 
     * @return The license plate
     */
    public String getLicensePlate() {
        return licensePlate;
    }

    /**
     * Sets the license plate of the vehicle.
     * 
     * @param licensePlate The new license plate
     */
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    /**
     * Gets the model of the vehicle.
     * 
     * @return The vehicle model
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the model of the vehicle.
     * 
     * @param model The new vehicle model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Gets the current operational status of the vehicle.
     * 
     * @return The vehicle status
     */
    public VehicleStatus getStatus() {
        return status;
    }

    /**
     * Sets the operational status of the vehicle.
     * 
     * @param status The new vehicle status
     */
    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    /**
     * Gets the number of trips this vehicle has completed.
     * 
     * @return The trip count
     */
    public int getTripCount() {
        return tripCount;
    }

    /**
     * Sets the number of trips this vehicle has completed.
     * 
     * @param tripCount The new trip count
     */
    public void setTripCount(int tripCount) {
        this.tripCount = tripCount;
    }

    /**
     * Abstract method to get the specific type of the vehicle.
     * 
     * @return The string representation of the vehicle type
     */
    public abstract String getVehicleType();

    /**
     * Checks if the vehicle needs maintenance based on its trip count.
     * A vehicle needs maintenance if its trip count is 10 or more.
     * 
     * @return True if the vehicle needs maintenance, false otherwise
     */
    public boolean needsMaintenance() {
        return tripCount >= 10;
    }

    /**
     * Increments the trip count of the vehicle by 1.
     */
    public void incrementTripCount() {
        this.tripCount++;
    }

    /**
     * Returns a string representation of the vehicle.
     * 
     * @return Formatted vehicle information
     */
    @Override
    public String toString() {
        return "Vehicle [id=" + id + ", licensePlate=" + licensePlate + ", model=" + model + ", status=" + status + ", tripCount=" + tripCount + "]";
    }
}
