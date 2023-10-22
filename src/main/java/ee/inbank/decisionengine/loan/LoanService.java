package ee.inbank.decisionengine.loan;

import ee.inbank.decisionengine.customer.CustomerProfileProvider;
import ee.inbank.decisionengine.customer.dto.CustomerProfile;
import ee.inbank.decisionengine.loan.dto.LoanDecisionProcessResult;
import ee.inbank.decisionengine.loan.dto.LoanRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanLimitDecisionSuggester loanLimitDecisionSuggester;
    private final CustomerProfileProvider customerProfileProvider;

    public LoanDecisionProcessResult submitLoan(LoanRequest loanRequest) {
        CustomerProfile customerProfile = customerProfileProvider.getCustomerProfile(loanRequest.getCustomerPersonalCode());

        return loanLimitDecisionSuggester.suggestLoanLimit(loanRequest, customerProfile);
    }

}
