package util;

import model.Employee;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ReportGenerator class for generating and exporting reports.
 */
public class ReportGenerator {
    /**
     * Generates and exports a CSV report of all employees.
     *
     * @param employees the list of employees to include in the report
     * @param filePath the path where the CSV file should be saved
     * @return true if the operation was successful, false otherwise
     */
    public static boolean generateEmployeeCSVReport(List<Employee> employees, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write CSV header
            writer.println("ID,Name,Department,Salary,Payment Method");
            
            // Write employee data
            for (Employee e : employees) {
                writer.println(
                    e.getId() + "," +
                    escapeCsvField(e.getName()) + "," +
                    escapeCsvField(e.getDepartment()) + "," +
                    e.getSalary() + "," +
                    escapeCsvField(e.getPaymentMethodName())
                );
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error generating CSV report: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Generates and exports a payroll report of all employees.
     *
     * @param employees the list of employees to include in the report
     * @param filePath the path where the report file should be saved
     * @return true if the operation was successful, false otherwise
     */
    public static boolean generatePayrollReport(List<Employee> employees, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = dateFormat.format(new Date());
            
            // Write report header
            writer.println("Payroll Report - Generated on " + currentDate);
            writer.println("====================================================");
            writer.println();
            
            // Write summary statistics
            double totalSalary = 0;
            for (Employee e : employees) {
                totalSalary += e.getSalary();
            }
            
            writer.println("Total Employees: " + employees.size());
            writer.println("Total Salary Payout: $" + String.format("%.2f", totalSalary));
            writer.println();
            
            // Write detailed employee information
            writer.println("Employee Details:");
            writer.println("----------------------------------------------------");
            for (Employee e : employees) {
                writer.println("ID: " + e.getId());
                writer.println("Name: " + e.getName());
                writer.println("Department: " + e.getDepartment());
                writer.println("Salary: $" + String.format("%.2f", e.getSalary()));
                writer.println("Payment Method: " + e.getPaymentMethodName());
                writer.println("----------------------------------------------------");
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error generating payroll report: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Escapes special characters in CSV fields to prevent parsing issues.
     *
     * @param field the CSV field to escape
     * @return the escaped CSV field
     */
    private static String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        
        // If the field contains commas, quotes, or newlines, enclose it in quotes
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            // Replace any quotes with double quotes
            field = field.replace("\"", "\"\"");
            // Enclose the field in quotes
            return "\"" + field + "\"";
        }
        
        return field;
    }
}