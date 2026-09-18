# Project Statement

## FleetSync

---

### Author

**Name:** Shivang Kaushal
**Project Title:** FleetSync 
**Technology:** Core Java (JDK 8+)  
**Database:** SQLite (JDBC)  
**Version Control:** Git

---

## 1. Problem Statement

Managing a fleet of logistics vehicles across distributed routes is a complex real-world challenge. Fleet operators need to:

- Maintain an accurate, up-to-date roster of all vehicles in the fleet
- Assign vehicles to delivery routes while preventing double-booking of already-active units
- Track multiple deliveries running simultaneously without blocking the main system
- Monitor vehicle health by counting completed trips and flagging units due for scheduled maintenance
- Persist fleet data reliably across application restarts to avoid data loss
- Import bulk vehicle data from external sources and export operational reports

**Without a centralized system**, fleet managers resort to manual spreadsheets, leading to scheduling conflicts, missed maintenance windows, and operational downtime.

This project solves these problems by implementing a **modular, console-based Fleet Management System** in Java that demonstrates industry-standard programming practices.

---

## 2. Proposed Solution

The application is a **menu-driven Java console system** composed of 13 classes organized into 7 packages. It provides:

| Capability | How It Works |
|---|---|
| **Vehicle CRUD** | Add, view, update, and delete vehicles with in-memory HashMap storage backed by a persistent SQLite database |
| **Route Assignment** | Validates vehicle availability before assignment; throws `VehicleUnavailableException` to prevent double-booking |
| **Concurrent Tracking** | Each assigned route spawns a background `RouteMonitor` thread that simulates real-time delivery progress |
| **Maintenance Alerts** | Automatically flags vehicles that have completed 10 or more trips for scheduled maintenance |
| **File I/O** | Bulk-loads vehicle data from pipe-delimited text files and exports formatted maintenance reports |
| **Operational Logging** | Records all system activity, thread states, and anomalies to `logs/fleet.log` with timestamps |

---

## 3. Scope

### In Scope

- Full CRUD operations on a fleet of Cargo Trucks and Refrigerated Vans
- Route assignment with availability validation and double-booking prevention
- Multi-threaded delivery simulation with real-time progress logging
- Trip-count-based maintenance flagging and alert system
- Persistent storage using SQLite relational database via JDBC
- Bulk data import from text files using character-oriented I/O streams
- Maintenance report generation and export to disk
- Thread-safe singleton logger for continuous operational monitoring
- Graceful error handling with custom exceptions — no application crashes

### Out of Scope

- Graphical User Interface (GUI) — the system uses a console-based CLI
- Network communication or distributed microservices
- GPS-based real-time vehicle tracking
- User authentication and role-based access control
- REST API endpoints

---

## 4. Objectives

1. **Apply Core Java Concepts** — Demonstrate mastery of OOP (abstract classes, inheritance, encapsulation, polymorphism), multithreading, exception handling, collections, I/O streams, and JDBC in a single cohesive application.

2. **Build a Modular Architecture** — Structure the codebase into well-defined packages with clear separation of concerns, achieving a minimum of 10+ meaningful classes.

3. **Implement Concurrent Processing** — Use Java threads to simulate real-time tracking of multiple active deliveries without freezing the main application logic.

4. **Enforce Data Integrity** — Prevent invalid operations (double-booking, missing vehicles) through custom exceptions and input validation.

5. **Demonstrate Persistence** — Store and retrieve fleet data across sessions using JDBC with a relational database.

6. **Produce Production-Quality Code** — Write heavily documented code with Javadoc comments, inline explanations, and a comprehensive README.

---

## 5. Technologies & Tools Used

| Category | Technology | Purpose |
|---|---|---|
| **Language** | Java (JDK 8+) | Core application logic |
| **Database** | SQLite | Lightweight relational data persistence |
| **JDBC Driver** | `sqlite-jdbc-3.46.0.0.jar` | Java-to-SQLite connectivity |
| **Build** | `javac` / `java` (standard JDK tools) | Compilation and execution |
| **Version Control** | Git | Source code management and history |
| **IDE** | Any Java-compatible IDE or text editor | Development environment |

---

## 6. Java Concepts Demonstrated

| # | Concept | Implementation Detail |
|---|---|---|
| 1 | **Abstract Classes** | `Vehicle` is declared `abstract` with an abstract method `getVehicleType()` |
| 2 | **Inheritance** | `CargoTruck` and `RefrigeratedVan` extend `Vehicle` |
| 3 | **`super` Keyword** | Subclass constructors call `super(id, licensePlate, model)` |
| 4 | **Method Overriding** | Both subclasses override `getVehicleType()` and `toString()` |
| 5 | **Encapsulation** | All class fields are `private` with `public` getters and setters |
| 6 | **Polymorphism** | `Vehicle` references hold `CargoTruck` or `RefrigeratedVan` objects |
| 7 | **Multithreading** | `RouteMonitor` implements `Runnable`, executed via `Thread` |
| 8 | **Synchronization** | `synchronized` methods in `Route` for thread-safe progress updates |
| 9 | **Custom Exceptions** | `VehicleUnavailableException`, `VehicleNotFoundException`, `RouteAssignmentException`, `DatabaseConnectionException` |
| 10 | **`try-catch-throw-throws`** | Used throughout for graceful error recovery |
| 11 | **`HashMap`** | `HashMap<String, Vehicle>` for O(1) fleet roster lookups |
| 12 | **`ArrayList`** | `ArrayList<Route>` for managing the collection of routes |
| 13 | **Character I/O Streams** | `BufferedReader`/`FileReader` for reading, `BufferedWriter`/`FileWriter` for writing |
| 14 | **JDBC** | `DriverManager`, `Connection`, `PreparedStatement`, `ResultSet` for database operations |
| 15 | **Singleton Pattern** | Thread-safe `FleetLogger` with `synchronized getInstance()` |
| 16 | **Functional Interface** | `RouteCompletionCallback` annotated with `@FunctionalInterface` |
| 17 | **Enums** | `VehicleStatus` and `RouteStatus` for type-safe status management |
| 18 | **Stream API** | Used in `FleetManager` for filtering vehicles and routes |

---

## 7. Class Architecture Summary

| # | Class | Package | Role |
|---|---|---|---|
| 1 | `Main` | `com.fleetmanager` | Entry point with interactive console menu |
| 2 | `Vehicle` | `com.fleetmanager.core` | Abstract base class for all vehicles |
| 3 | `CargoTruck` | `com.fleetmanager.core` | Concrete subclass with payload capacity |
| 4 | `RefrigeratedVan` | `com.fleetmanager.core` | Concrete subclass with temperature control |
| 5 | `FleetManager` | `com.fleetmanager.core` | Central controller managing collections and routing |
| 6 | `Route` | `com.fleetmanager.route` | Route data model with synchronized progress tracking |
| 7 | `RouteMonitor` | `com.fleetmanager.route` | Runnable thread for concurrent delivery simulation |
| 8 | `DatabaseHelper` | `com.fleetmanager.db` | JDBC utility for SQLite CRUD operations |
| 9 | `FileIOHelper` | `com.fleetmanager.io` | Character-stream file reader/writer |
| 10 | `FleetLogger` | `com.fleetmanager.logging` | Thread-safe singleton logger |
| 11 | `VehicleUnavailableException` | `com.fleetmanager.exceptions` | Thrown on double-booking attempts |
| 12 | `VehicleNotFoundException` | `com.fleetmanager.exceptions` | Thrown on failed vehicle lookups |
| 13 | `RouteAssignmentException` | `com.fleetmanager.exceptions` | Thrown on invalid route operations |
| 14 | `DatabaseConnectionException` | `com.fleetmanager.exceptions` | Wraps low-level SQL exceptions |

> **Total: 13 classes across 7 packages** (exceeds the 5–10 minimum requirement)

---

## 8. Expected Outcomes

Upon successful execution, the system will:

1. ✅ Allow the user to perform full CRUD operations on a fleet of vehicles via an interactive menu
2. ✅ Persist all vehicle data in a SQLite database, surviving application restarts
3. ✅ Prevent double-booking by validating vehicle availability before route assignment
4. ✅ Track multiple concurrent deliveries via background threads without freezing the UI
5. ✅ Automatically flag vehicles for maintenance after 10+ completed trips
6. ✅ Import bulk vehicle data from pipe-delimited text files
7. ✅ Export formatted maintenance reports to the local disk
8. ✅ Log all operations, thread activity, and errors to `logs/fleet.log`
9. ✅ Handle all edge cases gracefully via custom exceptions — no application crashes

---

## 9. Conclusion

This project demonstrates the practical application of **18 core Java concepts** in a single, cohesive real-world system. The modular architecture — with 13 classes across 7 well-defined packages — ensures maintainability, testability, and clear separation of concerns.

The Distributed Fleet Management & Logistics Tracker successfully addresses the challenges of vehicle roster management, route assignment with concurrency safety, and automated maintenance monitoring, providing a solid foundation that could be extended with a GUI, REST API, or GPS integration in future iterations.

---

*Prepared by: Shivang*

