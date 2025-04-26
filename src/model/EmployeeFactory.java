/*
 * Krutik Bajariya
 * PDP Mid-Semester Project
 * Employee Management System
 * Used VSCode for the project
 * EmployeeFactory.java
 */
package model;

/**
 * EmployeeFactory is an interface for creating Employee objects.
 */
public interface EmployeeFactory {
    /**
     * Creates an Employee object with the given parameters.
     *
     * @param id the unique identifier for the employee
     * @param name the name of the employee
     * @param department the department in which the employee works
     * @param salary the salary of the employee
     * @return a new Employee object
     */
    Employee createEmployee(int id, String name, String department, double salary);
}