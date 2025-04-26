/*
 * Krutik Bajariya
 * PDP Project
 * Employee Management System
 * Used VSCode for the project
 * EmployeeFormGUI.java
 */
package view;

import controller.EmployeeController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The EmployeeFormGUI class represents a graphical user interface for adding new employees.
 * It interacts with the EmployeeController to add employees to the system.
 * This updated version includes input validation.
 */
public class EmployeeFormGUI {
    private EmployeeController controller;
    private JFrame frame;
    private JTextField idField, nameField, deptField, salaryField;
    private JComboBox<String> paymentMethodCombo;

    /**
     * Constructs an EmployeeFormGUI with the specified EmployeeController.
     * 
     * @param controller the EmployeeController to interact with
     */
    public EmployeeFormGUI(EmployeeController controller) {
        this.controller = controller;
        frame = new JFrame("Add Employee");
        frame.setSize(400, 350);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // ID Field
        panel.add(new JLabel("ID:"));
        idField = new JTextField();
        panel.add(idField);
        
        // Name Field
        panel.add(new JLabel("Name:"));
        nameField = new JTextField();
        panel.add(nameField);
        
        // Department Field
        panel.add(new JLabel("Department:"));
        deptField = new JTextField();
        panel.add(deptField);
        
        // Salary Field
        panel.add(new JLabel("Salary:"));
        salaryField = new JTextField();
        panel.add(salaryField);
        
        // Payment Method Dropdown
        panel.add(new JLabel("Payment Method:"));
        paymentMethodCombo = new JComboBox<>(new String[]{"Direct Deposit", "Check"});
        panel.add(paymentMethodCombo);
        
        // Submit Button
        JButton submitButton = new JButton("Submit");
        panel.add(submitButton);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEmployee();
            }
        });
        
        // Clear Button
        JButton clearButton = new JButton("Clear");
        panel.add(clearButton);
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });
        
        frame.add(panel);
        frame.setVisible(true);
    }

    /**
     * Clears all input fields.
     */
    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        deptField.setText("");
        salaryField.setText("");
        paymentMethodCombo.setSelectedIndex(0);
    }

    /**
     * Adds a new employee using the data entered in the form fields.
     * Validates input before adding the employee.
     */
    private void addEmployee() {
        // Validate ID
        int id;
        try {
            id = Integer.parseInt(idField.getText());
            if (id <= 0) {
                JOptionPane.showMessageDialog(frame, "ID must be a positive number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "ID must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate Name
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate Department
        String dept = deptField.getText().trim();
        if (dept.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Department cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate Salary
        double salary;
        try {
            salary = Double.parseDouble(salaryField.getText());
            if (salary < 0) {
                JOptionPane.showMessageDialog(frame, "Salary cannot be negative.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Salary must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get payment method
        String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
        
        // Add employee
        controller.addEmployee(id, name, dept, salary, paymentMethod);
        JOptionPane.showMessageDialog(frame, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        frame.dispose();
    }
}