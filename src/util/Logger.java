package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logger utility class for logging system events, errors, and information.
 */
public class Logger {
    private static final String LOG_FILE = "employee_system.log";
    private static Logger instance;
    
    /**
     * Log level enumeration.
     */
    public enum LogLevel {
        INFO,
        WARNING,
        ERROR,
        DEBUG
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private Logger() {
        // Initialize log file
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println("----- Logger initialized on " + getCurrentTimestamp() + " -----");
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }
    
    /**
     * Returns the singleton instance of the Logger.
     *
     * @return the singleton instance of the Logger
     */
    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }
    
    /**
     * Logs a message with the specified log level.
     *
     * @param level the log level
     * @param message the message to log
     */
    public void log(LogLevel level, String message) {
        String logEntry = getCurrentTimestamp() + " [" + level + "] " + message;
        
        // Print to console
        if (level == LogLevel.ERROR) {
            System.err.println(logEntry);
        } else {
            System.out.println(logEntry);
        }
        
        // Write to log file
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(logEntry);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
    
    /**
     * Logs an error message.
     *
     * @param message the error message
     */
    public void error(String message) {
        log(LogLevel.ERROR, message);
    }
    
    /**
     * Logs an error message with an exception.
     *
     * @param message the error message
     * @param e the exception
     */
    public void error(String message, Exception e) {
        log(LogLevel.ERROR, message + ": " + e.getMessage());
        
        // Write stack trace to log file
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.print(getCurrentTimestamp() + " [STACK TRACE] ");
            e.printStackTrace(writer);
        } catch (IOException ex) {
            System.err.println("Failed to write stack trace to log file: " + ex.getMessage());
        }
    }
    
    /**
     * Logs an info message.
     *
     * @param message the info message
     */
    public void info(String message) {
        log(LogLevel.INFO, message);
    }
    
    /**
     * Logs a warning message.
     *
     * @param message the warning message
     */
    public void warning(String message) {
        log(LogLevel.WARNING, message);
    }
    
    /**
     * Logs a debug message.
     *
     * @param message the debug message
     */
    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }
    
    /**
     * Gets the current timestamp formatted for logging.
     *
     * @return the formatted timestamp
     */
    private String getCurrentTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return dateFormat.format(new Date());
    }
}