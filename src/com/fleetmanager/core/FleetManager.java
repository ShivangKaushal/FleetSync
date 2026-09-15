package com.fleetmanager.core;

import com.fleetmanager.exceptions.VehicleNotFoundException;
import com.fleetmanager.exceptions.VehicleUnavailableException;
import com.fleetmanager.logging.FleetLogger;
import com.fleetmanager.route.Route;
import com.fleetmanager.route.RouteMonitor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FleetManager acts as the central controller for managing the fleet of vehicles
 * and their routes. It maintains an in-memory HashMap roster and an ArrayList of
 * active routes, coordinating RouteMonitor threads for concurrent delivery tracking.
 *
 * @author Shivang
 */
public class FleetManager {

    /** In-memory roster of vehicles keyed by vehicle ID. */
    private HashMap<String, Vehicle> vehicleRoster;

    /** List of all routes (active and completed). */
    private ArrayList<Route> routes;

    /** Singleton logger instance. */
    private FleetLogger logger;

    /**
     * Constructs a new FleetManager, initializing empty collections and obtaining
     * the logger singleton.
     */
    public FleetManager() {
        this.vehicleRoster = new HashMap<>();
        this.routes = new ArrayList<>();
        this.logger = FleetLogger.getInstance();
    }

    /**
     * Adds a vehicle to the in-memory roster.
     *
     * @param v the vehicle to add
     */
    public void addVehicle(Vehicle v) {
        vehicleRoster.put(v.getId(), v);
        logger.info("Added vehicle to fleet: " + v.getId());
    }

    /**
     * Retrieves a vehicle by its unique ID.
     *
     * @param id the ID of the vehicle to look up
     * @return the Vehicle object
     * @throws VehicleNotFoundException if no vehicle with the given ID exists
     */
    public Vehicle getVehicle(String id) throws VehicleNotFoundException {
        Vehicle v = vehicleRoster.get(id);
        if (v == null) {
            throw new VehicleNotFoundException("Vehicle with ID '" + id + "' not found in the fleet.");
        }
        return v;
    }

    /**
     * Returns a snapshot list of all vehicles currently in the roster.
     *
     * @return a new ArrayList containing all vehicles
     */
    public List<Vehicle> listAllVehicles() {
        return new ArrayList<>(vehicleRoster.values());
    }

    /**
     * Updates the license plate and model of an existing vehicle.
     *
     * @param id              the ID of the vehicle to update
     * @param newLicensePlate the new license plate string
     * @param newModel        the new model string
     * @throws VehicleNotFoundException if no vehicle with the given ID exists
     */
    public void updateVehicle(String id, String newLicensePlate, String newModel) throws VehicleNotFoundException {
        Vehicle v = getVehicle(id);
        v.setLicensePlate(newLicensePlate);
        v.setModel(newModel);
        logger.info("Updated vehicle: " + id);
    }

    /**
     * Removes a vehicle from the fleet roster.
     *
     * @param id the ID of the vehicle to remove
     * @throws VehicleNotFoundException if no vehicle with the given ID exists
     */
    public void removeVehicle(String id) throws VehicleNotFoundException {
        if (vehicleRoster.remove(id) == null) {
            throw new VehicleNotFoundException("Cannot remove. Vehicle with ID '" + id + "' not found.");
        }
        logger.info("Removed vehicle from fleet: " + id);
    }

    /**
     * Assigns a delivery route to an available vehicle and starts a background
     * RouteMonitor thread to simulate real-time tracking.
     *
     * @param vehicleId   the ID of the vehicle to assign
     * @param routeId     the unique ID for the new route
     * @param origin      the starting location
     * @param destination the destination location
     * @param distanceKm  the total route distance in kilometers
     * @return the newly created Route object
     * @throws VehicleNotFoundException    if no vehicle with the given ID exists
     * @throws VehicleUnavailableException if the vehicle is not in AVAILABLE status
     */
    public Route assignRoute(String vehicleId, String routeId, String origin, String destination, double distanceKm)
            throws VehicleNotFoundException, VehicleUnavailableException {

        Vehicle v = getVehicle(vehicleId);

        // Prevent double-booking: only AVAILABLE vehicles can be assigned
        if (v.getStatus() != Vehicle.VehicleStatus.AVAILABLE) {
            throw new VehicleUnavailableException("Vehicle '" + vehicleId + "' is not available (current status: " + v.getStatus() + ").");
        }

        // Create the route with the assigned vehicle ID
        Route route = new Route(routeId, origin, destination, distanceKm, vehicleId);

        // Create and start the RouteMonitor thread for concurrent tracking
        RouteMonitor monitor = new RouteMonitor(route, v, (completedRoute, completedVehicle) -> {
            // Callback fires when route completes — log the event
            logger.info("Route completed callback: " + completedRoute.getRouteId()
                    + ". Vehicle " + completedVehicle.getId() + " is now AVAILABLE.");
        });

        routes.add(route);
        Thread monitorThread = new Thread(monitor, "RouteMonitor-" + routeId);
        monitorThread.setDaemon(true); // Allow JVM to exit even if routes are active
        monitorThread.start();

        logger.info("Assigned route " + routeId + " to vehicle " + vehicleId + " (" + origin + " -> " + destination + ", " + distanceKm + " km)");
        return route;
    }

    /**
     * Returns all routes currently in IN_PROGRESS status.
     *
     * @return a filtered list of active routes
     */
    public List<Route> getActiveRoutes() {
        return routes.stream()
                .filter(r -> r.getStatus() == Route.RouteStatus.IN_PROGRESS)
                .collect(Collectors.toList());
    }

    /**
     * Returns all vehicles whose trip count has reached the maintenance threshold.
     *
     * @return a filtered list of vehicles needing maintenance
     */
    public List<Vehicle> getMaintenanceFlaggedVehicles() {
        return vehicleRoster.values().stream()
                .filter(Vehicle::needsMaintenance)
                .collect(Collectors.toList());
    }
}
