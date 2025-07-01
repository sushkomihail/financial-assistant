package com.sushkomihail.llmagent.datastructures;

public class LoanOffer {
    private final String bankName;
    private final int loanAmount;
    private final int loanTerm;
    private final String loanInterest;

    public LoanOffer(String bankName, int loanAmount, int loanTerm, String loanInterest) {
        this.bankName = bankName;
        this.loanAmount = loanAmount;
        this.loanTerm = loanTerm;
        this.loanInterest = loanInterest;
    }

    public String getBankName() {
        return bankName;
    }

    public int getLoanAmount() {
        return loanAmount;
    }

    public int getLoanTerm() {
        return loanTerm;
    }

    public String getLoanInterest() {
        return loanInterest;
    }
}
