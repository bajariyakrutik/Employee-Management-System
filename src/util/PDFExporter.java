package util;

import model.Employee;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * PDFExporter utility class for generating PDF reports.
 * This is a placeholder implementation that would typically use a library like iText or Apache PDFBox.
 * Since those libraries aren't included in this project, this class simulates PDF creation by
 * generating a text file with .pdf extension.
 */
public class PDFExporter {
    private static final Logger logger = Logger.getInstance();

    /**
     * Exports employee data to a PDF file.
     *
     * @param employees the list of employees to include in the report
     * @param filePath the path where the PDF file should be saved
     * @return true if the operation was successful, false otherwise
     */
    public static boolean exportEmployeesToPDF(List<Employee> employees, String filePath) {
        logger.info("Starting PDF export to: " + filePath);
        
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            // In a real implementation, we would use a PDF library here.
            // For this simulation, we'll just write formatted text.
            
            // Add header
            String header = "Employee Report - Generated on " + getCurrentTimestamp() + "\n" +
                          "==========================================================\n\n";
            fos.write(header.getBytes());
            
            // Add employee data
            for (int i = 0; i < employees.size(); i++) {
                Employee e = employees.get(i);
                String employeeData = "Employee #" + (i+1) + ":\n" +
                                     "  ID: " + e.getId() + "\n" +
                                     "  Name: " + e.getName() + "\n" +
                                     "  Department: " + e.getDepartment() + "\n" +
                                     "  Salary: $" + String.format("%.2f", e.getSalary()) + "\n" +
                                     "  Payment Method: " + e.getPaymentMethodName() + "\n\n";
                fos.write(employeeData.getBytes());
            }
            
            // Add footer
            String footer = "End of Report\n" +
                          "Total Employees: " + employees.size();
            fos.write(footer.getBytes());
            
            logger.info("PDF export completed successfully");
            return true;
        } catch (IOException e) {
            logger.error("Failed to export employees to PDF", e);
            return false;
        }
    }
    
    /**
     * Exports payroll data to a PDF file.
     *
     * @param employees the list of employees to include in the report
     * @param filePath the path where the PDF file should be saved
     * @return true if the operation was successful, false otherwise
     */
    public static boolean exportPayrollToPDF(List<Employee> employees, String filePath) {
        logger.info("Starting payroll PDF export to: " + filePath);
        
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            // In a real implementation, we would use a PDF library here.
            // For this simulation, we'll just write formatted text.
            
            // Add header
            String header = "Payroll Report - Generated on " + getCurrentTimestamp() + "\n" +
                          "==========================================================\n\n";
            fos.write(header.getBytes());
            
            // Calculate total salary
            double totalSalary = 0;
            for (Employee e : employees) {
                totalSalary += e.getSalary();
            }
            
            // Add summary
            String summary = "Summary:\n" +
                           "  Total Employees: " + employees.size() + "\n" +
                           "  Total Monthly Salary: $" + String.format("%.2f", totalSalary) + "\n" +
                           "  Average Salary: $" + String.format("%.2f", totalSalary / employees.size()) + "\n\n";
            fos.write(summary.getBytes());
            
            // Add employee pay data
            String payData = "Individual Pay Details:\n" +
                           "==========================================================\n\n";
            fos.write(payData.getBytes());
            
            for (int i = 0; i < employees.size(); i++) {
                Employee e = employees.get(i);
                String employeePayData = "Employee: " + e.getName() + " (ID: " + e.getId() + ")\n" +
                                       "  Department: " + e.getDepartment() + "\n" +
                                       "  Salary: $" + String.format("%.2f", e.getSalary()) + "\n" +
                                       "  Payment Method: " + e.getPaymentMethodName() + "\n" +
                                       "  Payment: " + e.generatePayStub() + "\n\n";
                fos.write(employeePayData.getBytes());
            }
            
            logger.info("Payroll PDF export completed successfully");
            return true;
        } catch (IOException e) {
            logger.error("Failed to export payroll to PDF", e);
            return false;
        }
    }
    
    /**
     * Gets the current timestamp formatted for reports.
     *
     * @return the formatted timestamp
     */
    private static String getCurrentTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }
}