package util;

import model.Employee;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * SimplePDFExporter utility class for generating minimalistic PDF reports.
 * Uses a simplified approach that avoids version compatibility issues.
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
        logger.info("Starting simple PDF export to: " + filePath);
        
        PDDocument document = null;
        
        try {
            document = new PDDocument();
            PDFont font = PDType1Font.HELVETICA;
            PDFont boldFont = PDType1Font.HELVETICA_BOLD;
            
            // Create first page
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Add title
            float margin = 50;
            float yPosition = page.getMediaBox().getHeight() - margin;
            float lineHeight = 15;
            
            contentStream.setFont(boldFont, 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Employee Report");
            contentStream.endText();
            
            yPosition -= lineHeight * 2;
            
            // Add timestamp
            contentStream.setFont(font, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Generated on: " + getCurrentTimestamp());
            contentStream.endText();
            
            yPosition -= lineHeight * 3;
            
            // Add employee data
            for (int i = 0; i < employees.size(); i++) {
                Employee e = employees.get(i);
                
                // Check if we need a new page
                if (yPosition < 100) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    yPosition = page.getMediaBox().getHeight() - margin;
                }
                
                // Employee name
                contentStream.setFont(boldFont, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Employee #" + (i+1) + ": " + e.getName());
                contentStream.endText();
                
                yPosition -= lineHeight;
                
                // Employee details
                contentStream.setFont(font, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 20, yPosition);
                contentStream.showText("ID: " + e.getId());
                contentStream.endText();
                
                yPosition -= lineHeight;
                
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 20, yPosition);
                contentStream.showText("Department: " + e.getDepartment());
                contentStream.endText();
                
                yPosition -= lineHeight;
                
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 20, yPosition);
                contentStream.showText("Salary: $" + String.format("%.2f", e.getSalary()));
                contentStream.endText();
                
                yPosition -= lineHeight;
                
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 20, yPosition);
                contentStream.showText("Payment Method: " + e.getPaymentMethodName());
                contentStream.endText();
                
                yPosition -= lineHeight * 2;
            }
            
            // Add footer
            contentStream.setFont(font, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, 50);
            contentStream.showText("Total Employees: " + employees.size());
            contentStream.endText();
            
            contentStream.close();
            document.save(filePath);
            
            logger.info("Simple PDF export completed successfully");
            return true;
        } catch (IOException e) {
            logger.error("Failed to export employees to PDF", e);
            return false;
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    logger.error("Error closing PDF document", e);
                }
            }
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
        logger.info("Starting simple payroll PDF export to: " + filePath);
        
        PDDocument document = null;
        
        try {
            document = new PDDocument();
            PDFont font = PDType1Font.HELVETICA;
            PDFont boldFont = PDType1Font.HELVETICA_BOLD;
            
            // Calculate total salary
            double totalSalary = 0;
            for (Employee e : employees) {
                totalSalary += e.getSalary();
            }
            double averageSalary = employees.size() > 0 ? totalSalary / employees.size() : 0;
            
            // Create first page
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Add title
            float margin = 50;
            float yPosition = page.getMediaBox().getHeight() - margin;
            float lineHeight = 15;
            
            contentStream.setFont(boldFont, 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Payroll Report");
            contentStream.endText();
            
            yPosition -= lineHeight * 2;
            
            // Add timestamp
            contentStream.setFont(font, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Generated on: " + getCurrentTimestamp());
            contentStream.endText();
            
            yPosition -= lineHeight * 3;
            
            // Add summary
            contentStream.setFont(boldFont, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Summary:");
            contentStream.endText();
            
            yPosition -= lineHeight * 1.5;
            
            contentStream.setFont(font, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin + 20, yPosition);
            contentStream.showText("Total Employees: " + employees.size());
            contentStream.endText();
            
            yPosition -= lineHeight;
            
            contentStream.beginText();
            contentStream.newLineAtOffset(margin + 20, yPosition);
            contentStream.showText("Total Monthly Salary: $" + String.format("%.2f", totalSalary));
            contentStream.endText();
            
            yPosition -= lineHeight;
            
            contentStream.beginText();
            contentStream.newLineAtOffset(margin + 20, yPosition);
            contentStream.showText("Average Salary: $" + String.format("%.2f", averageSalary));
            contentStream.endText();
            
            yPosition -= lineHeight * 3;
            
            // Add employee data
            contentStream.setFont(boldFont, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Individual Pay Details:");
            contentStream.endText();
            
            yPosition -= lineHeight * 2;
            
            for (int i = 0; i < employees.size(); i++) {
                Employee e = employees.get(i);
                
                // Check if we need a new page
                if (yPosition < 100) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    yPosition = page.getMediaBox().getHeight() - margin;
                }
                
                // Employee name
                contentStream.setFont(boldFont, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Employee: " + e.getName());
                contentStream.endText();
                
                yPosition -= lineHeight;
                
                // Employee details
                contentStream.setFont(font, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 20, yPosition);
                contentStream.showText("ID: " + e.getId());
                contentStream.endText();
                
                yPosition -= lineHeight;
                
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 20, yPosition);
                contentStream.showText("Department: " + e.getDepartment());
                contentStream.endText();
                
                yPosition -= lineHeight;
                
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 20, yPosition);
                contentStream.showText("Salary: $" + String.format("%.2f", e.getSalary()));
                contentStream.endText();
                
                yPosition -= lineHeight;
                
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 20, yPosition);
                contentStream.showText("Payment Method: " + e.getPaymentMethodName());
                contentStream.endText();
                
                yPosition -= lineHeight;
                
                // Truncate paystub if too long
                String payStub = e.generatePayStub();
                if (payStub.length() > 50) {
                    payStub = payStub.substring(0, 50) + "...";
                }
                
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 20, yPosition);
                contentStream.showText("Payment: " + payStub);
                contentStream.endText();
                
                yPosition -= lineHeight * 2;
            }
            
            // Add footer
            contentStream.setFont(font, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, 50);
            contentStream.showText("End of Payroll Report");
            contentStream.endText();
            
            contentStream.close();
            document.save(filePath);
            
            logger.info("Simple payroll PDF export completed successfully");
            return true;
        } catch (IOException e) {
            logger.error("Failed to export payroll to PDF", e);
            return false;
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    logger.error("Error closing PDF document", e);
                }
            }
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