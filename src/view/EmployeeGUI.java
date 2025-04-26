/*
 * Krutik Bajariya
 * PDP Project
 * Employee Management System
 * Used VSCode for the project
 * EmployeeGUI.java
 */
package view;

import controller.EmployeeController;
import model.UserManager;
import model.User;
import util.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * The EmployeeGUI class represents the graphical user interface for the Employee Management System.
 * It provides options to add an employee, view the list of employees, pay all employees,
 * and generate reports.
 * Updated to include report generation functionality.
 */
public class EmployeeGUI {
    private JFrame frame;
    private EmployeeController controller;

    private final Logger logger = Logger.getInstance();
    private UserManager userManager = UserManager.getInstance();
    private JLabel statusLabel;
    
    /**
     * Constructs an EmployeeGUI object.
     *
     * @param controller the EmployeeController that handles the business logic for the Employee Management System
     */
    public EmployeeGUI(EmployeeController controller) {
        this.controller = controller;
        
        // Set window title based on user role
        String windowTitle = "Employee Management System";
        if (userManager.isAuthenticated()) {
            User currentUser = userManager.getCurrentUser();
            windowTitle += " - " + currentUser.getUsername() + " (" + currentUser.getRole() + ")";
        }
        
        frame = new JFrame(windowTitle);
        frame.setSize(600, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);  // Center on screen
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Title label
        JLabel titleLabel = new JLabel("Employee Management System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        // Add Employee button - only for managers and admins
        JButton addButton = new JButton("Add Employee");
        if (userManager.isCurrentUserManager()) {
            buttonPanel.add(addButton);
            addButton.addActionListener(e -> new EmployeeFormGUI(controller));
        }
        
        // View Employees button - for all users
        JButton viewListButton = new JButton("View Employees");
        buttonPanel.add(viewListButton);
        viewListButton.addActionListener(e -> new EmployeeListGUI(controller));
        
        // Pay All Employees button - only for managers and admins
        JButton payAllButton = new JButton("Pay All Employees");
        if (userManager.isCurrentUserManager()) {
            buttonPanel.add(payAllButton);
            payAllButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, controller.payAllEmployees()));
        }
        
        // Generate Reports button - only for admins
        JButton reportButton = new JButton("Generate Reports");
        if (userManager.isCurrentUserAdmin()) {
            buttonPanel.add(reportButton);
            reportButton.addActionListener(e -> showReportDialog());
        }
        
        // User Management button - only for admins
        JButton userManagementButton = new JButton("User Management");
        if (userManager.isCurrentUserAdmin()) {
            buttonPanel.add(userManagementButton);
            userManagementButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, 
                "User Management functionality will be implemented in the future.", 
                "Coming Soon", 
                JOptionPane.INFORMATION_MESSAGE));
        }
        
        // Change Password button - for all users
        JButton changePasswordButton = new JButton("Change Password");
        buttonPanel.add(changePasswordButton);
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showChangePasswordDialog();
            }
        });
        
        // Logout button
        JButton logoutButton = new JButton("Logout");
        buttonPanel.add(logoutButton);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        
        // Exit button
        JButton exitButton = new JButton("Exit");
        buttonPanel.add(exitButton);
        exitButton.addActionListener(e -> System.exit(0));
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        
        // Show current user and role
        String statusText = "Ready | ";
        if (userManager.isAuthenticated()) {
            User currentUser = userManager.getCurrentUser();
            statusText += "Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")";
        } else {
            statusText += "Not logged in";
        }
        
        statusLabel = new JLabel(statusText);
        statusPanel.add(statusLabel, BorderLayout.EAST);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel);
        frame.setVisible(true);
        
        logger.info("Main application window opened with user: " + 
                    (userManager.isAuthenticated() ? userManager.getCurrentUser().getUsername() : "none"));
    }
    
    /**
     * Shows a dialog for changing the user's password.
     */
    private void showChangePasswordDialog() {
        if (!userManager.isAuthenticated()) {
            JOptionPane.showMessageDialog(frame, "You must be logged in to change your password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JPasswordField oldPasswordField = new JPasswordField(15);
        JPasswordField newPasswordField = new JPasswordField(15);
        JPasswordField confirmPasswordField = new JPasswordField(15);
        
        panel.add(new JLabel("Current Password:"));
        panel.add(oldPasswordField);
        panel.add(new JLabel("New Password:"));
        panel.add(newPasswordField);
        panel.add(new JLabel("Confirm New Password:"));
        panel.add(confirmPasswordField);
        
        int result = JOptionPane.showConfirmDialog(frame, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            if (newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "New password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(frame, "New passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            User currentUser = userManager.getCurrentUser();
            if (userManager.updatePassword(currentUser.getUsername(), oldPassword, newPassword)) {
                JOptionPane.showMessageDialog(frame, "Password changed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to change password. Please ensure your current password is correct.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Logs out the current user and returns to the login screen.
     */
    private void logout() {
        if (userManager.isAuthenticated()) {
            logger.info("User logged out: " + userManager.getCurrentUser().getUsername());
            userManager.logout();
        }
        
        frame.dispose();
        new LoginGUI(controller);
    }
    
    /**
     * Displays a dialog for generating different types of reports.
     */
    private void showReportDialog() {
        JDialog reportDialog = new JDialog(frame, "Generate Reports", true);
        reportDialog.setSize(400, 300);
        reportDialog.setLocationRelativeTo(frame);
        
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // CSV Employee Report
        JButton csvButton = new JButton("Export Employee Data to CSV");
        panel.add(csvButton);
        csvButton.addActionListener(e -> {
            generateCSVReport();
            reportDialog.dispose();
        });
        
        // Text Payroll Report
        JButton payrollButton = new JButton("Generate Payroll Text Report");
        panel.add(payrollButton);
        payrollButton.addActionListener(e -> {
            generatePayrollReport();
            reportDialog.dispose();
        });
        
        // PDF Employee Report
        JButton pdfEmployeeButton = new JButton("Export Employee Data to PDF");
        panel.add(pdfEmployeeButton);
        pdfEmployeeButton.addActionListener(e -> {
            generateEmployeePDFReport();
            reportDialog.dispose();
        });
        
        // PDF Payroll Report
        JButton pdfPayrollButton = new JButton("Generate Payroll PDF Report");
        panel.add(pdfPayrollButton);
        pdfPayrollButton.addActionListener(e -> {
            generatePayrollPDFReport();
            reportDialog.dispose();
        });
        
        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        panel.add(cancelButton);
        cancelButton.addActionListener(e -> reportDialog.dispose());
        
        reportDialog.add(panel);
        reportDialog.setVisible(true);
    }
    
    /**
     * Generates a CSV report of employees after selecting a file location.
     */
    private void generateCSVReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV Report");
        fileChooser.setSelectedFile(new File("employee_report.csv"));
        
        int result = fileChooser.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            boolean success = controller.generateEmployeeCSVReport(selectedFile.getAbsolutePath());
            
            if (success) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Report successfully generated at:\n" + selectedFile.getAbsolutePath(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    frame,
                    "Failed to generate report.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    /**
     * Generates a payroll report after selecting a file location.
     */
    private void generatePayrollReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Payroll Report");
        fileChooser.setSelectedFile(new File("payroll_report.txt"));
        
        int result = fileChooser.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            boolean success = controller.generatePayrollReport(selectedFile.getAbsolutePath());
            
            if (success) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Payroll report successfully generated at:\n" + selectedFile.getAbsolutePath(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    frame,
                    "Failed to generate payroll report.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    /**
     * Generates a PDF report of employees after selecting a file location.
     */
    private void generateEmployeePDFReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Employee PDF Report");
        fileChooser.setSelectedFile(new File("employee_report.pdf"));
        
        int result = fileChooser.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            boolean success = controller.generateEmployeePDFReport(selectedFile.getAbsolutePath());
            
            if (success) {
                JOptionPane.showMessageDialog(
                    frame,
                    "PDF report successfully generated at:\n" + selectedFile.getAbsolutePath(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    frame,
                    "Failed to generate PDF report.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    /**
     * Generates a PDF payroll report after selecting a file location.
     */
    private void generatePayrollPDFReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Payroll PDF Report");
        fileChooser.setSelectedFile(new File("payroll_report.pdf"));
        
        int result = fileChooser.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            boolean success = controller.generatePayrollPDFReport(selectedFile.getAbsolutePath());
            
            if (success) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Payroll PDF report successfully generated at:\n" + selectedFile.getAbsolutePath(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    frame,
                    "Failed to generate payroll PDF report.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}