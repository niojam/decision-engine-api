package ee.inbank.decisionengine.loanlimit;

import ee.inbank.decisionengine.customer.dto.CustomerProfile;
import ee.inbank.decisionengine.customer.dto.CustomerSegment;
import ee.inbank.decisionengine.loan.LoanLimitDecisionSuggester;
import ee.inbank.decisionengine.loan.config.LoanLimitConfigurationProperties;
import ee.inbank.decisionengine.loan.dto.LoanDecisionProcessResult;
import ee.inbank.decisionengine.loan.dto.LoanRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Period;


import static ee.inbank.decisionengine.loan.dto.Decision.APPROVED;
import static ee.inbank.decisionengine.loan.dto.Decision.REJECTED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LoanLimitDecisionSuggesterTest {

    @Mock
    private LoanLimitConfigurationProperties loanLimitConfigurationProperties;

    @InjectMocks
    private LoanLimitDecisionSuggester loanLimitDecisionSuggester;

    @Test
    void shouldRejectLoanWhenCustomerHasDept() {
        LoanRequest loanRequest =  new LoanRequest()
                .setLoanPeriodInMonths(12)
                .setLoanAmount(BigDecimal.valueOf(1000))
                .setCustomerPersonalCode("49002010965");
        CustomerProfile customerProfile = new CustomerProfile()
                .setPersonalCode("49002010965")
                .setSegment(CustomerSegment.DEBT)
                .setCreditModifier(0L);

        LoanDecisionProcessResult result = loanLimitDecisionSuggester.suggestLoanLimit(loanRequest, customerProfile);

        assertThat(result.getDecision()).isEqualTo(REJECTED);
        assertThat(result.getDetails()).isNull();
    }

    @Test
    void shouldApproveLoanAndSuggestAmountWhenCustomerRequestUnderLimit() {
        LoanRequest loanRequest =  new LoanRequest()
                .setLoanPeriodInMonths(12)
                .setLoanAmount(BigDecimal.valueOf(1000))
                .setCustomerPersonalCode("49002010965");
        CustomerProfile customerProfile = new CustomerProfile()
                .setPersonalCode("49002010965")
                .setSegment(CustomerSegment.SEGMENT3)
                .setCreditModifier(1000L);

        when(loanLimitConfigurationProperties.getMaximumAmount()).thenReturn(BigDecimal.valueOf(15_000));

        LoanDecisionProcessResult result = loanLimitDecisionSuggester.suggestLoanLimit(loanRequest, customerProfile);

        assertThat(result.getDecision()).isEqualTo(APPROVED);
        assertThat(result.getDetails().getApprovedLimit()).isEqualTo(BigDecimal.valueOf(12_000L));
        assertThat(result.getDetails().getApprovedPeriod()).isEqualTo(Period.ofMonths(12));
    }

    @Test
    void shouldRejectAndReduceAmountWhenCustomerRequestOverLimit() {
        LoanRequest loanRequest =  new LoanRequest()
                .setLoanPeriodInMonths(12)
                .setLoanAmount(BigDecimal.valueOf(10000))
                .setCustomerPersonalCode("49002010965");
        CustomerProfile customerProfile = new CustomerProfile()
                .setPersonalCode("49002010965")
                .setSegment(CustomerSegment.SEGMENT2)
                .setCreditModifier(300L);

        when(loanLimitConfigurationProperties.getMinimalAmount()).thenReturn(BigDecimal.valueOf(2_000));
        when(loanLimitConfigurationProperties.getMaximumAmount()).thenReturn(BigDecimal.valueOf(10_000));


        LoanDecisionProcessResult result = loanLimitDecisionSuggester.suggestLoanLimit(loanRequest, customerProfile);

        assertThat(result.getDecision()).isEqualTo(REJECTED);
        assertThat(result.getDetails().getApprovedLimit()).isEqualTo(BigDecimal.valueOf(3600L));
        assertThat(result.getDetails().getApprovedPeriod()).isEqualTo(Period.ofMonths(12));
    }

    @Test
    void shouldRejectAndReducePeriodWhenCustomerRequestOverLimit() {
        LoanRequest loanRequest =  new LoanRequest()
                .setLoanPeriodInMonths(12)
                .setLoanAmount(BigDecimal.valueOf(12_0000))
                .setCustomerPersonalCode("49002010965");
        CustomerProfile customerProfile = new CustomerProfile()
                .setPersonalCode("49002010965")
                .setSegment(CustomerSegment.SEGMENT1)
                .setCreditModifier(100L);

        when(loanLimitConfigurationProperties.getMinimalAmount()).thenReturn(BigDecimal.valueOf(2_000));
        when(loanLimitConfigurationProperties.getMaximumPeriod()).thenReturn(Period.ofMonths(1500));
        when(loanLimitConfigurationProperties.getMaximumAmount()).thenReturn(BigDecimal.valueOf(15_0000));


        LoanDecisionProcessResult result = loanLimitDecisionSuggester.suggestLoanLimit(loanRequest, customerProfile);

        assertThat(result.getDecision()).isEqualTo(REJECTED);
        assertThat(result.getDetails().getApprovedLimit()).isEqualTo(BigDecimal.valueOf(12_0000));
        assertThat(result.getDetails().getApprovedPeriod()).isEqualTo(Period.ofMonths(1200));
    }

    @Test
    void shouldRejectWhenCustomerRequestOverLimitAndIncreasePeriod() {
        LoanRequest loanRequest =  new LoanRequest()
                .setLoanPeriodInMonths(12)
                .setLoanAmount(BigDecimal.valueOf(6000))
                .setCustomerPersonalCode("49002010965");
        CustomerProfile customerProfile = new CustomerProfile()
                .setPersonalCode("49002010965")
                .setSegment(CustomerSegment.SEGMENT1)
                .setCreditModifier(100L);

        when(loanLimitConfigurationProperties.getMinimalAmount()).thenReturn(BigDecimal.valueOf(2_000));
        when(loanLimitConfigurationProperties.getMaximumPeriod()).thenReturn(Period.ofMonths(64));
        when(loanLimitConfigurationProperties.getMaximumAmount()).thenReturn(BigDecimal.valueOf(10_000));


        LoanDecisionProcessResult result = loanLimitDecisionSuggester.suggestLoanLimit(loanRequest, customerProfile);

        assertThat(result.getDecision()).isEqualTo(REJECTED);
        assertThat(result.getDetails().getApprovedLimit()).isEqualTo(BigDecimal.valueOf(6000));
        assertThat(result.getDetails().getApprovedPeriod()).isEqualTo(Period.ofMonths(60));
    }

    @Test
    void shouldRejectWhenCustomerRequestOverLimitAndSuggestionImpossible() {
        LoanRequest loanRequest =  new LoanRequest()
                .setLoanPeriodInMonths(12)
                .setLoanAmount(BigDecimal.valueOf(12_0000))
                .setCustomerPersonalCode("49002010965");
        CustomerProfile customerProfile = new CustomerProfile()
                .setPersonalCode("49002010965")
                .setSegment(CustomerSegment.SEGMENT3)
                .setCreditModifier(100L);

        when(loanLimitConfigurationProperties.getMinimalAmount()).thenReturn(BigDecimal.valueOf(2_000));
        when(loanLimitConfigurationProperties.getMaximumPeriod()).thenReturn(Period.ofMonths(64));
        when(loanLimitConfigurationProperties.getMaximumAmount()).thenReturn(BigDecimal.valueOf(10_000));


        LoanDecisionProcessResult result = loanLimitDecisionSuggester.suggestLoanLimit(loanRequest, customerProfile);

        assertThat(result.getDecision()).isEqualTo(REJECTED);
        assertThat(result.getDetails()).isNull();
    }

    @Test
    void shouldAcceptAndReduceOutputMaximumWhenCustomerRequestOverBankLimit() {
        LoanRequest loanRequest =  new LoanRequest()
                .setLoanPeriodInMonths(52)
                .setLoanAmount(BigDecimal.valueOf(12_000))
                .setCustomerPersonalCode("49002010965");
        CustomerProfile customerProfile = new CustomerProfile()
                .setPersonalCode("49002010965")
                .setSegment(CustomerSegment.SEGMENT3)
                .setCreditModifier(1000L);

        when(loanLimitConfigurationProperties.getMaximumAmount()).thenReturn(BigDecimal.valueOf(10_000));


        LoanDecisionProcessResult result = loanLimitDecisionSuggester.suggestLoanLimit(loanRequest, customerProfile);

        assertThat(result.getDecision()).isEqualTo(APPROVED);
        assertThat(result.getDetails().getApprovedPeriod()).isEqualTo(Period.ofMonths(52));
        assertThat(result.getDetails().getApprovedLimit()).isEqualTo(BigDecimal.valueOf(10_000));
    }

}