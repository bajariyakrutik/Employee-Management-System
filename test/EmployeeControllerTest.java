/*
 * Krutik Bajariya
 * PDP Mid-Semester Project
 * Employee Management System
 * Used VSCode for the project
 * Used JUnit 5 for testing
 * EmployeeControllerTest.java
 */
package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import controller.EmployeeController;
import model.Database;
import model.Employee;
import model.CheckPayment;
import model.DirectDepositPayment;

import java.util.List;
import java.util.ArrayList;
import java.io.File;

/**
 * Test class for the EmployeeController.
 */
public class EmployeeControllerTest {
    
    private EmployeeController controller;
    private Database database;
    
    @BeforeEach
    public void setUp() {
        database = Database.getInstance();
        database.setUseInMemoryOnly(true); // Use in-memory storage for testing
        
        // Clear any existing employees
        List<Employee> employees = database.getEmployees();
        for (Employee e : new ArrayList<>(employees)) {
            database.removeEmployee(e.getId());
        }
        
        controller = new EmployeeController();
    }
    
    @Test
    public void testAddEmployee() {
        // Add a new employee
        controller.addEmployee(101, "John Doe", "Engineering", 75000.0);
        
        // Verify the employee was added
        List<Employee> employees = controller.getAllEmployees();
        boolean found = false;
        for (Employee e : employees) {
            if (e.getId() == 101) {
                found = true;
                assertEquals("John Doe", e.getName(), "Employee name should match");
                assertEquals("Engineering", e.getDepartment(), "Employee department should match");
                assertEquals(75000.0, e.getSalary(), 0.001, "Employee salary should match");
                assertTrue(e.getPaymentStrategy() instanceof DirectDepositPayment, 
                          "Default payment method should be Direct Deposit");
                break;
            }
        }
        
        assertTrue(found, "Added employee should be found");
    }
    
    @Test
    public void testAddEmployeeWithPaymentMethod() {
        // Add a new employee with Check payment method
        controller.addEmployee(102, "Jane Smith", "Finance", 85000.0, "Check");
        
        // Verify the employee was added with the correct payment method
        Employee employee = controller.getEmployeeById(102);
        assertNotNull(employee, "Employee should be added");
        assertEquals("Jane Smith", employee.getName(), "Employee name should match");
        assertTrue(employee.getPaymentStrategy() instanceof CheckPayment, 
                  "Payment method should be Check");
    }
    
    @Test
    public void testRemoveEmployee() {
        // Add a test employee
        controller.addEmployee(103, "Remove Test", "HR", 65000.0);
        
        // Verify the employee was added
        assertNotNull(controller.getEmployeeById(103), "Employee should be added");
        
        // Remove the employee
        controller.removeEmployee(103);
        
        // Verify the employee was removed
        assertNull(controller.getEmployeeById(103), "Employee should be removed");
    }
    
    @Test
    public void testUpdateEmployee() {
        // Add a test employee
        controller.addEmployee(104, "Update Test", "Marketing", 70000.0);
        
        // Update the employee
        controller.updateEmployee(104, "Updated Name", "Sales", 72000.0);
        
        // Verify the update
        Employee updated = controller.getEmployeeById(104);
        assertNotNull(updated, "Employee should exist after update");
        assertEquals("Updated Name", updated.getName(), "Name should be updated");
        assertEquals("Sales", updated.getDepartment(), "Department should be updated");
        assertEquals(72000.0, updated.getSalary(), 0.001, "Salary should be updated");
    }
    
    @Test
    public void testUpdateEmployeeWithPaymentMethod() {
        // Add a test employee
        controller.addEmployee(105, "Payment Update Test", "IT", 80000.0);
        
        // Update the employee with a new payment method
        controller.updateEmployee(105, "Payment Updated", "IT Support", 82000.0, "Check");
        
        // Verify the update
        Employee updated = controller.getEmployeeById(105);
        assertNotNull(updated, "Employee should exist after update");
        assertEquals("Payment Updated", updated.getName(), "Name should be updated");
        assertEquals("IT Support", updated.getDepartment(), "Department should be updated");
        assertEquals(82000.0, updated.getSalary(), 0.001, "Salary should be updated");
        assertTrue(updated.getPaymentStrategy() instanceof CheckPayment, 
                  "Payment method should be updated to Check");
    }
    
    @Test
    public void testChangePaymentMethod() {
        // Add a test employee with default payment method (Direct Deposit)
        controller.addEmployee(106, "Method Test", "Research", 90000.0);
        
        // Change to Check
        boolean success = controller.changePaymentMethod(106, "Check");
        
        // Verify the change
        assertTrue(success, "Change payment method should succeed");
        Employee employee = controller.getEmployeeById(106);
        assertNotNull(employee, "Employee should exist");
        assertTrue(employee.getPaymentStrategy() instanceof CheckPayment, 
                  "Payment method should be changed to Check");
        
        // Change back to Direct Deposit
        success = controller.changePaymentMethod(106, "Direct Deposit");
        
        // Verify the change back
        assertTrue(success, "Change payment method should succeed");
        employee = controller.getEmployeeById(106);
        assertNotNull(employee, "Employee should exist");
        assertTrue(employee.getPaymentStrategy() instanceof DirectDepositPayment, 
                  "Payment method should be changed to Direct Deposit");
    }
    
    @Test
    public void testGetAllEmployees() {
        // Add several test employees
        controller.addEmployee(107, "Employee A", "Department A", 65000.0);
        controller.addEmployee(108, "Employee B", "Department B", 75000.0);
        controller.addEmployee(109, "Employee C", "Department C", 85000.0);
        
        // Get all employees
        List<Employee> employees = controller.getAllEmployees();
        
        // Verify the count
        assertEquals(3, employees.size(), "Should have 3 employees");
        
        // Verify all employees are present
        boolean foundA = false, foundB = false, foundC = false;
        for (Employee e : employees) {
            if (e.getId() == 107) foundA = true;
            if (e.getId() == 108) foundB = true;
            if (e.getId() == 109) foundC = true;
        }
        
        assertTrue(foundA && foundB && foundC, "All added employees should be found");
    }
    
    @Test
    public void testPayAllEmployees() {
        // Add test employees with different payment methods
        controller.addEmployee(110, "Pay Test A", "Department A", 60000.0, "Direct Deposit");
        controller.addEmployee(111, "Pay Test B", "Department B", 70000.0, "Check");
        
        // Generate pay stubs
        String payStubs = controller.payAllEmployees();
        
        // Verify pay stubs contain expected information
        assertTrue(payStubs.contains("Pay Test A"), "Pay stubs should include first employee name");
        assertTrue(payStubs.contains("Pay Test B"), "Pay stubs should include second employee name");
        assertTrue(payStubs.contains("Direct Deposit"), "Pay stubs should include Direct Deposit method");
        assertTrue(payStubs.contains("Check"), "Pay stubs should include Check method");
    }
    
    @Test
    public void testGenerateCSVReport() {
        // Add test employees
        controller.addEmployee(112, "Report Test A", "Department A", 65000.0);
        controller.addEmployee(113, "Report Test B", "Department B", 75000.0);
        
        // Generate CSV report to a temporary file
        File tempFile = new File("test_report.csv");
        boolean success = controller.generateEmployeeCSVReport(tempFile.getAbsolutePath());
        
        // Verify report generation success
        assertTrue(success, "CSV report generation should succeed");
        assertTrue(tempFile.exists(), "Report file should exist");
        
        // Clean up
        tempFile.delete();
    }
    
    @Test
    public void testGeneratePayrollReport() {
        // Add test employees
        controller.addEmployee(114, "Payroll Test A", "Department A", 68000.0);
        controller.addEmployee(115, "Payroll Test B", "Department B", 78000.0);
        
        // Generate payroll report to a temporary file
        File tempFile = new File("test_payroll.txt");
        boolean success = controller.generatePayrollReport(tempFile.getAbsolutePath());
        
        // Verify report generation success
        assertTrue(success, "Payroll report generation should succeed");
        assertTrue(tempFile.exists(), "Payroll report file should exist");
        
        // Clean up
        tempFile.delete();
    }
}