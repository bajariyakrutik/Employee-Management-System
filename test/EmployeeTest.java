/*
 * Krutik Bajariya
 * PDP Mid-Semester Project
 * Employee Management System
 * Used VSCode for the project
 * Used JUnit 5 for testing
 * EmployeeTest.java
 */
package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import model.Employee;
import model.DirectDepositPayment;
import model.CheckPayment;
import model.PaymentStrategy;

/**
 * Test class for the Employee model.
 */
public class EmployeeTest {
    
    private Employee employee;
    
    @BeforeEach
    public void setUp() {
        // Create a test employee before each test
        employee = new Employee(1001, "Test Employee", "Test Department", 60000.0);
    }
    
    @Test
    public void testEmployeeCreation() {
        assertEquals(1001, employee.getId(), "Employee ID should match constructor value");
        assertEquals("Test Employee", employee.getName(), "Employee name should match constructor value");
        assertEquals("Test Department", employee.getDepartment(), "Employee department should match constructor value");
        assertEquals(60000.0, employee.getSalary(), 0.001, "Employee salary should match constructor value");
        assertTrue(employee.getPaymentStrategy() instanceof DirectDepositPayment, 
                  "Default payment strategy should be DirectDepositPayment");
    }
    
    @Test
    public void testSetName() {
        employee.setName("Updated Name");
        assertEquals("Updated Name", employee.getName(), "Employee name should be updated");
    }
    
    @Test
    public void testSetDepartment() {
        employee.setDepartment("New Department");
        assertEquals("New Department", employee.getDepartment(), "Employee department should be updated");
    }
    
    @Test
    public void testSetSalary() {
        employee.setSalary(65000.0);
        assertEquals(65000.0, employee.getSalary(), 0.001, "Employee salary should be updated");
    }
    
    @Test
    public void testSetPaymentStrategy() {
        // Test changing to Check payment
        PaymentStrategy checkPayment = new CheckPayment();
        employee.setPaymentStrategy(checkPayment);
        assertSame(checkPayment, employee.getPaymentStrategy(), "Payment strategy should be updated");
        
        // Test changing back to Direct Deposit
        PaymentStrategy directDeposit = new DirectDepositPayment();
        employee.setPaymentStrategy(directDeposit);
        assertSame(directDeposit, employee.getPaymentStrategy(), "Payment strategy should be updated");
    }
    
    @Test
    public void testGeneratePayStub() {
        // Test pay stub with Direct Deposit (default)
        String payStub = employee.generatePayStub();
        assertTrue(payStub.contains("ID: 1001"), "Pay stub should contain employee ID");
        assertTrue(payStub.contains("Name: Test Employee"), "Pay stub should contain employee name");
        assertTrue(payStub.contains("Direct Deposit"), "Pay stub should mention Direct Deposit");
        assertTrue(payStub.contains("60000.0"), "Pay stub should contain salary amount");
        
        // Test pay stub with Check payment
        employee.setPaymentStrategy(new CheckPayment());
        payStub = employee.generatePayStub();
        assertTrue(payStub.contains("Check"), "Pay stub should mention Check payment");
    }
    
    @Test
    public void testGetPaymentMethodName() {
        // Test default payment method
        assertEquals("Direct Deposit", employee.getPaymentMethodName(), 
                    "Default payment method name should be Direct Deposit");
        
        // Test with Check payment
        employee.setPaymentStrategy(new CheckPayment());
        assertEquals("Check", employee.getPaymentMethodName(), 
                    "Payment method name should be Check after changing strategy");
    }
}