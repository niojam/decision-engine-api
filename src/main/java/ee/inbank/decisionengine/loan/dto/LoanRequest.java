package ee.inbank.decisionengine.loan.dto;

import ee.inbank.decisionengine.validator.EstonianPersonalCode;
import ee.inbank.decisionengine.validator.LoanAmount;
import ee.inbank.decisionengine.validator.LoanPeriod;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class LoanRequest {

    @EstonianPersonalCode
    private String customerPersonalCode;

    @LoanPeriod
    private Integer loanPeriodInMonths;

    @LoanAmount
    private BigDecimal loanAmount;

}
