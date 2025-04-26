package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import model.User;
import model.UserManager;
import model.DatabaseManager;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Test class for the UserManager.
 */
public class UserManagerTest {
    
    private UserManager userManager;
    private DatabaseManager dbManager;
    
    @BeforeEach
    public void setUp() throws Exception {
        // Reset the singleton instance for each test using reflection
        Field instance = UserManager.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);

        dbManager = DatabaseManager.getInstance();
        
        // Get a fresh instance
        userManager = UserManager.getInstance();
        
        // Use reflection to reset the users map
        Field usersField = UserManager.class.getDeclaredField("users");
        usersField.setAccessible(true);
        usersField.set(userManager, new HashMap<>());
        
        // Add default test users
        User adminUser = new User("admin_test", "admin123", User.Role.ADMIN);
        User managerUser = new User("manager_test", "manager123", User.Role.MANAGER);
        User employeeUser = new User("employee_test", "employee123", User.Role.EMPLOYEE, 1001);

        // Add users to the database
        dbManager.addUser(adminUser);
        dbManager.addUser(managerUser);
        dbManager.addUser(employeeUser);

        // Add users to the UserManager
        userManager.addUser(adminUser);
        userManager.addUser(managerUser);
        userManager.addUser(employeeUser);
    }
    
    @Test
    public void testSingletonPattern() {
        // Get two instances and verify they are the same object
        UserManager instance1 = UserManager.getInstance();
        UserManager instance2 = UserManager.getInstance();
        assertSame(instance1, instance2, "UserManager should follow the Singleton pattern");
    }
    
    @Test
    public void testAddUser() {
        // Add a new user
        User newUser = new User("new_user", "password", User.Role.EMPLOYEE, 1002);
        boolean success = userManager.addUser(newUser);
        
        // Verify addition was successful
        assertTrue(success, "Adding a new user should succeed");
        
        // Try adding a user with an existing username
        User duplicateUser = new User("admin_test", "password", User.Role.EMPLOYEE);
        boolean duplicateSuccess = userManager.addUser(duplicateUser);
        
        // Verify addition failed
        assertFalse(duplicateSuccess, "Adding a user with existing username should fail");
    }
    
    @Test
    public void testAuthenticate() {
        // Test valid authentication
        boolean adminSuccess = userManager.authenticate("admin_test", "admin123");
        assertTrue(adminSuccess, "Authentication with correct credentials should succeed");
        
        // Test invalid password
        boolean invalidPassword = userManager.authenticate("admin_test", "wrong_password");
        assertFalse(invalidPassword, "Authentication with wrong password should fail");
        
        // Test non-existent user
        boolean nonExistentUser = userManager.authenticate("non_existent", "password");
        assertFalse(nonExistentUser, "Authentication for non-existent user should fail");
    }
    
    @Test
    public void testLogout() {
        // Authenticate a user
        userManager.authenticate("admin_test", "admin123");
        assertTrue(userManager.isAuthenticated(), "User should be authenticated");
        
        // Log out the user
        userManager.logout();
        assertFalse(userManager.isAuthenticated(), "User should not be authenticated after logout");
    }
    
    @Test
    public void testGetCurrentUser() {
        // No user authenticated initially
        assertNull(userManager.getCurrentUser(), "No user should be authenticated initially");
        
        // Authenticate a user
        userManager.authenticate("manager_test", "manager123");
        User currentUser = userManager.getCurrentUser();
        
        // Verify current user
        assertNotNull(currentUser, "Current user should be non-null after authentication");
        assertEquals("manager_test", currentUser.getUsername(), "Current user should have correct username");
        assertEquals(User.Role.MANAGER, currentUser.getRole(), "Current user should have correct role");
    }
    
    @Test
    public void testIsCurrentUserAdmin() {
        // Authenticate an admin
        userManager.authenticate("admin_test", "admin123");
        assertTrue(userManager.isCurrentUserAdmin(), "Admin user should be identified as admin");
        
        // Log out and authenticate a non-admin
        userManager.logout();
        userManager.authenticate("employee_test", "employee123");
        assertFalse(userManager.isCurrentUserAdmin(), "Employee user should not be identified as admin");
    }
    
    @Test
    public void testIsCurrentUserManager() {
        // Authenticate an admin (also considered a manager)
        userManager.authenticate("admin_test", "admin123");
        assertTrue(userManager.isCurrentUserManager(), "Admin user should be identified as manager");
        
        // Log out and authenticate a manager
        userManager.logout();
        userManager.authenticate("manager_test", "manager123");
        assertTrue(userManager.isCurrentUserManager(), "Manager user should be identified as manager");
        
        // Log out and authenticate an employee
        userManager.logout();
        userManager.authenticate("employee_test", "employee123");
        assertFalse(userManager.isCurrentUserManager(), "Employee user should not be identified as manager");
    }
    
    @Test
    public void testUpdatePassword() {
        // Try updating with correct old password
        boolean success = userManager.updatePassword("employee_test", "employee123", "new_password");
        assertTrue(success, "Password update with correct old password should succeed");
        
        // Verify the new password works for authentication
        boolean authSuccess = userManager.authenticate("employee_test", "new_password");
        assertTrue(authSuccess, "Authentication with new password should succeed");
        
        // Try updating with incorrect old password
        boolean failure = userManager.updatePassword("employee_test", "wrong_password", "newer_password");
        assertFalse(failure, "Password update with incorrect old password should fail");
    }
    
    @Test
    public void testGetUserByUsername() {
        // Get existing user
        User employee = userManager.getUserByUsername("employee_test");
        assertNotNull(employee, "Existing user should be found");
        assertEquals("employee_test", employee.getUsername(), "Retrieved user should have correct username");
        
        // Get non-existent user
        User nonExistent = userManager.getUserByUsername("non_existent");
        assertNull(nonExistent, "Non-existent user should not be found");
    }
    
    @Test
    public void testGetUserByEmployeeId() {
        // Get user by existing employee ID
        User employee = userManager.getUserByEmployeeId(1001);
        assertNotNull(employee, "User with existing employee ID should be found");
        assertEquals("employee_test", employee.getUsername(), "Retrieved user should have correct username");
        assertEquals(Integer.valueOf(1001), employee.getEmployeeId(), "Retrieved user should have correct employee ID");
        
        // Get user by non-existent employee ID
        User nonExistent = userManager.getUserByEmployeeId(9999);
        assertNull(nonExistent, "User with non-existent employee ID should not be found");
    }
    
    @Test
    public void testIsViewingOwnRecord() {
        // Authenticate employee user
        userManager.authenticate("employee_test", "employee123");
        
        // Check if viewing own record
        assertTrue(userManager.isViewingOwnRecord(1001), "Employee should be viewing own record with matching ID");
        assertFalse(userManager.isViewingOwnRecord(1002), "Employee should not be viewing own record with non-matching ID");
        
        // Log out and authenticate admin
        userManager.logout();
        userManager.authenticate("admin_test", "admin123");
        
        // Admin should never be viewing own record (admins don't have employee IDs)
        assertFalse(userManager.isViewingOwnRecord(1001), "Admin should never be viewing own record");
    }
}