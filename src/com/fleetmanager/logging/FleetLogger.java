package com.fleetmanager.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Thread-safe singleton logger for the fleet manager application.
 * Writes logs to a file and standard output.
 * 
 * @author Shivang
 */
public class FleetLogger {

    private static FleetLogger instance;
    private BufferedWriter writer;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private FleetLogger() {
        try {
            File logDir = new File("logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            File logFile = new File(logDir, "fleet.log");
            writer = new BufferedWriter(new FileWriter(logFile, true));
        } catch (IOException e) {
            System.err.println("Failed to initialize FleetLogger: " + e.getMessage());
        }
    }

    public static synchronized FleetLogger getInstance() {
        if (instance == null) {
            instance = new FleetLogger();
        }
        return instance;
    }

    private synchronized void log(String level, String msg) {
        String timestamp = LocalDateTime.now().format(formatter);
        String threadName = Thread.currentThread().getName();
        String logLine = String.format("[%s] [%s] [%s] %s", timestamp, level, threadName, msg);
        
        System.out.println(logLine);
        
        if (writer != null) {
            try {
                writer.write(logLine);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                System.err.println("Failed to write log: " + e.getMessage());
            }
        }
    }

    public synchronized void info(String msg) {
        log("INFO", msg);
    }

    public synchronized void warn(String msg) {
        log("WARN", msg);
    }

    public synchronized void error(String msg) {
        log("ERROR", msg);
    }

    public synchronized void close() {
        if (writer != null) {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                System.err.println("Failed to close logger: " + e.getMessage());
            } finally {
                writer = null;
            }
        }
    }
}
