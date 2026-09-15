package com.fleetmanager;

import com.fleetmanager.core.CargoTruck;
import com.fleetmanager.core.FleetManager;
import com.fleetmanager.core.RefrigeratedVan;
import com.fleetmanager.core.Vehicle;
import com.fleetmanager.db.DatabaseHelper;
import com.fleetmanager.exceptions.DatabaseConnectionException;
import com.fleetmanager.exceptions.VehicleNotFoundException;
import com.fleetmanager.exceptions.VehicleUnavailableException;
import com.fleetmanager.io.FileIOHelper;
import com.fleetmanager.logging.FleetLogger;
import com.fleetmanager.route.Route;
import java.util.List;
import java.util.Scanner;

/**
 * Main class serves as the entry point for the Distributed Fleet Management System.
 * It provides an interactive console menu to manage vehicles, assign routes,
 * and generate reports. All operations are wrapped in try-catch blocks for
 * graceful error handling.
 *
 * @author Shivang
 */
public class Main {

    /**
     * Application entry point. Initializes the logger, database, and fleet manager,
     * then enters an interactive menu loop.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        FleetLogger logger = FleetLogger.getInstance();
        DatabaseHelper dbHelper = null;
        FleetManager fleetManager = new FleetManager();
        Scanner scanner = new Scanner(System.in);

        // Initialize the database connection and load persisted vehicles
        try {
            dbHelper = new DatabaseHelper();
            List<Vehicle> dbVehicles = dbHelper.getAllVehicles();
            for (Vehicle v : dbVehicles) {
                fleetManager.addVehicle(v);
            }
            logger.info("System initialized. " + dbVehicles.size() + " vehicle(s) loaded from database.");
        } catch (DatabaseConnectionException e) {
            System.err.println("WARNING: Could not connect to database: " + e.getMessage());
            System.err.println("The system will run in memory-only mode.");
            logger.error("Database initialization failed: " + e.getMessage());
        }

        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== Distributed Fleet Management System ===");
            System.out.println("1.  Add Vehicle");
            System.out.println("2.  View All Vehicles");
            System.out.println("3.  Update Vehicle");
            System.out.println("4.  Remove Vehicle");
            System.out.println("5.  Assign Route");
            System.out.println("6.  View Active Routes");
            System.out.println("7.  View Maintenance Alerts");
            System.out.println("8.  Load Vehicles from File");
            System.out.println("9.  Generate Maintenance Report");
            System.out.println("10. Exit");
            System.out.print("Enter your choice: ");

            String choiceStr = scanner.nextLine();
            int choice = -1;
            try {
                choice = Integer.parseInt(choiceStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 10.");
                continue;
            }

            try {
                switch (choice) {
                    case 1: // ---- Add Vehicle ----
                        System.out.print("Enter vehicle type (1=CargoTruck, 2=RefrigeratedVan): ");
                        int type = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Enter Vehicle ID: ");
                        String id = scanner.nextLine().trim();
                        System.out.print("Enter License Plate: ");
                        String licensePlate = scanner.nextLine().trim();
                        System.out.print("Enter Model: ");
                        String model = scanner.nextLine().trim();

                        Vehicle v = null;
                        if (type == 1) {
                            System.out.print("Enter Max Payload Capacity (tons): ");
                            double capacity = Double.parseDouble(scanner.nextLine().trim());
                            v = new CargoTruck(id, licensePlate, model, capacity);
                        } else if (type == 2) {
                            System.out.print("Enter Minimum Temperature (Celsius): ");
                            double temp = Double.parseDouble(scanner.nextLine().trim());
                            v = new RefrigeratedVan(id, licensePlate, model, temp);
                        } else {
                            System.out.println("Invalid vehicle type. Please enter 1 or 2.");
                            break;
                        }
                        fleetManager.addVehicle(v);
                        if (dbHelper != null) {
                            dbHelper.insertVehicle(v);
                        }
                        System.out.println("Vehicle '" + id + "' added successfully.");
                        break;

                    case 2: // ---- View All Vehicles ----
                        List<Vehicle> allVehicles = fleetManager.listAllVehicles();
                        if (allVehicles.isEmpty()) {
                            System.out.println("No vehicles in the fleet.");
                        } else {
                            System.out.println("\n--- Fleet Roster (" + allVehicles.size() + " vehicles) ---");
                            for (Vehicle veh : allVehicles) {
                                System.out.println("  " + veh);
                            }
                        }
                        break;

                    case 3: // ---- Update Vehicle ----
                        System.out.print("Enter Vehicle ID to update: ");
                        String updateId = scanner.nextLine().trim();
                        System.out.print("Enter new License Plate: ");
                        String newLicense = scanner.nextLine().trim();
                        System.out.print("Enter new Model: ");
                        String newModel = scanner.nextLine().trim();
                        fleetManager.updateVehicle(updateId, newLicense, newModel);
                        if (dbHelper != null) {
                            dbHelper.updateVehicle(fleetManager.getVehicle(updateId));
                        }
                        System.out.println("Vehicle '" + updateId + "' updated successfully.");
                        break;

                    case 4: // ---- Remove Vehicle ----
                        System.out.print("Enter Vehicle ID to remove: ");
                        String removeId = scanner.nextLine().trim();
                        fleetManager.removeVehicle(removeId);
                        if (dbHelper != null) {
                            dbHelper.deleteVehicle(removeId);
                        }
                        System.out.println("Vehicle '" + removeId + "' removed successfully.");
                        break;

                    case 5: // ---- Assign Route ----
                        System.out.print("Enter Vehicle ID: ");
                        String vId = scanner.nextLine().trim();
                        System.out.print("Enter Route ID: ");
                        String rId = scanner.nextLine().trim();
                        System.out.print("Enter Origin: ");
                        String origin = scanner.nextLine().trim();
                        System.out.print("Enter Destination: ");
                        String destination = scanner.nextLine().trim();
                        System.out.print("Enter Distance (km): ");
                        double dist = Double.parseDouble(scanner.nextLine().trim());
                        Route assignedRoute = fleetManager.assignRoute(vId, rId, origin, destination, dist);
                        System.out.println("Route '" + rId + "' assigned to vehicle '" + vId + "'. Live monitoring started.");
                        break;

                    case 6: // ---- View Active Routes ----
                        List<Route> activeRoutes = fleetManager.getActiveRoutes();
                        if (activeRoutes.isEmpty()) {
                            System.out.println("No active routes at this time.");
                        } else {
                            System.out.println("\n--- Active Routes (" + activeRoutes.size() + ") ---");
                            for (Route r : activeRoutes) {
                                System.out.println("  " + r);
                            }
                        }
                        break;

                    case 7: // ---- View Maintenance Alerts ----
                        List<Vehicle> maintenanceVehicles = fleetManager.getMaintenanceFlaggedVehicles();
                        if (maintenanceVehicles.isEmpty()) {
                            System.out.println("No vehicles require maintenance at this time.");
                        } else {
                            System.out.println("\n--- Maintenance Alerts (" + maintenanceVehicles.size() + " vehicles) ---");
                            for (Vehicle veh : maintenanceVehicles) {
                                System.out.println("  [!] " + veh);
                            }
                        }
                        break;

                    case 8: // ---- Load Vehicles from File ----
                        System.out.print("Enter file path (press Enter for default 'data/vehicles_init.txt'): ");
                        String path = scanner.nextLine().trim();
                        if (path.isEmpty()) {
                            path = "data/vehicles_init.txt";
                        }
                        List<Vehicle> loadedVehicles = FileIOHelper.loadVehiclesFromFile(path);
                        int addedCount = 0;
                        for (Vehicle loadedVeh : loadedVehicles) {
                            try {
                                fleetManager.addVehicle(loadedVeh);
                                if (dbHelper != null) {
                                    dbHelper.insertVehicle(loadedVeh);
                                }
                                addedCount++;
                            } catch (Exception ex) {
                                System.out.println("  Skipped vehicle '" + loadedVeh.getId() + "': " + ex.getMessage());
                            }
                        }
                        System.out.println("Loaded " + addedCount + " vehicle(s) from file.");
                        break;

                    case 9: // ---- Generate Maintenance Report ----
                        List<Vehicle> allVehiclesForReport = fleetManager.listAllVehicles();
                        String reportPath = "data/reports/maintenance_report.txt";
                        FileIOHelper.writeMaintenanceReport(allVehiclesForReport, reportPath);
                        System.out.println("Maintenance report generated at: " + reportPath);
                        break;

                    case 10: // ---- Exit ----
                        exit = true;
                        if (dbHelper != null) {
                            dbHelper.closeConnection();
                        }
                        logger.close();
                        System.out.println("Exiting Fleet Management System. Goodbye!");
                        break;

                    default:
                        System.out.println("Invalid option. Please choose between 1 and 10.");
                }
            } catch (VehicleNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (VehicleUnavailableException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (DatabaseConnectionException e) {
                System.out.println("Database error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number format. Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                logger.error("Unhandled exception in main menu: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
