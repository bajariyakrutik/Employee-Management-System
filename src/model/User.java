package model;

/**
 * Represents a user of the Employee Management System.
 * Each user has a username, password, role, and may be associated with an employee.
 */
public class User {
    public enum Role {
        ADMIN,      // Can perform all operations
        MANAGER,    // Can view and modify employee data, but cannot generate reports
        EMPLOYEE    // Can only view employee data
    }
    
    private String username;
    private String password;  // In a real system, this would be hashed
    private Role role;
    private Integer employeeId;  // ID of associated employee (null for admin and manager)
    
    /**
     * Constructs a User with the specified username, password, and role.
     * 
     * @param username the user's username
     * @param password the user's password
     * @param role the user's role
     */
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeId = null;
    }
    
    /**
     * Constructs a User with the specified username, password, role, and employee ID.
     * 
     * @param username the user's username
     * @param password the user's password
     * @param role the user's role
     * @param employeeId the ID of the associated employee
     */
    public User(String username, String password, Role role, Integer employeeId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeId = employeeId;
    }
    
    /**
     * Returns the user's username.
     * 
     * @return the user's username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Returns the user's password.
     * This would normally be hidden or encrypted in a real system.
     * 
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Returns the user's role.
     * 
     * @return the user's role
     */
    public Role getRole() {
        return role;
    }
    
    /**
     * Returns the ID of the associated employee.
     * 
     * @return the employee ID, or null if no employee is associated
     */
    public Integer getEmployeeId() {
        return employeeId;
    }
    
    /**
     * Sets the ID of the associated employee.
     * 
     * @param employeeId the employee ID
     */
    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
    
    /**
     * Validates the provided password against the user's password.
     * 
     * @param password the password to validate
     * @return true if the password is valid, false otherwise
     */
    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }
    
    /**
     * Sets a new password for the user.
     * 
     * @param newPassword the new password
     */
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
    
    /**
     * Checks if the user has admin privileges.
     * 
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
    
    /**
     * Checks if the user has manager privileges (or higher).
     * 
     * @return true if the user is a manager or admin, false otherwise
     */
    public boolean isManager() {
        return role == Role.MANAGER || role == Role.ADMIN;
    }
    
    /**
     * Checks if the user is associated with an employee.
     * 
     * @return true if the user has an associated employee ID, false otherwise
     */
    public boolean isEmployee() {
        return employeeId != null;
    }
    
    /**
     * Changes the user's password.
     * 
     * @param oldPassword the user's current password
     * @param newPassword the user's new password
     * @return true if the password was changed successfully, false otherwise
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (validatePassword(oldPassword)) {
            this.password = newPassword;
            return true;
        }
        return false;
    }
}