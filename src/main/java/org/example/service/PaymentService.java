package org.example.service;

import org.example.entity.BillingEntity;
import org.example.enums.BillingState;

public class PaymentService {
    private int balance;

    public PaymentService(int initialBalance) {
        this.balance = initialBalance;
    }

    public PaymentService() {
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void cashIn(int amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Cash in successful. New balance: $" + balance);
        } else {
            System.out.println("Invalid amount.");
        }
    }

    public boolean makePayment(BillingEntity billing) {
        if (billing.getAmount() > 0 && billing.getAmount() <= balance) {
            balance -= billing.getAmount();
            System.out.println("Payment successful. Remaining balance: $" + balance);
        } else if (billing.getState().equals(BillingState.PAID)) {
            System.out.println("Sorry! This billing has been paid");
            return false;
        } else {
            System.out.println("Sorry! Not enough fund to proceed with payment.");
            return false;
        }
        return true;
    }
}
