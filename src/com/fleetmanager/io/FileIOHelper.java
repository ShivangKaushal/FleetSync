package com.fleetmanager.io;

import com.fleetmanager.core.CargoTruck;
import com.fleetmanager.core.RefrigeratedVan;
import com.fleetmanager.core.Vehicle;
import com.fleetmanager.logging.FleetLogger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for reading vehicle data from files and writing reports.
 * 
 * @author Shivang
 */
public class FileIOHelper {

    private static final FleetLogger logger = FleetLogger.getInstance();

    /**
     * Loads vehicles from a pipe-delimited text file.
     * Format per line: type|id|licensePlate|model|extraField
     * 
     * @param filePath The path to the file to read.
     * @return A list of loaded vehicles.
     * @throws RuntimeException if an IO error occurs.
     */
    public static List<Vehicle> loadVehiclesFromFile(String filePath) {
        List<Vehicle> vehicles = new ArrayList<>();
        logger.info("Loading vehicles from file: " + filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    String type = parts[0];
                    String id = parts[1];
                    String licensePlate = parts[2];
                    String model = parts[3];
                    String extraField = parts[4];

                    Vehicle vehicle = null;
                    try {
                        if ("CARGO_TRUCK".equalsIgnoreCase(type)) {
                            double payload = Double.parseDouble(extraField);
                            vehicle = new CargoTruck(id, licensePlate, model, payload);
                        } else if ("REFRIGERATED_VAN".equalsIgnoreCase(type)) {
                            double temp = Double.parseDouble(extraField);
                            vehicle = new RefrigeratedVan(id, licensePlate, model, temp);
                        }
                        
                        if (vehicle != null) {
                            vehicles.add(vehicle);
                            logger.info("Loaded vehicle: " + id);
                        } else {
                            logger.warn("Unknown vehicle type in file: " + type);
                        }
                    } catch (NumberFormatException e) {
                        logger.error("Invalid extra field format for vehicle " + id + ": " + extraField);
                    }
                } else {
                    logger.warn("Invalid line format: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("File not found: " + filePath);
            throw new RuntimeException("File not found: " + filePath, e);
        } catch (IOException e) {
            logger.error("IO Exception reading file: " + e.getMessage());
            throw new RuntimeException("Error reading file: " + filePath, e);
        }

        return vehicles;
    }

    /**
     * Writes a maintenance report for the given list of vehicles.
     * 
     * @param vehicles The list of vehicles.
     * @param outputPath The path where the report should be saved.
     */
    public static void writeMaintenanceReport(List<Vehicle> vehicles, String outputPath) {
        logger.info("Writing maintenance report to: " + outputPath);
        File outFile = new File(outputPath);
        File parentDir = outFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write("========================================");
            writer.newLine();
            writer.write("       MAINTENANCE REPORT");
            writer.newLine();
            writer.write("       Generated: " + timestamp);
            writer.newLine();
            writer.write("========================================");
            writer.newLine();
            writer.newLine();

            writer.write(String.format("%-10s | %-16s | %-15s | %-10s | %-10s", 
                    "ID", "Type", "License Plate", "Trip Count", "Needs Maint."));
            writer.newLine();
            writer.write("-".repeat(70));
            writer.newLine();

            for (Vehicle v : vehicles) {
                String type = (v instanceof CargoTruck) ? "Cargo Truck" : 
                              (v instanceof RefrigeratedVan) ? "Refrigerated" : "Unknown";
                String needsMaint = v.needsMaintenance() ? "YES" : "NO";
                
                writer.write(String.format("%-10s | %-16s | %-15s | %-10d | %-10s",
                        v.getId(), type, v.getLicensePlate(), v.getTripCount(), needsMaint));
                writer.newLine();
            }
            
            logger.info("Maintenance report generated successfully.");
        } catch (IOException e) {
            logger.error("Failed to write maintenance report: " + e.getMessage());
        }
    }
}
