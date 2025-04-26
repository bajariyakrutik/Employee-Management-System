/*
 * Krutik Bajariya
 * PDP Project
 * Employee Management System
 * Used VSCode for the project
 * ConcreteEmployeeFactory.java
 */
package model;

/**
 * ConcreteEmployeeFactory is a factory class that implements the EmployeeFactory interface.
 * It is responsible for creating instances of Employee.
 */
public class ConcreteEmployeeFactory implements EmployeeFactory {

    /**
     * Creates a new Employee instance with the given parameters.
     *
     * @param id the unique identifier for the employee
     * @param name the name of the employee
     * @param department the department in which the employee works
     * @param salary the salary of the employee
     * @return a new Employee instance
     */
    @Override
    public Employee createEmployee(int id, String name, String department, double salary) {
        return new Employee(id, name, department, salary);
    }
}