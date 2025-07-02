package com.sushkomihail.loan;

public final class AnnuityPaymentLoan extends Loan {
    public AnnuityPaymentLoan(int amount, int period, float interestRate) {
        super(amount, period, interestRate);
        calculatePayments();
        calculateAmount();
    }

    @Override
    protected void calculatePayments() {
        double monthlyInterestRate = interestRate / 1200;
        double payment = amount * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, period) /
                (Math.pow(1 + monthlyInterestRate, period) - 1);
        payments.add(payment);
    }

    @Override
    protected void calculateAmount() {
        paymentsAmount = payments.get(0) * period;
    }
}
