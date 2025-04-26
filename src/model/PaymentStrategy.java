/*
 * Krutik Bajariya
 * PDP Project
 * Employee Management System
 * Used VSCode for the project
 * PaymentStrategy.java
 */
package model;

/**
 * PaymentStrategy is an interface for defining different payment strategies.
 * Implementing classes should provide their own implementation of the pay method.
 */
public interface PaymentStrategy {
    /**
     * Processes a payment of the specified amount.
     *
     * @param amount the amount to be paid
     * @return a String message indicating the result of the payment process
     */
    String pay(double amount);
}