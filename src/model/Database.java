/*
 * Krutik Bajariya
 * PDP Project
 * Employee Management System
 * Used VSCode for the project
 * Database.java
 */
package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class representing a database of employees.
 * Updated to use both in-memory storage and SQLite persistence.
 */
public class Database {
    private static Database instance;
    private List<Employee> employeeList;
    private DatabaseManager dbManager;
    private boolean useInMemoryOnly = false; // Flag to determine storage mode
    
    /**
     * Private constructor to prevent instantiation.
     * Initializes the employee list and database manager.
     */
    private Database() {
        employeeList = new ArrayList<>();
        dbManager = DatabaseManager.getInstance();
        loadEmployeesFromDatabase();
    }
    
    /**
     * Returns the singleton instance of the Database.
     * If the instance does not exist, it creates one.
     *
     * @return the singleton instance of the Database
     */
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
    
    /**
     * Sets whether to use only in-memory storage without database persistence.
     * Useful for testing.
     *
     * @param useInMemoryOnly true to use only in-memory storage, false to use database persistence
     */
    public void setUseInMemoryOnly(boolean useInMemoryOnly) {
        this.useInMemoryOnly = useInMemoryOnly;
    }
    
    /**
     * Loads employees from the database into memory.
     */
    private void loadEmployeesFromDatabase() {
        if (!useInMemoryOnly) {
            employeeList = dbManager.getAllEmployees();
        }
    }
    
    /**
     * Adds an employee to the database and in-memory list.
     *
     * @param e the employee to be added
     */
    public void addEmployee(Employee e) { 
        employeeList.add(e);
        
        if (!useInMemoryOnly) {
            dbManager.addEmployee(e);
        }
    }
    
    /**
     * Removes an employee from the database and in-memory list by their ID.
     *
     * @param id the ID of the employee to be removed
     */
    public void removeEmployee(int id) {
        employeeList.removeIf(e -> e.getId() == id);
        
        if (!useInMemoryOnly) {
            dbManager.removeEmployee(id);
        }
    }
    
    /**
     * Updates an employee in the database.
     *
     * @param e the employee to be updated
     */
    public void updateEmployee(Employee e) {
        for (int i = 0; i < employeeList.size(); i++) {
            if (employeeList.get(i).getId() == e.getId()) {
                employeeList.set(i, e);
                break;
            }
        }
        
        if (!useInMemoryOnly) {
            dbManager.updateEmployee(e);
        }
    }
    
    /**
     * Updates just the payment method for an employee.
     *
     * @param id the ID of the employee
     * @param paymentMethod the new payment method ("Direct Deposit" or "Check")
     * @return true if successful, false otherwise
     */
    public boolean updatePaymentMethod(int id, String paymentMethod) {
        boolean updated = false;
        
        for (Employee e : employeeList) {
            if (e.getId() == id) {
                if ("Check".equals(paymentMethod)) {
                    e.setPaymentStrategy(new CheckPayment());
                } else {
                    e.setPaymentStrategy(new DirectDepositPayment());
                }
                updated = true;
                break;
            }
        }
        
        if (!useInMemoryOnly && updated) {
            dbManager.updatePaymentMethod(id, paymentMethod);
        }
        
        return updated;
    }
    
    /**
     * Returns the list of employees in the database.
     *
     * @return the list of employees
     */
    public List<Employee> getEmployees() { 
        return employeeList; 
    }
    
    /**
     * Refreshes the in-memory employee list from the database.
     * Useful when the database might have been updated externally.
     */
    public void refreshFromDatabase() {
        if (!useInMemoryOnly) {
            loadEmployeesFromDatabase();
        }
    }
    
    /**
     * Retrieves an employee by ID.
     *
     * @param id the ID of the employee to find
     * @return the employee if found, null otherwise
     */
    public Employee getEmployeeById(int id) {
        if (!useInMemoryOnly) {
            // Try to get from database first
            Employee e = dbManager.getEmployeeById(id);
            if (e != null) {
                return e;
            }
        }
        
        // Fallback to in-memory search
        for (Employee e : employeeList) {
            if (e.getId() == id) {
                return e;
            }
        }
        
        return null;
    }
}