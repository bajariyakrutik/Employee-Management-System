/*
 * Krutik Bajariya
 * PDP Mid-Semester Project
 * Employee Management System
 * Used VSCode for the project
 * CheckPayment.java
 */
package model;

/**
 * The CheckPayment class implements the PaymentStrategy interface
 * and provides a concrete implementation for paying via check.
 */
public class CheckPayment implements PaymentStrategy {
    /**
     * Processes the payment and returns a confirmation message.
     * 
     * @param amount the amount to be paid
     * @return a confirmation message indicating the payment method and amount
     */
    @Override
    public String pay(double amount) {
        return "Paid " + amount + " via Check.";
    }
}
