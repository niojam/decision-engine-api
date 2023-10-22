package ee.inbank.decisionengine.loan.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.Period;

@Data
@Accessors(chain = true)
public class LoanLimitDecisionDetails {

    private BigDecimal approvedLimit;

    private Period approvedPeriod;

}
