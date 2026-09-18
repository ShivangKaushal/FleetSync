# 🎯 FleetSync

A highly concurrent, modular Java console application designed to manage vehicle rosters, assign delivery routes, and monitor maintenance in a real-world logistics context . 

This project was developed for the **Programming in Java** course. It bridges core object-oriented principles with advanced runtime capabilities like multithreading and database persistence to solve complex logistical challenges  .

---

## 🧠 Core Java Concepts 

This system is architected to explicitly demonstrate the foundational concepts of Java programming:

* **Multithreading & Concurrency:** The system simulates real-time vehicle tracking without freezing the UI . Daemon threads (`RouteMonitor`) utilize `Runnable` and `synchronized` methods to concurrently process active deliveries safely .
* **Object-Oriented Programming (OOP):** Strict adherence to abstraction and inheritance . An abstract `Vehicle` base class is extended by specialized `CargoTruck` and `RefrigeratedVan` classes utilizing method overriding and the `super` keyword .
* **Data Persistence & File I/O:** Bridges in-memory collections (`HashMap`/`ArrayList`) with durable storage . Uses JDBC (SQLite) for SQL-based CRUD operations, and Character-oriented streams to bulk-load initialization data and export reports .
* **Robust Exception Handling:** Enforces business logic and prevents crashes using custom, user-defined exceptions (e.g., `VehicleUnavailableException`) .

---

## ⚙️ System Architecture: How It Works

The project adheres to strict Separation of Concerns (SoC) across 13 classes  :

1. **The Core Engine (`FleetManager.java`):** Central controller that manages in-memory data structures and validates business rules before assigning routes .
2. **The Interface (`Main.java`):** An interactive Command-Line Interface (CLI) that routes user commands without handling business logic directly .
3. **The Utility Layer:** Dedicated helper classes abstract external system interactions . `DatabaseHelper` manages SQL execution, `FileIOHelper` handles disk writes, and a thread-safe singleton `FleetLogger` records all operations to `logs/fleet.log` .

---

## 💻 Setup & Installation

**Prerequisites:**
* **JDK 8+** (Verify with `java -version`) 
* **SQLite JDBC Driver:** Download `sqlite-jdbc-3.46.0.0.jar` 

> ⚠️ **CRITICAL:** Place the `sqlite-jdbc-3.46.0.0.jar` file directly in the project root directory alongside the `src/` folder for the classpath to resolve correctly  .

**1. Compile the Source Code**

*Windows (Command Prompt / PowerShell):*
```bash
javac -cp ".;sqlite-jdbc-3.46.0.0.jar" -d out src/com/fleetmanager/exceptions/*.java src/com/fleetmanager/logging/*.java src/com/fleetmanager/core/*.java src/com/fleetmanager/route/*.java src/com/fleetmanager/db/*.java src/com/fleetmanager/io/*.java src/com/fleetmanager/*.java
```

*Linux / macOS:*
```bash
javac -cp ".:sqlite-jdbc-3.46.0.0.jar" -d out src/com/fleetmanager/exceptions/*.java src/com/fleetmanager/logging/*.java src/com/fleetmanager/core/*.java src/com/fleetmanager/route/*.java src/com/fleetmanager/db/*.java src/com/fleetmanager/io/*.java src/com/fleetmanager/*.java
```

**2. Execute the Application**

*Windows:*
```bash
java -cp "out;sqlite-jdbc-3.46.0.0.jar" com.fleetmanager.Main
```

*Linux / macOS:*
```bash
java -cp "out:sqlite-jdbc-3.46.0.0.jar" com.fleetmanager.Main
```

---

## 🚀 Execution & Operational Scenarios

The system operates via an interactive CLI menu  .

### Scenario 1: Initializing the Fleet
Bulk load seed data from text files and verify persistence .

**Expected Terminal Output:**
```text
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
Enter your choice: 8

[>] Loading vehicles from file (data/vehicles_init.txt)...
[+] SUCCESS: 6 vehicle(s) loaded and saved to database.
```

### Scenario 2: Concurrent Route Tracking & Double-Booking Prevention
Assigns a vehicle to a route, triggering a background thread . Attempting to assign the same vehicle immediately throws an exception.

**Expected Terminal Output:**
```text
Enter your choice: 5
Enter Vehicle ID: VH001
Enter Route ID: RT001
Enter Route Distance (km): 1400

[+] Route assigned! Background tracking started for VH001.

Enter your choice: 5
Enter Vehicle ID: VH001
[!] ERROR (VehicleUnavailableException): Vehicle VH001 is currently ON_ROUTE and cannot be assigned.
```
*(Meanwhile, in the background / logs)*
```text
[INFO] [RouteMonitor-RT001] Route RT001: 22.4/1400.0 km — Vehicle VH001
```

---

## 📁 Repository Map

* `src/com/fleetmanager/Main.java` — Interactive CLI menu and execution entry point .
* `src/com/fleetmanager/core/` — OOP data models (`Vehicle.java`, `CargoTruck.java`, `RefrigeratedVan.java`, and `FleetManager.java`) .
* `src/com/fleetmanager/route/` — Concurrency implementations (`Route.java` and `RouteMonitor.java`) .
* `src/com/fleetmanager/db/DatabaseHelper.java` — JDBC CRUD utility handling SQLite connections .
* `src/com/fleetmanager/io/FileIOHelper.java` — Character-stream Reader/Writer for text files and reports .
* `src/com/fleetmanager/logging/FleetLogger.java` — Thread-safe singleton logging utility .
* `src/com/fleetmanager/exceptions/` — User-defined exception classes for validation and error handling .
* `data/vehicles_init.txt` — Pipe-delimited seed data for bulk loading .
* `sqlite-jdbc-3.46.0.0.jar` — Required SQLite JDBC driver .
* `project_report.md` — Detailed system architecture and syllabus mapping documentation.

---

## 👨‍💻 Author

**Made by:** Shivang Kaushal