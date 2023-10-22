package ee.inbank.decisionengine.loan;

import ee.inbank.decisionengine.customer.dto.CustomerProfile;
import ee.inbank.decisionengine.customer.dto.CustomerSegment;
import ee.inbank.decisionengine.loan.config.LoanLimitConfigurationProperties;
import ee.inbank.decisionengine.loan.dto.LoanDecisionProcessResult;
import ee.inbank.decisionengine.loan.dto.LoanLimitDecisionDetails;
import ee.inbank.decisionengine.loan.dto.LoanRequest;
import ee.inbank.decisionengine.loan.dto.Decision;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Period;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoanLimitDecisionSuggester {

    private final LoanLimitConfigurationProperties loanLimitConfigurationProperties;

    public LoanDecisionProcessResult suggestLoanLimit(LoanRequest loanRequest, CustomerProfile customerProfile) {
        if (customerProfile.getSegment().equals(CustomerSegment.DEBT)) {
            return new LoanDecisionProcessResult().setDecision(Decision.REJECTED);
        }

        Long creditModifier = customerProfile.getCreditModifier();

        BigDecimal maximumLoanAmountForPeriod = calculateMaximumLoanAmountForRequestedPeriod(loanRequest.getLoanPeriodInMonths(),
                creditModifier);

        if (maximumLoanAmountForPeriod.compareTo(loanRequest.getLoanAmount()) >= 0
                || maximumLoanAmountForPeriod.compareTo(loanLimitConfigurationProperties.getMaximumAmount()) >= 0) {
            return new LoanDecisionProcessResult()
                    .setDecision(Decision.APPROVED)
                    .setDetails(new LoanLimitDecisionDetails()
                            .setApprovedLimit(maximumLoanAmountForPeriod)
                            .setApprovedPeriod(Period.ofMonths(loanRequest.getLoanPeriodInMonths()))
                    );
        }

        if (maximumLoanAmountForPeriod.compareTo(loanLimitConfigurationProperties.getMinimalAmount()) >= 0) {
            return new LoanDecisionProcessResult()
                    .setDecision(Decision.REJECTED)
                    .setDetails(new LoanLimitDecisionDetails()
                            .setApprovedLimit(maximumLoanAmountForPeriod)
                            .setApprovedPeriod(Period.ofMonths(loanRequest.getLoanPeriodInMonths()))
                    );
        }

        LoanDecisionProcessResult loanDecisionRejected = new LoanDecisionProcessResult()
                .setDecision(Decision.REJECTED);

        Optional<Integer> optionalExpandedLoanPeriod = calculateNewLoanPeriodIfPossible(loanRequest, creditModifier);
        return optionalExpandedLoanPeriod
                .map(expandedPeriod -> loanDecisionRejected
                        .setDetails(new LoanLimitDecisionDetails()
                                .setApprovedLimit(calculateMaximumLoanAmountForRequestedPeriod(expandedPeriod,
                                        creditModifier))
                                .setApprovedPeriod(Period.ofMonths(expandedPeriod))))
                .orElse(loanDecisionRejected);
    }

    private BigDecimal calculateMaximumLoanAmountForRequestedPeriod(Integer requestedPeriodInMonths, Long creditModifier) {
        BigDecimal calculatedMaximum = BigDecimal.valueOf(creditModifier * requestedPeriodInMonths);
        return calculatedMaximum.compareTo(loanLimitConfigurationProperties.getMaximumAmount()) >= 0
                ? loanLimitConfigurationProperties.getMaximumAmount()
                : calculatedMaximum;
    }

    private Optional<Integer> calculateNewLoanPeriodIfPossible(LoanRequest loanRequest, Long creditModifier) {
        int loanPeriodMaximumInMonths = loanLimitConfigurationProperties.getMaximumPeriod().getMonths();
        for (int expandedLoanPeriod = loanRequest.getLoanPeriodInMonths() + 1; expandedLoanPeriod <= loanPeriodMaximumInMonths; expandedLoanPeriod++) {

            BigDecimal newMaximumLoanAmount = calculateMaximumLoanAmountForRequestedPeriod(expandedLoanPeriod,
                    creditModifier);

            if (newMaximumLoanAmount.compareTo(loanRequest.getLoanAmount()) >= 0) {
                return Optional.of(expandedLoanPeriod);
            }
        }
        return Optional.empty();
    }

}
