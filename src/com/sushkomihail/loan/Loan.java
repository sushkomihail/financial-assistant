package com.sushkomihail.loan;

import java.util.ArrayList;
import java.util.List;

public abstract class Loan {
    protected final int amount;
    protected final int period;
    protected final float interestRate;

    protected List<Double> payments = new ArrayList<>();
    protected double paymentsAmount;

    public Loan(int amount, int period, float interestRate) {
        this.amount = amount;
        this.period = period;
        this.interestRate = interestRate;
    }

    public int getAmount() {
        return amount;
    }

    public int getPeriod() {
        return period;
    }

    public float getInterestRate() {
        return interestRate;
    }

    public List<Double> getPayments() {
        return payments;
    }

    public double getPaymentsAmount() {
        return paymentsAmount;
    }

    public double getMaxPayment() {
        double max = payments.get(0);

        for (int i = 1; i < payments.size(); i++) {
            if (payments.get(i) > max) {
                max = payments.get(i);
            }
        }

        return max;
    }

    protected abstract String getPaymentType();

    protected abstract void calculatePayments();

    protected abstract void calculateAmount();
}
