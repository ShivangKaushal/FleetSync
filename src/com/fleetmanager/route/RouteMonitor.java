package com.fleetmanager.route;

import com.fleetmanager.core.Vehicle;
import com.fleetmanager.logging.FleetLogger;
import java.util.Random;

/**
 * A thread-based monitor that simulates a vehicle traveling along a route.
 * Implements {@link Runnable} to demonstrate Java multithreading concepts.
 * Each tick (every 2 seconds) the vehicle advances 15-30 km along the route,
 * with progress logged to the fleet log file.
 *
 * <p>On route completion, the vehicle status is restored to AVAILABLE, its trip
 * count is incremented, and a maintenance warning is logged if the threshold is met.</p>
 *
 * @author Shivang
 */
public class RouteMonitor implements Runnable {

    /**
     * Functional callback interface invoked when a route is completed.
     */
    @FunctionalInterface
    public interface RouteCompletionCallback {
        /**
         * Called when the monitored route finishes.
         *
         * @param route   the completed route
         * @param vehicle the vehicle that completed the route
         */
        void onRouteCompleted(Route route, Vehicle vehicle);
    }

    /** The route being monitored. */
    private final Route route;

    /** The vehicle assigned to this route. */
    private final Vehicle vehicle;

    /** Callback to fire upon route completion. */
    private final RouteCompletionCallback callback;

    /** Random number generator for simulating variable progress increments. */
    private final Random random;

    /**
     * Constructs a new RouteMonitor.
     *
     * @param route    the route to monitor
     * @param vehicle  the vehicle assigned to the route
     * @param callback the callback to execute upon route completion
     */
    public RouteMonitor(Route route, Vehicle vehicle, RouteCompletionCallback callback) {
        this.route = route;
        this.vehicle = vehicle;
        this.callback = callback;
        this.random = new Random();
    }

    /**
     * Runs the route monitoring simulation. This method is executed in a
     * separate thread and performs the following lifecycle:
     * <ol>
     *   <li>Sets route status to IN_PROGRESS and vehicle status to ON_ROUTE</li>
     *   <li>Loops every 2 seconds, advancing 15-30 km per tick</li>
     *   <li>Logs progress at each tick</li>
     *   <li>On completion: restores vehicle to AVAILABLE, increments trip count</li>
     *   <li>Warns if the vehicle now requires maintenance</li>
     *   <li>Fires the completion callback</li>
     * </ol>
     */
    @Override
    public void run() {
        FleetLogger logger = FleetLogger.getInstance();

        // Mark route and vehicle as active
        route.setStatus(Route.RouteStatus.IN_PROGRESS);
        vehicle.setStatus(Vehicle.VehicleStatus.ON_ROUTE);

        logger.info("Started route " + route.getRouteId()
                + " with vehicle " + vehicle.getId()
                + " (" + route.getOrigin() + " -> " + route.getDestination() + ")");

        try {
            // Simulation loop: advance progress every 2 seconds
            while (!route.isComplete()) {
                Thread.sleep(2000);

                // Random increment between 15.0 and 30.0 km
                double increment = 15.0 + (15.0 * random.nextDouble());
                route.advanceProgress(increment);

                String logMsg = String.format("Route %s: %.1f/%.1f km — Vehicle %s",
                        route.getRouteId(),
                        route.getProgressKm(),
                        route.getDistanceKm(),
                        vehicle.getId());
                logger.info(logMsg);
            }

            // Route complete — update statuses
            route.setStatus(Route.RouteStatus.COMPLETED);
            vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            vehicle.incrementTripCount();

            logger.info("Route " + route.getRouteId()
                    + " COMPLETED by vehicle " + vehicle.getId()
                    + " (total trips: " + vehicle.getTripCount() + ")");

            // Check if vehicle now needs maintenance
            if (vehicle.needsMaintenance()) {
                logger.warn("Vehicle " + vehicle.getId()
                        + " has reached " + vehicle.getTripCount()
                        + " trips and requires maintenance!");
            }

            // Fire the completion callback
            if (callback != null) {
                callback.onRouteCompleted(route, vehicle);
            }

        } catch (InterruptedException e) {
            logger.warn("RouteMonitor interrupted for route "
                    + route.getRouteId() + ": " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore the interrupted flag
        }
    }
}
