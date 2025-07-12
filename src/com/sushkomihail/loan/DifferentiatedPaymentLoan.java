package com.sushkomihail.loan;

public final class DifferentiatedPaymentLoan extends Loan {
    public DifferentiatedPaymentLoan(int amount, int period, float interestRate) {
        super(amount, period, interestRate);
        calculatePayments();
        calculateAmount();
    }

    @Override
    protected String getPaymentType() {
        return PaymentType.DIFFERENTIATED.getTitle();
    }

    @Override
    protected void calculatePayments() {
        double monthlyInterestRate = interestRate / 1200;
        double basicPayment = (double) amount / period;
        double remainingDebt = amount;

        for (int i = 0; i < period; i++) {
            double payment = basicPayment + remainingDebt * monthlyInterestRate;
            payments.add(payment);
            remainingDebt -= basicPayment;
        }
    }

    @Override
    protected void calculateAmount() {
        for (Double payment : payments) {
            paymentsAmount += payment;
        }
    }
}
