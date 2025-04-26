package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.Logger;

/**
 * Singleton class for managing users and authentication.
 * Updated to work with the database for persistent storage.
 */
public class UserManager {
    private static UserManager instance;
    private Map<String, User> users;
    private User currentUser;
    private final Logger logger = Logger.getInstance();
    private final DatabaseManager dbManager = DatabaseManager.getInstance();
    
    /**
     * Private constructor to prevent instantiation.
     * Loads users from the database.
     */
    private UserManager() {
        users = new HashMap<>();
        loadUsersFromDatabase();
    }
    
    /**
     * Returns the singleton instance of the UserManager.
     * If the instance does not exist, it creates one.
     *
     * @return the singleton instance of the UserManager
     */
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }
    
    /**
     * Loads all users from the database into memory.
     */
    private void loadUsersFromDatabase() {
        users.clear();
        List<User> userList = dbManager.getAllUsers();
        
        for (User user : userList) {
            users.put(user.getUsername(), user);
        }
        
        logger.info("Loaded " + users.size() + " users from database");
    }
    
    /**
     * Adds a user to the system.
     *
     * @param user the user to add
     * @return true if the user was added successfully, false if the username already exists
     */
    public boolean addUser(User user) {
        if (users.containsKey(user.getUsername())) {
            logger.warning("Failed to add user: Username '" + user.getUsername() + "' already exists");
            return false;
        }
        
        // Add to database
        boolean success = dbManager.addUser(user);
        
        if (success) {
            // Add to in-memory map
            users.put(user.getUsername(), user);
            logger.info("User added: " + user.getUsername() + " with role " + user.getRole());
        }
        
        return success;
    }
    
    /**
     * Authenticates a user.
     *
     * @param username the username
     * @param password the password
     * @return true if authentication was successful, false otherwise
     */
    public boolean authenticate(String username, String password) {
        // Refresh users from database to ensure up-to-date data
        loadUsersFromDatabase();
        
        User user = users.get(username);
        if (user != null && user.validatePassword(password)) {
            currentUser = user;
            logger.info("User authenticated: " + username);
            return true;
        }
        
        logger.warning("Authentication failed for username: " + username);
        return false;
    }
    
    /**
     * Logs out the current user.
     */
    public void logout() {
        if (currentUser != null) {
            logger.info("User logged out: " + currentUser.getUsername());
            currentUser = null;
        }
    }
    
    /**
     * Checks if a user is currently authenticated.
     *
     * @return true if a user is authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        return currentUser != null;
    }
    
    /**
     * Gets the currently authenticated user.
     *
     * @return the current user, or null if no user is authenticated
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Checks if the current user has admin privileges.
     *
     * @return true if the current user is an admin, false otherwise
     */
    public boolean isCurrentUserAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
    
    /**
     * Checks if the current user has manager privileges (or higher).
     *
     * @return true if the current user is a manager or admin, false otherwise
     */
    public boolean isCurrentUserManager() {
        return currentUser != null && currentUser.isManager();
    }
    
    /**
     * Updates a user's password.
     *
     * @param username the username
     * @param oldPassword the current password
     * @param newPassword the new password
     * @return true if the password was updated successfully, false otherwise
     */
    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        // Refresh from database first
        loadUsersFromDatabase();
        
        User user = users.get(username);
        if (user != null && user.validatePassword(oldPassword)) {
            // Update user's password
            user.setPassword(newPassword);
            
            // Update in database
            boolean success = dbManager.updateUserPassword(username, newPassword);
            
            if (success) {
                logger.info("Password updated for user: " + username);
                return true;
            }
        }
        
        logger.warning("Failed to update password for user: " + username);
        return false;
    }
    
    /**
     * Gets a user by username.
     *
     * @param username the username
     * @return the User object if found, null otherwise
     */
    public User getUserByUsername(String username) {
        // Refresh from database first
        loadUsersFromDatabase();
        
        return users.get(username);
    }
    
    /**
     * Gets a user by employee ID.
     *
     * @param employeeId the employee ID
     * @return the User object if found, null otherwise
     */
    public User getUserByEmployeeId(int employeeId) {
        for (User user : users.values()) {
            Integer id = user.getEmployeeId();
            if (id != null && id == employeeId) {
                return user;
            }
        }
        
        // If not found in memory, try database
        return dbManager.getUserByEmployeeId(employeeId);
    }
    
    /**
     * Refreshes the user list from the database.
     */
    public void refreshUsers() {
        loadUsersFromDatabase();
    }
    
    /**
     * Checks if the current user is viewing their own employee record.
     *
     * @param employeeId the employee ID being viewed
     * @return true if the current user is an employee viewing their own record
     */
    public boolean isViewingOwnRecord(int employeeId) {
        if (currentUser == null || currentUser.isAdmin() || currentUser.isManager()) {
            return false;
        }
        
        Integer id = currentUser.getEmployeeId();
        return id != null && id == employeeId;
    }
}