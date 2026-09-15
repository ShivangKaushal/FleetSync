package com.fleetmanager.route;

/**
 * Represents a route for a vehicle in the fleet management system.
 * This class tracks origin, destination, distance, and the progress of the route.
 *
 * @author Shivang
 */
public class Route {

    /**
     * Represents the current status of the route.
     */
    public enum RouteStatus {
        PENDING, IN_PROGRESS, COMPLETED
    }

    private String routeId;
    private String origin;
    private String destination;
    private double distanceKm;
    private String assignedVehicleId;
    private RouteStatus status;
    private double progressKm;

    /**
     * Constructs a new Route with the specified details.
     *
     * @param routeId           the unique identifier for the route
     * @param origin            the starting location
     * @param destination       the destination location
     * @param distanceKm        the total distance of the route in kilometers
     * @param assignedVehicleId the ID of the vehicle assigned to this route
     */
    public Route(String routeId, String origin, String destination, double distanceKm, String assignedVehicleId) {
        this.routeId = routeId;
        this.origin = origin;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.assignedVehicleId = assignedVehicleId;
        this.status = RouteStatus.PENDING;
        this.progressKm = 0.0;
    }

    /**
     * Gets the route ID.
     * @return the route ID
     */
    public String getRouteId() {
        return routeId;
    }

    /**
     * Sets the route ID.
     * @param routeId the route ID to set
     */
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    /**
     * Gets the origin.
     * @return the origin
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * Sets the origin.
     * @param origin the origin to set
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * Gets the destination.
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Sets the destination.
     * @param destination the destination to set
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * Gets the total distance.
     * @return the distance in kilometers
     */
    public double getDistanceKm() {
        return distanceKm;
    }

    /**
     * Sets the total distance.
     * @param distanceKm the distance in kilometers to set
     */
    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    /**
     * Gets the assigned vehicle ID.
     * @return the vehicle ID
     */
    public String getAssignedVehicleId() {
        return assignedVehicleId;
    }

    /**
     * Sets the assigned vehicle ID.
     * @param assignedVehicleId the vehicle ID to set
     */
    public void setAssignedVehicleId(String assignedVehicleId) {
        this.assignedVehicleId = assignedVehicleId;
    }

    /**
     * Gets the current status of the route.
     * @return the route status
     */
    public RouteStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the route.
     * @param status the route status to set
     */
    public void setStatus(RouteStatus status) {
        this.status = status;
    }

    /**
     * Gets the progress in kilometers.
     * @return the progress in kilometers
     */
    public double getProgressKm() {
        return progressKm;
    }

    /**
     * Sets the progress in kilometers.
     * @param progressKm the progress in kilometers to set
     */
    public void setProgressKm(double progressKm) {
        this.progressKm = progressKm;
    }

    /**
     * Advances the progress of the route by the specified kilometers.
     *
     * @param km the distance to add to the progress
     */
    public synchronized void advanceProgress(double km) {
        if (km > 0) {
            this.progressKm += km;
            if (this.progressKm > this.distanceKm) {
                this.progressKm = this.distanceKm;
            }
        }
    }

    /**
     * Calculates the remaining distance to complete the route.
     *
     * @return the remaining distance in kilometers
     */
    public synchronized double getRemainingKm() {
        return Math.max(0, this.distanceKm - this.progressKm);
    }

    /**
     * Checks if the route is fully completed.
     *
     * @return true if progress is greater than or equal to the total distance, false otherwise
     */
    public synchronized boolean isComplete() {
        return this.progressKm >= this.distanceKm;
    }

    /**
     * Returns a string representation of the route.
     *
     * @return a string describing the route
     */
    @Override
    public String toString() {
        return "Route{" +
                "routeId='" + routeId + '\'' +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", distanceKm=" + distanceKm +
                ", assignedVehicleId='" + assignedVehicleId + '\'' +
                ", status=" + status +
                ", progressKm=" + progressKm +
                '}';
    }
}
