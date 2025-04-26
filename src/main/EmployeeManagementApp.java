/*
 * Krutik Bajariya
 * PDP Project
 * Employee Management System
 * Used VSCode for the project
 * EmployeeManagementApp.java
 */
package main;

import controller.EmployeeController;
import view.LoginGUI;
import util.Logger;
import javax.swing.UIManager;

/**
 * The EmployeeManagementApp class serves as the entry point for the Employee Management System application.
 * It initializes the EmployeeController and LoginGUI to start the application.
 * Updated to start with login screen.
 */
public class EmployeeManagementApp {
    private static final Logger logger = Logger.getInstance();
    
    /**
     * The main method to launch the Employee Management System application.
     * 
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            // Set look and feel to system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.error("Failed to set look and feel", e);
        }
        
        logger.info("Starting Employee Management System");
        
        // Initialize controller
        EmployeeController controller = new EmployeeController();
        
        // Start with login screen
        new LoginGUI(controller);
    }
}