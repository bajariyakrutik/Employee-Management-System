/*
 * Krutik Bajariya
 * PDP Mid-Semester Project
 * Employee Management System
 * Used VSCode for the project
 * EmployeeController.java
 */
package controller;

import java.util.List;
import model.Database;
import model.Employee;
import model.EmployeeFactory;
import model.ConcreteEmployeeFactory;
import model.CheckPayment;
import model.DirectDepositPayment;
import util.ReportGenerator;
import util.PDFExporter;

/**
 * Controller class for managing employees.
 * This class interacts with the database and employee factory to perform CRUD operations on employees.
 * Updated to include report generation functionality.
 */
public class EmployeeController {
    private Database database;
    private EmployeeFactory employeeFactory;
    
    /**
     * Constructor for EmployeeController.
     * Initializes the database and employee factory instances.
     */
    public EmployeeController() {
        this.database = Database.getInstance();
        this.employeeFactory = new ConcreteEmployeeFactory();
    }
    
    /**
     * Adds a new employee to the database.
     * 
     * @param id the ID of the employee
     * @param name the name of the employee
     * @param department the department of the employee
     * @param salary the salary of the employee
     */
    public void addEmployee(int id, String name, String department, double salary) {
        Employee e = employeeFactory.createEmployee(id, name, department, salary);
        database.addEmployee(e);
    }
    
    /**
     * Adds a new employee to the database with specified payment method.
     * 
     * @param id the ID of the employee
     * @param name the name of the employee
     * @param department the department of the employee
     * @param salary the salary of the employee
     * @param paymentMethod the payment method ("Direct Deposit" or "Check")
     */
    public void addEmployee(int id, String name, String department, double salary, String paymentMethod) {
        Employee e = employeeFactory.createEmployee(id, name, department, salary);
        
        // Set payment method based on selection
        if ("Check".equals(paymentMethod)) {
            e.setPaymentStrategy(new CheckPayment());
        } else {
            e.setPaymentStrategy(new DirectDepositPayment());  // Default
        }
        
        database.addEmployee(e);
    }
    
    /**
     * Removes an employee from the database by ID.
     * 
     * @param id the ID of the employee to be removed
     */
    public void removeEmployee(int id) {
        database.removeEmployee(id);
    }
    
    /**
     * Updates the details of an existing employee.
     * 
     * @param id the ID of the employee to be updated
     * @param name the new name of the employee
     * @param department the new department of the employee
     * @param salary the new salary of the employee
     */
    public void updateEmployee(int id, String name, String department, double salary) {
        Employee e = database.getEmployeeById(id);
        if (e != null) {
            e.setName(name);
            e.setDepartment(department);
            e.setSalary(salary);
            database.updateEmployee(e);
        }
    }
    
    /**
     * Updates the details of an existing employee including payment method.
     * 
     * @param id the ID of the employee to be updated
     * @param name the new name of the employee
     * @param department the new department of the employee
     * @param salary the new salary of the employee
     * @param paymentMethod the payment method ("Direct Deposit" or "Check")
     */
    public void updateEmployee(int id, String name, String department, double salary, String paymentMethod) {
        Employee e = database.getEmployeeById(id);
        if (e != null) {
            e.setName(name);
            e.setDepartment(department);
            e.setSalary(salary);
            
            // Update payment method
            if ("Check".equals(paymentMethod)) {
                e.setPaymentStrategy(new CheckPayment());
            } else {
                e.setPaymentStrategy(new DirectDepositPayment());
            }
            
            database.updateEmployee(e);
        }
    }
    
    /**
     * Changes the payment method for an employee.
     * 
     * @param id the ID of the employee
     * @param paymentMethod the payment method ("Direct Deposit" or "Check")
     * @return true if employee found and updated, false otherwise
     */
    public boolean changePaymentMethod(int id, String paymentMethod) {
        return database.updatePaymentMethod(id, paymentMethod);
    }
    
    /**
     * Retrieves a list of all employees from the database.
     * 
     * @return a list of all employees
     */
    public List<Employee> getAllEmployees() {
        return database.getEmployees();
    }
    
    /**
     * Retrieves an employee by ID.
     * 
     * @param id the ID of the employee to find
     * @return the employee if found, null otherwise
     */
    public Employee getEmployeeById(int id) {
        return database.getEmployeeById(id);
    }
    
    /**
     * Generates pay stubs for all employees.
     * 
     * @return a string containing the pay stubs for all employees
     */
    public String payAllEmployees() {
        StringBuilder payStubs = new StringBuilder();
        for (Employee e : database.getEmployees()) {
            payStubs.append(e.generatePayStub()).append("\n");
        }
        return payStubs.toString();
    }
    
    /**
     * Generates and exports a CSV report of all employees.
     * 
     * @param filePath the path where the CSV file should be saved
     * @return true if the operation was successful, false otherwise
     */
    public boolean generateEmployeeCSVReport(String filePath) {
        List<Employee> employees = database.getEmployees();
        return ReportGenerator.generateEmployeeCSVReport(employees, filePath);
    }
    
    /**
     * Generates and exports a payroll report.
     * 
     * @param filePath the path where the report file should be saved
     * @return true if the operation was successful, false otherwise
     */
    public boolean generatePayrollReport(String filePath) {
        List<Employee> employees = database.getEmployees();
        return ReportGenerator.generatePayrollReport(employees, filePath);
    }
    
    /**
     * Refreshes the database connection to ensure the latest data is loaded.
     */
    public void refreshDatabase() {
        database.refreshFromDatabase();
    }
    
    /**
     * Generates and exports a PDF report of all employees.
     * 
     * @param filePath the path where the PDF file should be saved
     * @return true if the operation was successful, false otherwise
     */
    public boolean generateEmployeePDFReport(String filePath) {
        List<Employee> employees = database.getEmployees();
        return PDFExporter.exportEmployeesToPDF(employees, filePath);
    }
    
    /**
     * Generates and exports a PDF payroll report.
     * 
     * @param filePath the path where the PDF file should be saved
     * @return true if the operation was successful, false otherwise
     */
    public boolean generatePayrollPDFReport(String filePath) {
        List<Employee> employees = database.getEmployees();
        return PDFExporter.exportPayrollToPDF(employees, filePath);
    }
}