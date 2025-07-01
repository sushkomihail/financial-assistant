package com.sushkomihail.llmagent.responses;

import com.sushkomihail.llmagent.datastructures.LoanOffer;

import java.util.ArrayList;
import java.util.List;

public class LoanOffersResponse implements ILlmAgentResponse {
    private final int loanAmount;
    private final int loanTerm;
    private final List<LoanOffer> offers = new ArrayList<>();

    public LoanOffersResponse(int loanAmount, int loanTerm) {
        this.loanAmount = loanAmount;
        this.loanTerm = loanTerm;
    }

    public List<LoanOffer> getOffers() {
        return offers;
    }

    public void add(String bankName, String loanInterest) {
        offers.add(new LoanOffer(bankName, loanAmount, loanTerm, loanInterest));
    }
}
