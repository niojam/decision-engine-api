package ee.inbank.decisionengine.loan.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoanDecisionProcessResult {

    private Decision decision;

    private LoanLimitDecisionDetails details;

}
