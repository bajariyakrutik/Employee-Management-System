/*
 * Krutik Bajariya
 * PDP Project
 * Employee Management System
 * Used VSCode for the project
 * EmployeeListGUI.java
*/
package view;

import controller.EmployeeController;
import model.Employee;
import model.UserManager;
import model.User;
import util.Logger;

import view.custom.ButtonEditor;
import view.custom.ButtonRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * EmployeeListGUI class provides a graphical user interface for displaying,
 * editing, and deleting employees from the Employee Management System.
 * Updated to show employees based on user role - regular employees see only their own record.
 */
public class EmployeeListGUI {
    private EmployeeController controller;
    private JFrame frame;
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private final Logger logger = Logger.getInstance();
    private UserManager userManager = UserManager.getInstance();
    
    /**
     * Constructs an EmployeeListGUI with the specified EmployeeController.
     *
     * @param controller the EmployeeController to manage employee operations
     */
    public EmployeeListGUI(EmployeeController controller) {
        this.controller = controller;
        frame = new JFrame("Employee List");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create search panel - only visible for admins and managers
        if (userManager.isCurrentUserManager()) {
            JPanel searchPanel = new JPanel();
            searchPanel.add(new JLabel("Search by ID:"));
            JTextField searchField = new JTextField(10);
            searchPanel.add(searchField);
            JButton searchButton = new JButton("Search");
            searchButton.addActionListener(e -> {
                try {
                    int id = Integer.parseInt(searchField.getText());
                    Employee emp = controller.getEmployeeById(id);
                    if (emp != null) {
                        // Clear table and show only the searched employee
                        tableModel.setRowCount(0);
                        
                        Object[] rowData;
                        rowData = new Object[]{
                            emp.getId(), 
                            emp.getName(), 
                            emp.getDepartment(), 
                            emp.getSalary(), 
                            "Edit", 
                            "Delete", 
                            "Change Payment"
                        };
                        tableModel.addRow(rowData);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Employee not found!", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid ID!", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            searchPanel.add(searchButton);
            
            JButton resetButton = new JButton("Show All");
            resetButton.addActionListener(e -> refreshTable());
            searchPanel.add(resetButton);
            
            panel.add(searchPanel, BorderLayout.NORTH);
        }
        
        // Define column names based on user role
        String[] columnNames;
        if (userManager.isCurrentUserManager()) {
            // Managers and admins can edit employees
            columnNames = new String[]{"ID", "Name", "Department", "Salary", "Edit", "Delete", "Change Payment"};
        } else {
            // Regular employees can only view
            columnNames = new String[]{"ID", "Name", "Department", "Salary", "Payment Method"};
        }
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make only button columns editable for managers and admins
                if (userManager.isCurrentUserManager()) {
                    return column >= 4;
                } else {
                    return false;
                }
            }
        };
        employeeTable = new JTable(tableModel);
        
        // Configure table appearance
        employeeTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        employeeTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        employeeTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Department
        employeeTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Salary
        
        // Add action buttons for managers and admins
        if (userManager.isCurrentUserManager()) {
            employeeTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Edit
            employeeTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Delete
            employeeTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Change Payment
            
            // Add custom button renderer and editor
            employeeTable.getColumn("Edit").setCellRenderer(new ButtonRenderer());
            employeeTable.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox(), (row) -> editEmployee(row)));
            
            employeeTable.getColumn("Delete").setCellRenderer(new ButtonRenderer());
            employeeTable.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), (row) -> deleteEmployee(row)));
            
            employeeTable.getColumn("Change Payment").setCellRenderer(new ButtonRenderer());
            employeeTable.getColumn("Change Payment").setCellEditor(new ButtonEditor(new JCheckBox(), (row) -> changePaymentMethod(row)));
        }
        
        // Populate Table
        refreshTable();
        
        panel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);
        
        // Add a status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        String roleInfo = "View mode: " + 
                        (userManager.isCurrentUserAdmin() ? "Admin (Full Access)" : 
                         userManager.isCurrentUserManager() ? "Manager (Edit Access)" : 
                         "Employee (Personal Record Only)");
        JLabel statusLabel = new JLabel(roleInfo);
        statusPanel.add(statusLabel, BorderLayout.EAST);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(statusPanel, BorderLayout.SOUTH);
        
        frame.add(panel);
        frame.setVisible(true);
        
        logger.info("Employee list opened by user: " + 
                  (userManager.isAuthenticated() ? userManager.getCurrentUser().getUsername() : "unknown"));
    }

    /**
     * Refreshes the employee table with the latest data from the controller.
     */
    private void refreshTable() {
        tableModel.setRowCount(0);
        
        // Get current user
        User currentUser = userManager.getCurrentUser();
        
        if (currentUser == null) {
            return;
        }
        
        // If user is a regular employee, show only their record
        if (currentUser.getRole() == User.Role.EMPLOYEE && currentUser.getEmployeeId() != null) {
            Employee e = controller.getEmployeeById(currentUser.getEmployeeId());
            if (e != null) {
                tableModel.addRow(new Object[]{
                    e.getId(), 
                    e.getName(), 
                    e.getDepartment(), 
                    e.getSalary(),
                    e.getPaymentMethodName()
                });
            }
        } else {
            // Admin or manager - show all employees
            List<Employee> employees = controller.getAllEmployees();
            
            for (Employee e : employees) {
                if (userManager.isCurrentUserManager()) {
                    // Managers and admins see action buttons
                    tableModel.addRow(new Object[]{
                        e.getId(), 
                        e.getName(), 
                        e.getDepartment(), 
                        e.getSalary(), 
                        "Edit", 
                        "Delete", 
                        "Change Payment"
                    });
                } else {
                    // Should never reach here, but just in case
                    tableModel.addRow(new Object[]{
                        e.getId(), 
                        e.getName(), 
                        e.getDepartment(), 
                        e.getSalary(),
                        e.getPaymentMethodName()
                    });
                }
            }
        }
    }

    /**
     * Edits the employee details at the specified row.
     *
     * @param row the row index of the employee to be edited
     */
    private void editEmployee(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        String name = JOptionPane.showInputDialog("Enter New Name:", tableModel.getValueAt(row, 1));
        if (name == null) return; // User canceled
        
        String department = JOptionPane.showInputDialog("Enter New Department:", tableModel.getValueAt(row, 2));
        if (department == null) return; // User canceled
        
        String salaryStr = JOptionPane.showInputDialog("Enter New Salary:", tableModel.getValueAt(row, 3));
        if (salaryStr == null) return; // User canceled
        
        try {
            double salary = Double.parseDouble(salaryStr);
            if (salary < 0) {
                JOptionPane.showMessageDialog(frame, "Salary cannot be negative.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            controller.updateEmployee(id, name, department, salary);
            refreshTable();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Salary must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes the employee at the specified row.
     *
     * @param row the row index of the employee to be deleted
     */
    private void deleteEmployee(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(
            frame, 
            "Are you sure you want to delete this employee? This will also delete their user account.",
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            controller.removeEmployee(id);
            refreshTable();
        }
    }
    
    /**
     * Changes the payment method for the employee at the specified row.
     *
     * @param row the row index of the employee
     */
    private void changePaymentMethod(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        String[] options = {"Direct Deposit", "Check"};
        
        int choice = JOptionPane.showOptionDialog(
            frame,
            "Select Payment Method:",
            "Change Payment Method",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice >= 0) {
            String paymentMethod = options[choice];
            boolean success = controller.changePaymentMethod(id, paymentMethod);
            
            if (success) {
                JOptionPane.showMessageDialog(
                    frame, 
                    "Payment method changed to " + paymentMethod + ".", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE
                );
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(
                    frame, 
                    "Failed to change payment method. Employee not found.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}