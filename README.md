# Distributed Fleet Management & Logistics Tracker

**Author:** Shivang

---

## 1. Project Overview

### 1.1 Objective

This project implements a **Distributed Fleet Tracking System** — a modular Java console application designed to manage vehicle rosters, assign delivery routes, and monitor maintenance in a real-world logistics context. The system demonstrates core Java technologies including OOP, multithreading, JDBC, collections, I/O streams, and custom exception handling.

### 1.2 Problem Statement

Logistics companies operating large vehicle fleets face challenges in:
- Tracking which vehicles are available, on-route, or due for maintenance
- Preventing double-booking of vehicles already assigned to active deliveries
- Monitoring multiple concurrent deliveries in real-time without freezing the system
- Persisting fleet data across application restarts

This application addresses all of the above through a clean, modular architecture.

### 1.3 Key Modules

| Module | Responsibility |
|---|---|
| **Vehicle Roster Management** | Full CRUD operations (Create, Read, Update, Delete) on the fleet database with both in-memory and persistent storage |
| **Route Assignment Engine** | Assigns available vehicles to specific delivery routes, preventing double-booking via status validation |
| **Maintenance & Health Logging** | Tracks vehicle trip counts, auto-flags units for maintenance at 10+ trips, and generates status reports |
| **Concurrent Route Monitoring** | Background threads simulate real-time delivery tracking without freezing the main application |
| **Data Persistence** | SQLite relational database stores vehicle records across sessions via JDBC |
| **File I/O** | Bulk-load vehicles from text files and export formatted maintenance reports to disk |
| **Operational Logging** | Thread-safe singleton logger records all system activity to `logs/fleet.log` |

### 1.4 Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         Main.java                               │
│                  (Interactive Console Menu)                      │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                      FleetManager.java                          │
│         Central Controller (HashMap + ArrayList)                │
│                                                                 │
│  ┌──────────────┐  ┌───────────────┐  ┌───────────────────┐    │
│  │ Vehicle (abs) │  │    Route      │  │   RouteMonitor    │    │
│  │  ┌─────────┐ │  │  (data model) │  │  (Runnable thread)│    │
│  │  │CargoTruck│ │  └───────────────┘  └───────────────────┘    │
│  │  ├─────────┤ │                                               │
│  │  │Refrig.  │ │                                               │
│  │  │Van      │ │                                               │
│  │  └─────────┘ │                                               │
│  └──────────────┘                                               │
└────────┬──────────────────┬──────────────────┬──────────────────┘
         │                  │                  │
         ▼                  ▼                  ▼
┌──────────────┐  ┌──────────────────┐  ┌──────────────┐
│DatabaseHelper│  │   FileIOHelper   │  │  FleetLogger │
│  (JDBC/SQL)  │  │(Reader/Writer)   │  │  (Singleton) │
└──────┬───────┘  └───────┬──────────┘  └──────┬───────┘
       │                  │                    │
       ▼                  ▼                    ▼
  fleet_data.db    vehicles_init.txt      logs/fleet.log
                   maintenance_report
```

---

## 2. Prerequisites

- **JDK 8** or higher (verify with `java -version` and `javac -version`)
- **SQLite JDBC Driver** — download [`sqlite-jdbc-3.46.0.0.jar`](https://github.com/xerial/sqlite-jdbc/releases) and place it in the project root directory

> **Note:** No build tool (Maven/Gradle) is required. The project compiles and runs with standard `javac` and `java` commands.

---

## 3. Project Structure

```
Java Vityarthi Project/
├── .gitignore
├── README.md
├── sqlite-jdbc-3.46.0.0.jar          # JDBC driver (place here)
├── data/
│   ├── vehicles_init.txt              # Bulk-load seed data (pipe-delimited)
│   └── reports/                       # Generated maintenance reports (auto-created)
├── logs/
│   └── fleet.log                      # Runtime log file (auto-created)
├── src/
│   └── com/
│       └── fleetmanager/
│           ├── Main.java                          # CLI menu & entry point
│           ├── core/
│           │   ├── Vehicle.java                   # Abstract base class + VehicleStatus enum
│           │   ├── CargoTruck.java                # Subclass (inheritance, super, override)
│           │   ├── RefrigeratedVan.java           # Subclass (inheritance, super, override)
│           │   └── FleetManager.java              # Central controller (HashMap, ArrayList)
│           ├── route/
│           │   ├── Route.java                     # Route data model + RouteStatus enum
│           │   └── RouteMonitor.java              # Runnable thread for live tracking
│           ├── db/
│           │   └── DatabaseHelper.java            # JDBC CRUD utility (SQLite)
│           ├── io/
│           │   └── FileIOHelper.java              # Character-stream Reader/Writer
│           ├── logging/
│           │   └── FleetLogger.java               # Thread-safe singleton logger
│           └── exceptions/
│               ├── VehicleUnavailableException.java
│               ├── VehicleNotFoundException.java
│               ├── RouteAssignmentException.java
│               └── DatabaseConnectionException.java
└── out/                                           # Compiled .class files (auto-generated)
```

**Total Classes: 13** | **Total Packages: 7**

---

## 4. How to Compile & Run

### Step 1: Verify Prerequisites

```bash
# Check Java is installed
java -version
javac -version
```

### Step 2: Download the SQLite JDBC Driver

Download [`sqlite-jdbc-3.46.0.0.jar`](https://github.com/xerial/sqlite-jdbc/releases/tag/3.46.0.0) from the releases page and place the `.jar` file in the project root directory (same level as `README.md` and `src/`).

### Step 3: Compile All Source Files

Open a terminal in the project root directory and run:

**Windows (Command Prompt / PowerShell):**
```bash
javac -cp ".;sqlite-jdbc-3.46.0.0.jar" -d out src/com/fleetmanager/exceptions/*.java src/com/fleetmanager/logging/*.java src/com/fleetmanager/core/*.java src/com/fleetmanager/route/*.java src/com/fleetmanager/db/*.java src/com/fleetmanager/io/*.java src/com/fleetmanager/*.java
```

**Linux / macOS:** (use `:` instead of `;` as the classpath separator)
```bash
javac -cp ".:sqlite-jdbc-3.46.0.0.jar" -d out src/com/fleetmanager/exceptions/*.java src/com/fleetmanager/logging/*.java src/com/fleetmanager/core/*.java src/com/fleetmanager/route/*.java src/com/fleetmanager/db/*.java src/com/fleetmanager/io/*.java src/com/fleetmanager/*.java
```

This compiles all 13 source files into the `out/` directory. You should see **no errors**.

### Step 4: Run the Application

**Windows:**
```bash
java -cp "out;sqlite-jdbc-3.46.0.0.jar" com.fleetmanager.Main
```

**Linux / macOS:**
```bash
java -cp "out:sqlite-jdbc-3.46.0.0.jar" com.fleetmanager.Main
```

### Step 5: Use the Application

On launch, the system automatically:
1. Creates `fleet_data.db` (SQLite database) in the project root
2. Creates `logs/fleet.log` for operational logging
3. Loads any previously saved vehicles from the database

You will see the interactive menu:

```
=== Distributed Fleet Management System ===
1.  Add Vehicle
2.  View All Vehicles
3.  Update Vehicle
4.  Remove Vehicle
5.  Assign Route
6.  View Active Routes
7.  View Maintenance Alerts
8.  Load Vehicles from File
9.  Generate Maintenance Report
10. Exit
Enter your choice:
```

### Recommended First Run Workflow

1. **Option 8** → Load vehicles from file (press Enter for the default `data/vehicles_init.txt`) — loads 6 sample vehicles
2. **Option 2** → View all vehicles to confirm they were loaded
3. **Option 5** → Assign a route (e.g., Vehicle `VH001`, Route `RT001`, Mumbai → Delhi, 1400 km) — a background thread starts tracking
4. **Option 6** → View active routes to see real-time progress
5. **Option 5** → Try assigning the same vehicle again — you will see a `VehicleUnavailableException` (double-booking prevention)
6. **Option 7** → View maintenance alerts after vehicles complete 10+ trips
7. **Option 9** → Generate a maintenance report to `data/reports/maintenance_report.txt`
8. **Option 10** → Exit gracefully (closes DB connection and logger)

---

## 5. Features

| Feature | Description |
|---|---|
| **Vehicle CRUD** | Add, view, update, and remove vehicles from the fleet with full input validation |
| **Route Assignment** | Assign available vehicles to delivery routes with double-booking prevention |
| **Live Tracking** | Background daemon threads simulate real-time delivery progress (15–30 km every 2 seconds) |
| **Maintenance Alerts** | Automatic flagging when vehicles reach 10+ completed trips |
| **Data Persistence** | SQLite database stores vehicle records durably across application restarts |
| **Bulk Loading** | Import vehicles from pipe-delimited text files (`type\|id\|plate\|model\|extra`) |
| **Report Generation** | Export formatted maintenance status reports to disk with timestamps |
| **Operational Logging** | All operations logged to `logs/fleet.log` with ISO timestamps, log levels, and thread names |
| **Graceful Error Handling** | Custom exceptions, try-catch coverage on all menu options, no application crashes |

---

## 6. Seed Data Format

The file `data/vehicles_init.txt` uses pipe-delimited format:

```
type|id|licensePlate|model|extraField
```

| Field | Description | Example |
|---|---|---|
| `type` | `CARGO_TRUCK` or `REFRIGERATED_VAN` | `CARGO_TRUCK` |
| `id` | Unique vehicle identifier | `VH001` |
| `licensePlate` | Vehicle registration number | `KA-01-AB-1234` |
| `model` | Manufacturer and model name | `Tata Prima 4928` |
| `extraField` | Payload (tons) for trucks, min temp (°C) for vans | `18.5` or `-18.0` |

---

## 7. Java Concepts Demonstrated

| Concept | Where It's Used |
|---|---|
| **Abstract Classes** | `Vehicle` is abstract with `getVehicleType()` method |
| **Inheritance & `super`** | `CargoTruck` and `RefrigeratedVan` extend `Vehicle`, call `super()` |
| **Method Overriding** | Both subclasses override `getVehicleType()` and `toString()` |
| **Encapsulation** | All fields are `private` with `public` getters/setters |
| **Multithreading (`Runnable`)** | `RouteMonitor` implements `Runnable`, runs in daemon `Thread` |
| **Synchronization** | `synchronized` methods in `Route` for thread-safe progress updates |
| **Custom Exceptions** | 4 user-defined exceptions with `try-catch`, `throw`, `throws` |
| **`HashMap`** | `HashMap<String, Vehicle>` for O(1) roster lookups in `FleetManager` |
| **`ArrayList`** | `ArrayList<Route>` for managing active routes |
| **Character I/O Streams** | `BufferedReader`/`FileReader` and `BufferedWriter`/`FileWriter` in `FileIOHelper` |
| **JDBC Database** | SQLite via `DriverManager`, `PreparedStatement`, `ResultSet` in `DatabaseHelper` |
| **Singleton Pattern** | Thread-safe `FleetLogger` with `synchronized getInstance()` |
| **Functional Interface** | `RouteCompletionCallback` with `@FunctionalInterface` annotation |
| **Enums** | `VehicleStatus` (AVAILABLE, ON_ROUTE, MAINTENANCE) and `RouteStatus` (PENDING, IN_PROGRESS, COMPLETED) |
| **Stream API** | Used in `FleetManager` for filtering active routes and maintenance-flagged vehicles |

---

## 8. Database Schema

The SQLite database (`fleet_data.db`) contains a single table:

```sql
CREATE TABLE IF NOT EXISTS vehicles (
    id           TEXT PRIMARY KEY,
    license_plate TEXT NOT NULL,
    model        TEXT NOT NULL,
    vehicle_type TEXT NOT NULL,        -- 'CARGO_TRUCK' or 'REFRIGERATED_VAN'
    status       TEXT NOT NULL DEFAULT 'AVAILABLE',
    trip_count   INTEGER NOT NULL DEFAULT 0,
    extra_field  TEXT                   -- payload tons or min temperature
);
```

---

## 9. Log File Format

All operations are logged to `logs/fleet.log` in the following format:

```
[2026-09-15 22:15:30] [INFO] [main] System initialized. 6 vehicle(s) loaded from database.
[2026-09-15 22:15:35] [INFO] [RouteMonitor-RT001] Started route RT001 with vehicle VH001 (Mumbai -> Delhi)
[2026-09-15 22:15:37] [INFO] [RouteMonitor-RT001] Route RT001: 22.4/1400.0 km — Vehicle VH001
[2026-09-15 22:16:10] [WARN] [RouteMonitor-RT001] Vehicle VH001 has reached 10 trips and requires maintenance!
```

---

## 10. License

MIT License
