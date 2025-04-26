package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import model.DatabaseManager;
import model.Employee;
import model.User;
import model.CheckPayment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Test class for the DatabaseManager.
 * Since we can't modify the final DB_URL field, we'll test against the actual database file,
 * but ensure we clean up after each test.
 */
public class DatabaseManagerTest {
    
    private DatabaseManager dbManager;
    private Connection connection;
    
    @BeforeEach
    public void setUp() throws Exception {
        // Get a fresh instance
        dbManager = DatabaseManager.getInstance();
        connection = dbManager.getConnection();
        
        // Clear tables for each test
        clearTables();
    }
    
    @AfterEach
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
    
    private void clearTables() throws SQLException {
        // Clear the users table first (due to foreign key constraints)
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM users WHERE username != 'admin'")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Table might not exist yet, which is fine
            System.out.println("Note: " + e.getMessage());
        }
        
        // Clear the employees table
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM employees")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Table might not exist yet, which is fine
            System.out.println("Note: " + e.getMessage());
        }
    }
    
    @Test
    public void testSingletonPattern() {
        // Get two instances and verify they are the same object
        DatabaseManager instance1 = DatabaseManager.getInstance();
        DatabaseManager instance2 = DatabaseManager.getInstance();
        assertSame(instance1, instance2, "DatabaseManager should follow the Singleton pattern");
    }
    
    @Test
    public void testAddAndGetEmployee() {
        // Create a test employee
        Employee employee = new Employee(1, "Test Employee", "Test Department", 50000.0);
        
        // Add to database
        boolean addSuccess = dbManager.addEmployee(employee);
        assertTrue(addSuccess, "Adding employee should succeed");
        
        // Retrieve the employee
        Employee retrieved = dbManager.getEmployeeById(1);
        
        // Verify retrieval
        assertNotNull(retrieved, "Retrieved employee should not be null");
        assertEquals(1, retrieved.getId(), "Employee ID should match");
        assertEquals("Test Employee", retrieved.getName(), "Employee name should match");
        assertEquals("Test Department", retrieved.getDepartment(), "Employee department should match");
        assertEquals(50000.0, retrieved.getSalary(), 0.001, "Employee salary should match");
    }
    
    @Test
    public void testUpdateEmployee() {
        // Add a test employee
        Employee employee = new Employee(2, "Update Test", "Old Department", 60000.0);
        dbManager.addEmployee(employee);
        
        // Update the employee
        employee.setName("Updated Name");
        employee.setDepartment("New Department");
        employee.setSalary(65000.0);
        employee.setPaymentStrategy(new CheckPayment());
        
        boolean updateSuccess = dbManager.updateEmployee(employee);
        assertTrue(updateSuccess, "Updating employee should succeed");
        
        // Retrieve the updated employee
        Employee updated = dbManager.getEmployeeById(2);
        
        // Verify update
        assertNotNull(updated, "Updated employee should not be null");
        assertEquals("Updated Name", updated.getName(), "Employee name should be updated");
        assertEquals("New Department", updated.getDepartment(), "Employee department should be updated");
        assertEquals(65000.0, updated.getSalary(), 0.001, "Employee salary should be updated");
        assertEquals("Check", updated.getPaymentMethodName(), "Payment method should be updated to Check");
    }
    
    @Test
    public void testRemoveEmployee() {
        // Add a test employee
        Employee employee = new Employee(3, "Remove Test", "Test Department", 70000.0);
        dbManager.addEmployee(employee);
        
        // Verify employee was added
        assertNotNull(dbManager.getEmployeeById(3), "Employee should be added successfully");
        
        // Remove the employee
        boolean removeSuccess = dbManager.removeEmployee(3);
        assertTrue(removeSuccess, "Removing employee should succeed");
        
        // Verify employee was removed
        assertNull(dbManager.getEmployeeById(3), "Employee should be removed successfully");
    }
    
    @Test
    public void testGetAllEmployees() {
        // Clear any existing employees first
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM employees")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            fail("Failed to clear employees table: " + e.getMessage());
        }
        
        // Add multiple test employees
        Employee employee1 = new Employee(4, "Employee A", "Department A", 50000.0);
        Employee employee2 = new Employee(5, "Employee B", "Department B", 60000.0);
        Employee employee3 = new Employee(6, "Employee C", "Department C", 70000.0);
        
        dbManager.addEmployee(employee1);
        dbManager.addEmployee(employee2);
        dbManager.addEmployee(employee3);
        
        // Get all employees
        List<Employee> employees = dbManager.getAllEmployees();
        
        // Verify count
        assertEquals(3, employees.size(), "Should have 3 employees");
        
        // Verify all employees are present
        boolean foundA = false, foundB = false, foundC = false;
        for (Employee e : employees) {
            if (e.getId() == 4) foundA = true;
            if (e.getId() == 5) foundB = true;
            if (e.getId() == 6) foundC = true;
        }
        
        assertTrue(foundA && foundB && foundC, "All added employees should be found");
    }
    
    @Test
    public void testUpdatePaymentMethod() {
        // Add a test employee with Direct Deposit (default)
        Employee employee = new Employee(7, "Payment Test", "Test Department", 80000.0);
        dbManager.addEmployee(employee);
        
        // Update to Check payment
        boolean updateSuccess = dbManager.updatePaymentMethod(7, "Check");
        assertTrue(updateSuccess, "Updating payment method should succeed");
        
        // Verify payment method was updated
        Employee updated = dbManager.getEmployeeById(7);
        assertNotNull(updated, "Employee should exist after payment method update");
        assertEquals("Check", updated.getPaymentMethodName(), "Payment method should be updated to Check");
        
        // Update back to Direct Deposit
        updateSuccess = dbManager.updatePaymentMethod(7, "Direct Deposit");
        assertTrue(updateSuccess, "Updating payment method should succeed");
        
        // Verify payment method was updated back
        updated = dbManager.getEmployeeById(7);
        assertNotNull(updated, "Employee should exist after payment method update");
        assertEquals("Direct Deposit", updated.getPaymentMethodName(), "Payment method should be updated to Direct Deposit");
    }
    
    @Test
    public void testAddAndGetUser() {
        // Create a test user
        User user = new User("test_user", "password", User.Role.EMPLOYEE, 8);
        
        // Add employee first (for foreign key constraint)
        Employee employee = new Employee(8, "User Test", "Test Department", 90000.0);
        dbManager.addEmployee(employee);
        
        // Add user
        boolean addSuccess = dbManager.addUser(user);
        assertTrue(addSuccess, "Adding user should succeed");
        
        // Get user by username
        User retrieved = dbManager.getUserByUsername("test_user");
        
        // Verify retrieval
        assertNotNull(retrieved, "Retrieved user should not be null");
        assertEquals("test_user", retrieved.getUsername(), "Username should match");
        assertEquals("password", retrieved.getPassword(), "Password should match");
        assertEquals(User.Role.EMPLOYEE, retrieved.getRole(), "Role should match");
        assertEquals(Integer.valueOf(8), retrieved.getEmployeeId(), "Employee ID should match");
    }
    
    @Test
    public void testGetUserByEmployeeId() {
        // Add employee first
        Employee employee = new Employee(9, "Employee User Test", "Test Department", 95000.0);
        dbManager.addEmployee(employee);
        
        // Add user with employee ID
        User user = new User("employee_user_test", "password", User.Role.EMPLOYEE, 9);
        dbManager.addUser(user);
        
        // Get user by employee ID
        User retrieved = dbManager.getUserByEmployeeId(9);
        
        // Verify retrieval
        assertNotNull(retrieved, "User should be retrieved by employee ID");
        assertEquals("employee_user_test", retrieved.getUsername(), "Username should match");
        assertEquals(Integer.valueOf(9), retrieved.getEmployeeId(), "Employee ID should match");
    }
    
    @Test
    public void testUpdateUserPassword() {
        // Add a test user
        User user = new User("password_test", "old_password", User.Role.EMPLOYEE);
        dbManager.addUser(user);
        
        // Update password
        boolean updateSuccess = dbManager.updateUserPassword("password_test", "new_password");
        assertTrue(updateSuccess, "Password update should succeed");
        
        // Verify password was updated
        User updated = dbManager.getUserByUsername("password_test");
        assertNotNull(updated, "User should exist after password update");
        assertEquals("new_password", updated.getPassword(), "Password should be updated");
    }
    
    @Test
    public void testGetAllUsers() {
        // Clear non-admin users first
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM users WHERE username != 'admin'")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            fail("Failed to clear users table: " + e.getMessage());
        }
        
        // Verify the current count - should only be the admin user
        List<User> initialUsers = dbManager.getAllUsers();
        
        // Add multiple test users
        User admin = new User("admin_test", "admin_pass", User.Role.ADMIN);
        User manager = new User("manager_test", "manager_pass", User.Role.MANAGER);
        
        // Add employee first
        Employee employee = new Employee(10, "Employee User", "Test Department", 100000.0);
        dbManager.addEmployee(employee);
        
        User employeeUser = new User("employee_test", "employee_pass", User.Role.EMPLOYEE, 10);
        
        dbManager.addUser(admin);
        dbManager.addUser(manager);
        dbManager.addUser(employeeUser);
        
        // Get all users
        List<User> users = dbManager.getAllUsers();
        
        // Calculate expected count - the initial count (admin) plus the 3 added users
        int expectedCount = initialUsers.size() + 4; // 1 admin + 3 added users + 1 employee
        assertEquals(expectedCount, users.size(), "Should have " + expectedCount + " users");
        
        // Verify our added users are present
        boolean foundAdmin = false, foundManager = false, foundEmployee = false;
        for (User u : users) {
            if (u.getUsername().equals("admin_test")) foundAdmin = true;
            if (u.getUsername().equals("manager_test")) foundManager = true;
            if (u.getUsername().equals("employee_test")) foundEmployee = true;
        }
        
        assertTrue(foundAdmin && foundManager && foundEmployee, "All added users should be found");
    }
}