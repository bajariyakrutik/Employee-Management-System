/*
 * Krutik Bajariya
 * PDP Mid-Semester Project
 * Employee Management System
 * Used VSCode for the project
 * Employee.java
 */
package model;

/**
 * Represents an employee in the Employee Management System.
 * Each employee has an ID, name, department, salary, and a payment strategy.
 * The default payment strategy is Direct Deposit.
 * Updated to include getter for PaymentStrategy.
 */
public class Employee {
    private int id;
    private String name;
    private String department;
    private double salary;
    private PaymentStrategy paymentStrategy;

    /**
     * Constructs an Employee with the specified ID, name, department, and salary.
     * The default payment strategy is Direct Deposit.
     * 
     * @param id the employee's ID
     * @param name the employee's name
     * @param department the employee's department
     * @param salary the employee's salary
     */
    public Employee(int id, String name, String department, double salary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
        this.paymentStrategy = new DirectDepositPayment(); // Default payment method
    }

    /**
     * Returns the employee's ID.
     * 
     * @return the employee's ID
     */
    public int getId() { return id; }

    /**
     * Returns the employee's name.
     * 
     * @return the employee's name
     */
    public String getName() { return name; }

    /**
     * Sets the employee's name.
     * 
     * @param name the employee's name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Returns the employee's department.
     * 
     * @return the employee's department
     */
    public String getDepartment() { return department; }

    /**
     * Sets the employee's department.
     * 
     * @param department the employee's department
     */
    public void setDepartment(String department) { this.department = department; }

    /**
     * Returns the employee's salary.
     * 
     * @return the employee's salary
     */
    public double getSalary() { return salary; }

    /**
     * Sets the employee's salary.
     * 
     * @param salary the employee's salary
     */
    public void setSalary(double salary) { this.salary = salary; }

    /**
     * Gets the employee's payment strategy.
     * 
     * @return the employee's payment strategy
     */
    public PaymentStrategy getPaymentStrategy() { return paymentStrategy; }

    /**
     * Sets the employee's payment strategy.
     * 
     * @param paymentStrategy the employee's payment strategy
     */
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) { this.paymentStrategy = paymentStrategy; }
    
    /**
     * Generates a pay stub for the employee.
     * 
     * @return a string representing the pay stub
     */
    public String generatePayStub() {
        return "Paystub: ID: " + id + ", Name: " + name + ", " + paymentStrategy.pay(salary);
    }
    
    /**
     * Returns the name of the payment method being used by this employee.
     *
     * @return "Direct Deposit" or "Check" based on the payment strategy
     */
    public String getPaymentMethodName() {
        if (paymentStrategy instanceof CheckPayment) {
            return "Check";
        } else {
            return "Direct Deposit";
        }
    }
}