package com.fleetmanager.db;

import com.fleetmanager.core.CargoTruck;
import com.fleetmanager.core.RefrigeratedVan;
import com.fleetmanager.core.Vehicle;
import com.fleetmanager.core.Vehicle.VehicleStatus;
import com.fleetmanager.exceptions.DatabaseConnectionException;
import com.fleetmanager.exceptions.VehicleNotFoundException;
import com.fleetmanager.logging.FleetLogger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class exclusively handling JDBC API connections and CRUD operations
 * for persisting Vehicle data in a SQLite relational database.
 *
 * <p>Uses {@link PreparedStatement} to prevent SQL injection and wraps all
 * {@link SQLException}s in {@link DatabaseConnectionException} for cleaner
 * error propagation.</p>
 *
 * @author Shivang
 */
public class DatabaseHelper {

    /** Active JDBC connection to the SQLite database. */
    private Connection connection;

    /** Singleton logger instance. */
    private final FleetLogger logger = FleetLogger.getInstance();

    /** JDBC connection URL for the SQLite database file. */
    private static final String URL = "jdbc:sqlite:fleet_data.db";

    /**
     * Constructs a new DatabaseHelper, opening a JDBC connection and
     * initializing the database schema.
     *
     * @throws DatabaseConnectionException if the connection or schema creation fails
     */
    public DatabaseHelper() throws DatabaseConnectionException {
        try {
            connection = DriverManager.getConnection(URL);
            logger.info("Connected to database: " + URL);
            initializeDatabase();
        } catch (SQLException e) {
            logger.error("Failed to connect to database: " + e.getMessage());
            throw new DatabaseConnectionException("Failed to connect to database", e);
        }
    }

    /**
     * Creates the vehicles table if it does not already exist.
     * The {@code extra_field} column stores type-specific data
     * (payload tons for CargoTruck, min temperature for RefrigeratedVan).
     *
     * @throws DatabaseConnectionException if the table creation fails
     */
    private void initializeDatabase() throws DatabaseConnectionException {
        String sql = "CREATE TABLE IF NOT EXISTS vehicles ("
                + "id TEXT PRIMARY KEY, "
                + "license_plate TEXT NOT NULL, "
                + "model TEXT NOT NULL, "
                + "vehicle_type TEXT NOT NULL, "
                + "status TEXT NOT NULL DEFAULT 'AVAILABLE', "
                + "trip_count INTEGER NOT NULL DEFAULT 0, "
                + "extra_field TEXT"
                + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            logger.info("Database initialized with vehicles table.");
        } catch (SQLException e) {
            logger.error("Failed to initialize database: " + e.getMessage());
            throw new DatabaseConnectionException("Failed to initialize database", e);
        }
    }

    /**
     * Inserts a new vehicle record into the database.
     *
     * @param v the Vehicle to persist
     * @throws DatabaseConnectionException if the insert operation fails
     */
    public void insertVehicle(Vehicle v) throws DatabaseConnectionException {
        String sql = "INSERT INTO vehicles (id, license_plate, model, vehicle_type, status, trip_count, extra_field) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, v.getId());
            pstmt.setString(2, v.getLicensePlate());
            pstmt.setString(3, v.getModel());

            // Determine vehicle type and type-specific extra field
            String vehicleType = "";
            String extraField = "";

            if (v instanceof CargoTruck) {
                vehicleType = "CARGO_TRUCK";
                extraField = String.valueOf(((CargoTruck) v).getMaxPayloadTons());
            } else if (v instanceof RefrigeratedVan) {
                vehicleType = "REFRIGERATED_VAN";
                extraField = String.valueOf(((RefrigeratedVan) v).getMinTemperatureCelsius());
            }

            pstmt.setString(4, vehicleType);
            pstmt.setString(5, v.getStatus().name());
            pstmt.setInt(6, v.getTripCount());
            pstmt.setString(7, extraField);

            pstmt.executeUpdate();
            logger.info("Inserted vehicle into DB: " + v.getId());
        } catch (SQLException e) {
            logger.error("Failed to insert vehicle: " + e.getMessage());
            throw new DatabaseConnectionException("Failed to insert vehicle", e);
        }
    }

    /**
     * Retrieves all vehicle records from the database, reconstructing the
     * appropriate subclass (CargoTruck or RefrigeratedVan) based on the
     * stored {@code vehicle_type} column.
     *
     * @return a list of all persisted vehicles
     * @throws DatabaseConnectionException if the query fails
     */
    public List<Vehicle> getAllVehicles() throws DatabaseConnectionException {
        String sql = "SELECT * FROM vehicles";
        List<Vehicle> vehicles = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
            logger.info("Retrieved all vehicles from DB, count: " + vehicles.size());
            return vehicles;
        } catch (SQLException e) {
            logger.error("Failed to retrieve all vehicles: " + e.getMessage());
            throw new DatabaseConnectionException("Failed to retrieve vehicles", e);
        }
    }

    /**
     * Retrieves a single vehicle by its ID.
     *
     * @param id the unique vehicle ID to look up
     * @return the Vehicle object
     * @throws DatabaseConnectionException if the query fails
     * @throws VehicleNotFoundException    if no vehicle with the given ID exists
     */
    public Vehicle getVehicleById(String id) throws DatabaseConnectionException, VehicleNotFoundException {
        String sql = "SELECT * FROM vehicles WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Vehicle v = mapResultSetToVehicle(rs);
                    logger.info("Retrieved vehicle from DB: " + id);
                    return v;
                } else {
                    logger.warn("Vehicle not found in DB: " + id);
                    throw new VehicleNotFoundException("Vehicle with ID '" + id + "' not found in database.");
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve vehicle by ID: " + e.getMessage());
            throw new DatabaseConnectionException("Failed to retrieve vehicle", e);
        }
    }

    /**
     * Updates an existing vehicle record in the database.
     *
     * @param v the Vehicle with updated fields
     * @throws DatabaseConnectionException if the update operation fails
     */
    public void updateVehicle(Vehicle v) throws DatabaseConnectionException {
        String sql = "UPDATE vehicles SET license_plate = ?, model = ?, vehicle_type = ?, status = ?, trip_count = ?, extra_field = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, v.getLicensePlate());
            pstmt.setString(2, v.getModel());

            String vehicleType = "";
            String extraField = "";

            if (v instanceof CargoTruck) {
                vehicleType = "CARGO_TRUCK";
                extraField = String.valueOf(((CargoTruck) v).getMaxPayloadTons());
            } else if (v instanceof RefrigeratedVan) {
                vehicleType = "REFRIGERATED_VAN";
                extraField = String.valueOf(((RefrigeratedVan) v).getMinTemperatureCelsius());
            }

            pstmt.setString(3, vehicleType);
            pstmt.setString(4, v.getStatus().name());
            pstmt.setInt(5, v.getTripCount());
            pstmt.setString(6, extraField);
            pstmt.setString(7, v.getId());

            pstmt.executeUpdate();
            logger.info("Updated vehicle in DB: " + v.getId());
        } catch (SQLException e) {
            logger.error("Failed to update vehicle: " + e.getMessage());
            throw new DatabaseConnectionException("Failed to update vehicle", e);
        }
    }

    /**
     * Deletes a vehicle record from the database by ID.
     *
     * @param id the ID of the vehicle to delete
     * @throws DatabaseConnectionException if the delete operation fails
     * @throws VehicleNotFoundException    if no vehicle with the given ID exists
     */
    public void deleteVehicle(String id) throws DatabaseConnectionException, VehicleNotFoundException {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                logger.warn("Attempted to delete non-existent vehicle: " + id);
                throw new VehicleNotFoundException("Vehicle with ID '" + id + "' not found in database.");
            }
            logger.info("Deleted vehicle from DB: " + id);
        } catch (SQLException e) {
            logger.error("Failed to delete vehicle: " + e.getMessage());
            throw new DatabaseConnectionException("Failed to delete vehicle", e);
        }
    }

    /**
     * Closes the JDBC database connection.
     * Safe to call multiple times; subsequent calls are no-ops.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed.");
            } catch (SQLException e) {
                logger.error("Failed to close database connection: " + e.getMessage());
            }
        }
    }

    /**
     * Maps a database ResultSet row to the appropriate Vehicle subclass.
     *
     * @param rs the ResultSet positioned at the current row
     * @return a CargoTruck or RefrigeratedVan instance
     * @throws SQLException if a column read fails or the vehicle type is unknown
     */
    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String licensePlate = rs.getString("license_plate");
        String model = rs.getString("model");
        String vehicleType = rs.getString("vehicle_type");
        String statusStr = rs.getString("status");
        int tripCount = rs.getInt("trip_count");
        String extraField = rs.getString("extra_field");

        // Parse vehicle status, defaulting to AVAILABLE for unknown values
        VehicleStatus status;
        try {
            status = VehicleStatus.valueOf(statusStr);
        } catch (IllegalArgumentException | NullPointerException e) {
            status = VehicleStatus.AVAILABLE;
        }

        // Reconstruct the correct subclass based on stored vehicle type
        Vehicle vehicle;
        if ("CARGO_TRUCK".equals(vehicleType)) {
            double payload = (extraField != null) ? Double.parseDouble(extraField) : 0.0;
            vehicle = new CargoTruck(id, licensePlate, model, payload);
        } else if ("REFRIGERATED_VAN".equals(vehicleType)) {
            double minTemp = (extraField != null) ? Double.parseDouble(extraField) : 0.0;
            vehicle = new RefrigeratedVan(id, licensePlate, model, minTemp);
        } else {
            throw new SQLException("Unknown vehicle type in database: " + vehicleType);
        }

        vehicle.setStatus(status);
        vehicle.setTripCount(tripCount);

        return vehicle;
    }
}
