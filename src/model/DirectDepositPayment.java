/*
 * Krutik Bajariya
 * PDP Project
 * Employee Management System
 * Used VSCode for the project
 * DirectDepositPayment.java
 */
package model;

/**
 * The DirectDepositPayment class implements the PaymentStrategy interface
 * and provides a concrete implementation for making payments via direct deposit.
 */
public class DirectDepositPayment implements PaymentStrategy {

    /**
     * Processes the payment of the specified amount via direct deposit.
     * 
     * @param amount the amount to be paid
     * @return a string confirming the payment via direct deposit
     */
    @Override
    public String pay(double amount) {
        return "Paid " + amount + " via Direct Deposit.";
    }
}