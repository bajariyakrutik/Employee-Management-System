/*
 * Krutik Bajariya
 * PDP Project
 * Employee Management System
 * Used VSCode for the project
 * DatabaseManager.java
 */
package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.Logger;

/**
 * DatabaseManager class for handling database operations using SQLite.
 * Updated to handle user accounts for all employees.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:employee_management.db";
    private static DatabaseManager instance;
    private static final Logger logger = Logger.getInstance();
    
    /**
     * Private constructor to prevent instantiation.
     * Initializes the database.
     */
    private DatabaseManager() {
        initializeDatabase();
    }
    
    /**
     * Returns the singleton instance of the DatabaseManager.
     * If the instance does not exist, it creates one.
     *
     * @return the singleton instance of the DatabaseManager
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Initializes the database by creating the necessary tables if they don't exist.
     */
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            // Create employees table
            String employeesSql = "CREATE TABLE IF NOT EXISTS employees (" +
                         "id INTEGER PRIMARY KEY, " +
                         "name TEXT NOT NULL, " +
                         "department TEXT NOT NULL, " +
                         "salary REAL NOT NULL, " +
                         "payment_method TEXT NOT NULL" +
                         ")";
            stmt.execute(employeesSql);
            
            // Create users table
            String usersSql = "CREATE TABLE IF NOT EXISTS users (" +
                         "username TEXT PRIMARY KEY, " +
                         "password TEXT NOT NULL, " +
                         "role TEXT NOT NULL, " +
                         "employee_id INTEGER, " +
                         "FOREIGN KEY (employee_id) REFERENCES employees(id)" +
                         ")";
            stmt.execute(usersSql);
            
            // Create admin user if it doesn't exist
            String checkAdminSql = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
            ResultSet rs = stmt.executeQuery(checkAdminSql);
            if (rs.next() && rs.getInt(1) == 0) {
                String insertAdminSql = "INSERT INTO users (username, password, role, employee_id) VALUES ('admin', 'admin123', 'ADMIN', 0)";
                stmt.execute(insertAdminSql);
                logger.info("Created default admin user");
            }

            // Create manager user if it doesn't exist
            String checkManagerSql = "SELECT COUNT(*) FROM users WHERE username = 'manager'";
            rs = stmt.executeQuery(checkManagerSql);
            if (rs.next() && rs.getInt(1) == 0) {
                String insertManagerSql = "INSERT INTO users (username, password, role, employee_id) VALUES ('manager', 'manager123', 'MANAGER', 1)";
                stmt.execute(insertManagerSql);
                logger.info("Created default manager user");
            }
            
            logger.info("Database initialized successfully");
            
        } catch (SQLException e) {
            logger.error("Error initializing database: " + e.getMessage(), e);
        }
    }
    
    /**
     * Establishes a connection to the database.
     *
     * @return a Connection object to the database
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    /**
     * Adds an employee to the database.
     *
     * @param employee the Employee object to be added
     * @return true if the operation was successful, false otherwise
     */
    public boolean addEmployee(Employee employee) {
        String sql = "INSERT INTO employees (id, name, department, salary, payment_method) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employee.getId());
            pstmt.setString(2, employee.getName());
            pstmt.setString(3, employee.getDepartment());
            pstmt.setDouble(4, employee.getSalary());
            
            // Determine payment method from PaymentStrategy
            String paymentMethod = "Direct Deposit"; // Default
            if (employee.getPaymentStrategy() instanceof CheckPayment) {
                paymentMethod = "Check";
            }
            pstmt.setString(5, paymentMethod);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Create user account for the employee
                createUserForEmployee(employee);
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            logger.error("Error adding employee: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Creates a user account for an employee.
     * The username is the employee's name (lowercase, spaces replaced with underscores)
     * The default password is the employee's ID followed by their name (first 3 characters)
     *
     * @param employee the Employee object
     * @return true if successful, false otherwise
     */
    private boolean createUserForEmployee(Employee employee) {
        String username = employee.getName().toLowerCase().replace(' ', '_');
        String password = employee.getId() + employee.getName().substring(0, Math.min(3, employee.getName().length()));
        
        String sql = "INSERT INTO users (username, password, role, employee_id) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, "EMPLOYEE");
            pstmt.setInt(4, employee.getId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Created user account for employee: " + employee.getName() + " with username: " + username);
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            logger.error("Error creating user for employee: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Updates an employee in the database.
     *
     * @param employee the Employee object with updated information
     * @return true if the operation was successful, false otherwise
     */
    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE employees SET name = ?, department = ?, salary = ?, payment_method = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getDepartment());
            pstmt.setDouble(3, employee.getSalary());
            
            // Determine payment method from PaymentStrategy
            String paymentMethod = "Direct Deposit"; // Default
            if (employee.getPaymentStrategy() instanceof CheckPayment) {
                paymentMethod = "Check";
            }
            pstmt.setString(4, paymentMethod);
            pstmt.setInt(5, employee.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            logger.error("Error updating employee: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Removes an employee from the database by ID.
     * Also removes the associated user account.
     *
     * @param id the ID of the employee to be removed
     * @return true if the operation was successful, false otherwise
     */
    public boolean removeEmployee(int id) {
        // First remove the user account
        String userSql = "DELETE FROM users WHERE employee_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement userStmt = conn.prepareStatement(userSql)) {
            
            userStmt.setInt(1, id);
            userStmt.executeUpdate();
            
            // Then remove the employee
            String employeeSql = "DELETE FROM employees WHERE id = ?";
            try (PreparedStatement empStmt = conn.prepareStatement(employeeSql)) {
                empStmt.setInt(1, id);
                int affectedRows = empStmt.executeUpdate();
                return affectedRows > 0;
            }
            
        } catch (SQLException e) {
            logger.error("Error removing employee: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Retrieves all employees from the database.
     *
     * @return a list of Employee objects
     */
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String department = rs.getString("department");
                double salary = rs.getDouble("salary");
                String paymentMethod = rs.getString("payment_method");
                
                Employee employee = new Employee(id, name, department, salary);
                
                // Set payment strategy based on stored value
                if ("Check".equals(paymentMethod)) {
                    employee.setPaymentStrategy(new CheckPayment());
                } else {
                    employee.setPaymentStrategy(new DirectDepositPayment());
                }
                
                employees.add(employee);
            }
            
        } catch (SQLException e) {
            logger.error("Error retrieving employees: " + e.getMessage(), e);
        }
        
        return employees;
    }
    
    /**
     * Retrieves an employee from the database by ID.
     *
     * @param id the ID of the employee to retrieve
     * @return the Employee object if found, null otherwise
     */
    public Employee getEmployeeById(int id) {
        String sql = "SELECT * FROM employees WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                String department = rs.getString("department");
                double salary = rs.getDouble("salary");
                String paymentMethod = rs.getString("payment_method");
                
                Employee employee = new Employee(id, name, department, salary);
                
                // Set payment strategy based on stored value
                if ("Check".equals(paymentMethod)) {
                    employee.setPaymentStrategy(new CheckPayment());
                } else {
                    employee.setPaymentStrategy(new DirectDepositPayment());
                }
                
                return employee;
            }
            
        } catch (SQLException e) {
            logger.error("Error retrieving employee: " + e.getMessage(), e);
        }
        
        return null;
    }
    
    /**
     * Updates the payment method for an employee.
     *
     * @param id the ID of the employee
     * @param paymentMethod the new payment method ("Direct Deposit" or "Check")
     * @return true if the operation was successful, false otherwise
     */
    public boolean updatePaymentMethod(int id, String paymentMethod) {
        String sql = "UPDATE employees SET payment_method = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, paymentMethod);
            pstmt.setInt(2, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            logger.error("Error updating payment method: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Retrieves all users from the database.
     *
     * @return a list of User objects
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String roleStr = rs.getString("role");
                Integer employeeId = rs.getInt("employee_id");
                if (rs.wasNull()) {
                    employeeId = null;
                }
                
                User.Role role = User.Role.valueOf(roleStr);
                User user = new User(username, password, role);
                user.setEmployeeId(employeeId);
                
                users.add(user);
            }
            
        } catch (SQLException e) {
            logger.error("Error retrieving users: " + e.getMessage(), e);
        }
        
        return users;
    }
    
    /**
     * Gets a user by username.
     *
     * @param username the username to search for
     * @return the User object if found, null otherwise
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String password = rs.getString("password");
                String roleStr = rs.getString("role");
                Integer employeeId = rs.getInt("employee_id");
                if (rs.wasNull()) {
                    employeeId = null;
                }
                
                User.Role role = User.Role.valueOf(roleStr);
                User user = new User(username, password, role);
                user.setEmployeeId(employeeId);
                
                return user;
            }
            
        } catch (SQLException e) {
            logger.error("Error retrieving user: " + e.getMessage(), e);
        }
        
        return null;
    }
    
    /**
     * Gets a user by employee ID.
     *
     * @param employeeId the employee ID to search for
     * @return the User object if found, null otherwise
     */
    public User getUserByEmployeeId(int employeeId) {
        String sql = "SELECT * FROM users WHERE employee_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String roleStr = rs.getString("role");
                
                User.Role role = User.Role.valueOf(roleStr);
                User user = new User(username, password, role);
                user.setEmployeeId(employeeId);
                
                return user;
            }
            
        } catch (SQLException e) {
            logger.error("Error retrieving user by employee ID: " + e.getMessage(), e);
        }
        
        return null;
    }
    
    /**
     * Updates a user's password.
     *
     * @param username the username
     * @param newPassword the new password
     * @return true if successful, false otherwise
     */
    public boolean updateUserPassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            logger.error("Error updating user password: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Adds a new user to the database.
     *
     * @param user the User object to add
     * @return true if successful, false otherwise
     */
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password, role, employee_id) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole().toString());
            
            Integer employeeId = user.getEmployeeId();
            if (employeeId == null) {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(4, employeeId);
            }
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            logger.error("Error adding user: " + e.getMessage(), e);
            return false;
        }
    }
}