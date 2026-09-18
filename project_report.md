# Project Report: Distributed Fleet Management & Logistics Tracker
**Course:** Programming in Java

---

## 1. Project Objective
The objective of this project is to implement a modular, concurrent Java console application designed to manage vehicle rosters, assign delivery routes, and monitor maintenance in a real-world logistics context. The system addresses core industry challenges by tracking real-time vehicle availability, preventing the double-booking of active units, and tracking concurrent deliveries without freezing the primary user interface. Furthermore, it ensures high reliability by persisting fleet and trip records across sessions using an embedded relational database.

## 2. Syllabus Mapping & Application
This project was specifically architected to demonstrate core concepts from the Programming in Java syllabus:

* **Object-Oriented Programming (OOP) & Inheritance:** The domain model is structured around an abstract base class `Vehicle`, extended by specialized subclasses `CargoTruck` and `RefrigeratedVan`. It leverages the `super` keyword, method overriding, and runtime polymorphism, while strictly encapsulating attributes with private access modifiers and public accessors.
* **Multithreading & Synchronization:** To simulate real-time transit telemetry concurrently, the system implements the `Runnable` interface in `RouteMonitor` executed as background daemon threads. Thread safety during progress updates across multiple active routes is guaranteed via synchronized methods in the `Route` model.
* **Collections Framework & Exception Handling:** Roster state and fast $O(1)$ lookups are managed in memory using `HashMap<String, Vehicle>`, while dynamic trip schedules utilize `ArrayList<Route>`. Application stability is safeguarded through custom checked exceptions (such as `VehicleUnavailableException` and `RouteAssignmentException`) that enforce business constraints without program crashes.
* **Database Connectivity (JDBC) & I/O Streams:** Persistent storage is implemented via SQLite using JDBC `DriverManager`, `PreparedStatement`, and `ResultSet` abstractions. File I/O utilizes character-oriented streams (`BufferedReader` and `BufferedWriter`) for bulk-loading pipe-delimited vehicle rosters and exporting timestamped maintenance reports.

### 2.1 The Architectural Flow
The structural diagram below illustrates the flow from the command-line interface to the controller, concurrency threads, persistent database, and disk I/O layers.

```
┌─────────────────────────────────────────────────────────────────┐
│                         Main.java                               │
│                  (Interactive Console Menu)                     │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                      FleetManager.java                          │
│         Central Controller (HashMap + ArrayList)                │
│                                                                 │
│  ┌──────────────┐  ┌───────────────┐  ┌───────────────────┐    │
│  │ Vehicle (abs)│  │    Route      │  │   RouteMonitor    │    │
│  │  ┌─────────┐ │  │  (data model) │  │ (Runnable thread) │    │
│  │  │CargoTruck││  └───────────────┘  └───────────────────┘    │
│  │  ├─────────┤ │                                               │
│  │  │RefrigVan│ │                                               │
│  │  └─────────┘ │                                               │
│  └──────────────┘                                               │
└────────┬──────────────────┬──────────────────┬──────────────────┘
         │                  │                  │
         ▼                  ▼                  ▼
┌──────────────┐  ┌──────────────────┐  ┌──────────────┐
│DatabaseHelper│  │   FileIOHelper   │  │  FleetLogger │
│  (JDBC/SQL)  │  │ (Reader/Writer)  │  │ (Singleton)  │
└──────┬───────┘  └───────┬──────────┘  └──────┬───────┘
       │                  │                    │
       ▼                  ▼                    ▼
  fleet_data.db    vehicles_init.txt      logs/fleet.log
                   maintenance_report
```

## 3. System Architecture
The project employs a decoupled architecture, separating presentation, business logic, data persistence, and utility logging to adhere to clean engineering practices:

* **The Controller Layer (`FleetManager.java`):** Acts as the central brain of the application. It owns the in-memory collections, enforces validation checks prior to route dispatch, triggers background threads, and handles trip lifecycle events.
* **The Interface Layer (`Main.java`):** Serves as the interactive Command-Line Interface (CLI) menu. It captures user commands, coordinates inputs, and displays formatted status outputs without housing operational logic.
* **The Persistence & Utility Layer:** Consists of `DatabaseHelper.java` for SQLite JDBC operations, `FileIOHelper.java` for character-stream operations, and `FleetLogger.java`, a thread-safe singleton that logs system operations and audit trails with ISO timestamps to `logs/fleet.log`.

## 4. Implementation Details
The core logistics workflow operates through an event-driven, state-validated cycle:
1. Upon startup, `DatabaseHelper` ensures the SQLite schema is initialized and hydrates the in-memory cache in `FleetManager`.
2. The user can bulk-import records from `data/vehicles_init.txt` using `FileIOHelper`, parsing pipe-delimited data directly into vehicle entity objects.
3. When a route is assigned via the CLI, `FleetManager` inspects the vehicle state; if the vehicle is in `ON_ROUTE` or `MAINTENANCE` status, a `VehicleUnavailableException` is thrown to halt execution.
4. Upon successful validation, the vehicle status transitions to `ON_ROUTE` and a daemon thread running `RouteMonitor` spawns, updating simulated transit distances every 2 seconds.
5. Upon route completion, vehicle trip counts are incremented and written back to SQLite. If trip counts hit the maintenance threshold ($\ge 10$), an automated flag is triggered, preventing further assignments until servicing is logged.

## 5. Critical Reflection & Future Directions
**Algorithmic & Concurrency Analysis:** The current implementation reliably handles concurrent delivery simulation on a single machine using Java daemon threads and in-memory synchronization. Because the workload runs locally, memory utilization and thread context switching remain nominal.

However, scaling the tracker to thousands of concurrent vehicles in a distributed commercial enterprise would overwhelm standard JVM thread pools and risk thread starvation. For future development, the system should be upgraded from standard `Thread` objects to Java Virtual Threads (Project Loom) or an asynchronous event loop architecture. Furthermore, the local SQLite database should be migrated to a distributed PostgreSQL cluster accessed via the Java Persistence API (JPA/Hibernate) to provide connection pooling, automatic schema migrations, and high-concurrency write handling.

## 6. Conclusion
The Distributed Fleet Management & Logistics Tracker successfully bridges abstract Java programming constructs with a real-world enterprise use case. It fulfills all technical requirements of the flipped course project component—including CLI executability, clean package modularity, and strict zero-crash exception handling—while demonstrating a firm practical grasp of object-oriented architecture, database persistence, multithreaded simulation, and stream I/O.

## 7. Author

**Made by:** Shivang Kaushal