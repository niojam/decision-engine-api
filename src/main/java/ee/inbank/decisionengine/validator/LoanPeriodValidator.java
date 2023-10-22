package ee.inbank.decisionengine.validator;

import ee.inbank.decisionengine.loan.config.LoanLimitConfigurationProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoanPeriodValidator implements ConstraintValidator<LoanPeriod, Integer> {

    private final LoanLimitConfigurationProperties loanLimitConfigurationProperties;

    @Override
    public boolean isValid(Integer loanPeriod, ConstraintValidatorContext context) {
        if (loanPeriod == null) {
            return false;
        }
        int maxPeriodMonths = loanLimitConfigurationProperties.getMaximumPeriod().getMonths();
        int minPeriodMonths = loanLimitConfigurationProperties.getMinimalPeriod().getMonths();

        return loanPeriod >= minPeriodMonths && loanPeriod <= maxPeriodMonths;
    }


}