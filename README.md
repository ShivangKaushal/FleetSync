# Distributed Fleet Management & Logistics Tracker

**Author:** Shivang

A modular Java console application that manages vehicle rosters, assigns delivery routes, and monitors maintenance — built with OOP, multithreading, JDBC, collections, I/O streams, and custom exceptions.

## Prerequisites

- **JDK 8** or higher
- **SQLite JDBC Driver** — download [`sqlite-jdbc-3.46.0.0.jar`](https://github.com/xerial/sqlite-jdbc/releases) and place it in the project root directory.

## Project Structure

```
Java Vityarthi Project/
├── README.md
├── data/
│   ├── vehicles_init.txt              # Bulk-load seed data (pipe-delimited)
│   └── reports/                       # Generated maintenance reports
├── logs/
│   └── fleet.log                      # Runtime log file (auto-created)
└── src/
    └── com/
        └── fleetmanager/
            ├── Main.java                          # CLI menu & entry point
            ├── core/
            │   ├── FleetManager.java              # Central controller (HashMap, ArrayList)
            │   ├── Vehicle.java                   # Abstract base class (OOP)
            │   ├── CargoTruck.java                # Subclass (inheritance, super, override)
            │   └── RefrigeratedVan.java           # Subclass (inheritance, super, override)
            ├── route/
            │   ├── Route.java                     # Route data model
            │   └── RouteMonitor.java              # Runnable thread for live tracking
            ├── db/
            │   └── DatabaseHelper.java            # JDBC CRUD utility (SQLite)
            ├── io/
            │   └── FileIOHelper.java              # Character-stream Reader/Writer
            ├── logging/
            │   └── FleetLogger.java               # Thread-safe singleton logger
            └── exceptions/
                ├── VehicleUnavailableException.java
                ├── VehicleNotFoundException.java
                ├── RouteAssignmentException.java
                └── DatabaseConnectionException.java
```

## Compilation & Execution

From the project root directory:

```bash
# Compile all source files
javac -cp ".;sqlite-jdbc-3.46.0.0.jar" -d out src/com/fleetmanager/exceptions/*.java src/com/fleetmanager/logging/*.java src/com/fleetmanager/core/*.java src/com/fleetmanager/route/*.java src/com/fleetmanager/db/*.java src/com/fleetmanager/io/*.java src/com/fleetmanager/*.java

# Run the application
java -cp "out;sqlite-jdbc-3.46.0.0.jar" com.fleetmanager.Main
```

> **Note (Linux/macOS):** Replace `;` with `:` in the classpath separator.

## Features

| Feature | Description |
|---|---|
| Vehicle CRUD | Add, view, update, and remove vehicles from the fleet |
| Route Assignment | Assign available vehicles to delivery routes with double-booking prevention |
| Live Tracking | Background threads simulate real-time delivery progress |
| Maintenance Alerts | Automatic flagging when vehicles reach 10+ completed trips |
| Data Persistence | SQLite database for durable vehicle storage across sessions |
| Bulk Loading | Import vehicles from pipe-delimited text files |
| Report Generation | Export maintenance status reports to disk |
| Logging | All operations logged to `logs/fleet.log` with timestamps |

## Java Concepts Demonstrated

- **Object-Oriented Programming** — Abstract classes (`Vehicle`), inheritance (`CargoTruck`, `RefrigeratedVan`), method overriding, `super` keyword, strict encapsulation with private fields and public getters/setters
- **Multithreading** — `Runnable` interface (`RouteMonitor`), `Thread` lifecycle, `synchronized` methods, daemon threads
- **Custom Exceptions** — `VehicleUnavailableException`, `VehicleNotFoundException`, `RouteAssignmentException`, `DatabaseConnectionException` with `try-catch`, `throw`, `throws`
- **Collections Framework** — `HashMap<String, Vehicle>` for O(1) roster lookups, `ArrayList<Route>` for route management
- **I/O Streams** — Character-oriented `BufferedReader`/`FileReader` and `BufferedWriter`/`FileWriter` for file parsing and report writing
- **Database Integration** — JDBC with SQLite, `PreparedStatement` for SQL injection safety, `ResultSet` parsing
- **Singleton Pattern** — Thread-safe `FleetLogger` singleton

## License

MIT License
