/*
 * Krutik Bajariya
 * PDP Project
 * Employee Management System
 * Used VSCode for the project
 * Used JUnit 5 for testing
 * DatabaseTest.java
 */
package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import model.Database;
import model.Employee;
import model.DirectDepositPayment;
import model.CheckPayment;

import java.util.List;
import java.util.ArrayList;

/**
 * Test class for the Database singleton.
 */
public class DatabaseTest {
    
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
    }
    
    @Test
    public void testSingletonPattern() {
        // Get two instances and verify they are the same object
        Database instance1 = Database.getInstance();
        Database instance2 = Database.getInstance();
        assertSame(instance1, instance2, "Database should follow the Singleton pattern");
    }
    
    @Test
    public void testAddEmployee() {
        // Create a test employee
        Employee employee = new Employee(999, "Test Employee", "Test Department", 50000.0);
        
        // Add the employee to the database
        database.addEmployee(employee);
        
        // Get all employees and check if the test employee is present
        List<Employee> employees = database.getEmployees();
        boolean found = false;
        for (Employee e : employees) {
            if (e.getId() == 999) {
                found = true;
                assertEquals("Test Employee", e.getName(), "Employee name should match");
                assertEquals("Test Department", e.getDepartment(), "Employee department should match");
                assertEquals(50000.0, e.getSalary(), 0.001, "Employee salary should match");
                break;
            }
        }
        
        assertTrue(found, "Added employee should be found in the database");
    }
    
    @Test
    public void testRemoveEmployee() {
        // Add a test employee
        Employee employee = new Employee(998, "Remove Test", "Test Department", 60000.0);
        database.addEmployee(employee);
        
        // Verify the employee was added
        List<Employee> employeesBefore = database.getEmployees();
        boolean foundBefore = false;
        for (Employee e : employeesBefore) {
            if (e.getId() == 998) {
                foundBefore = true;
                break;
            }
        }
        assertTrue(foundBefore, "Employee should be added before removal test");
        
        // Remove the employee
        database.removeEmployee(998);
        
        // Verify the employee was removed
        List<Employee> employeesAfter = database.getEmployees();
        boolean foundAfter = false;
        for (Employee e : employeesAfter) {
            if (e.getId() == 998) {
                foundAfter = true;
                break;
            }
        }
        assertFalse(foundAfter, "Employee should not be found after removal");
    }
    
    @Test
    public void testUpdateEmployee() {
        // Add a test employee
        Employee employee = new Employee(997, "Update Test", "Old Department", 70000.0);
        database.addEmployee(employee);
        
        // Update the employee
        employee.setName("Updated Name");
        employee.setDepartment("New Department");
        employee.setSalary(75000.0);
        database.updateEmployee(employee);
        
        // Retrieve the employee and verify updates
        Employee updated = database.getEmployeeById(997);
        assertNotNull(updated, "Updated employee should exist");
        assertEquals("Updated Name", updated.getName(), "Employee name should be updated");
        assertEquals("New Department", updated.getDepartment(), "Employee department should be updated");
        assertEquals(75000.0, updated.getSalary(), 0.001, "Employee salary should be updated");
    }
    
    @Test
    public void testUpdatePaymentMethod() {
        // Add a test employee with direct deposit
        Employee employee = new Employee(996, "Payment Test", "Test Department", 80000.0);
        employee.setPaymentStrategy(new DirectDepositPayment());
        database.addEmployee(employee);
        
        // Update to check payment
        boolean updateSuccess = database.updatePaymentMethod(996, "Check");
        assertTrue(updateSuccess, "Payment method update should succeed");
        
        // Verify payment method was updated
        Employee updated = database.getEmployeeById(996);
        assertNotNull(updated, "Employee should exist after payment method update");
        assertTrue(updated.getPaymentStrategy() instanceof CheckPayment, 
                  "Payment strategy should be updated to CheckPayment");
    }
    
    @Test
    public void testGetEmployeeById() {
        // Add a test employee
        Employee employee = new Employee(995, "ID Test", "Test Department", 90000.0);
        database.addEmployee(employee);
        
        // Retrieve by ID
        Employee retrieved = database.getEmployeeById(995);
        
        // Verify retrieval
        assertNotNull(retrieved, "Employee should be retrieved by ID");
        assertEquals(995, retrieved.getId(), "Retrieved employee should have correct ID");
        assertEquals("ID Test", retrieved.getName(), "Retrieved employee should have correct name");
    }
    
    @Test
    public void testGetNonExistentEmployee() {
        // Try to retrieve a non-existent employee
        Employee retrieved = database.getEmployeeById(9999);
        
        // Verify null is returned
        assertNull(retrieved, "Non-existent employee should return null");
    }
}