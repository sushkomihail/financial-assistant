package com.sushkomihail.loan;

public class LoanAnalysis {
    private static final float MAX_REVENUE_PERCENTAGE_FOR_PAYMENT = 0.4F;

    private final Loan loan;
    private final float averageIncome;

    public LoanAnalysis(Loan loan, float averageIncome) {
        this.loan = loan;
        this.averageIncome = averageIncome;
    }

    public String perform() {
        String analytics = "Кредитные условия: сумма - " + loan.getAmount() + " руб., срок - " +
                loan.getPeriod() + " месяц(-а/-ев), годовая ставка - " + loan.getInterestRate() + "%.\n" +
                "Ваш средний доход составляет " + averageIncome + " руб.\n";
        double maxPayment = averageIncome * MAX_REVENUE_PERCENTAGE_FOR_PAYMENT;
        analytics += "Рекомендуется, чтобы ежемесячный платеж по кредиту не превышал 40% от вашего дохода " +
                "(в Вашем случае - " + String.format("%.2f", maxPayment) + " руб.)\n" +
                "Максимальный ежемесячный платеж для данных кредитных условий и типе платежа - '" +
                loan.getPaymentType() + "' составит " + String.format("%.2f", loan.getMaxPayment()) + " руб.\n\n";

        if (maxPayment >= loan.getMaxPayment()) {
            analytics += "Ваши финансовые возможности соответствуют рекомендациям.";
        } else {
            analytics += "Ваши финансовые возможности не соответствуют рекомендациям. " +
                    "Возможно, стоит пересмотреть кредитные условия (уменьшит сумму кредита, " +
                    "поискать предложения с более выгодной процентной ставкой или увеличить срок кредита).\n";
        }

        return analytics;
    }
}
