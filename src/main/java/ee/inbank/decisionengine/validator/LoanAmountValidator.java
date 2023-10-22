package ee.inbank.decisionengine.validator;

import ee.inbank.decisionengine.loan.config.LoanLimitConfigurationProperties;
import lombok.RequiredArgsConstructor;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class LoanAmountValidator implements ConstraintValidator<LoanAmount, BigDecimal> {

    private final LoanLimitConfigurationProperties loanLimitConfigurationProperties;

    @Override
    public boolean isValid(BigDecimal loanAmount, ConstraintValidatorContext context) {
        if (loanAmount == null) {
            return false;
        }
        BigDecimal maxAmount = loanLimitConfigurationProperties.getMaximumAmount();
        BigDecimal minAmount = loanLimitConfigurationProperties.getMinimalAmount();


        return loanAmount.compareTo(maxAmount) <= 0 && loanAmount.compareTo(minAmount) >= 0;
    }


}
