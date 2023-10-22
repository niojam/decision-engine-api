package ee.inbank.decisionengine.loan.web;

import ee.inbank.decisionengine.loan.LoanService;
import ee.inbank.decisionengine.loan.dto.LoanDecisionProcessResult;
import ee.inbank.decisionengine.loan.dto.LoanRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("loan")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public LoanDecisionProcessResult submitLoanRequest(@Valid @RequestBody LoanRequest loanRequest) {
        return loanService.submitLoan(loanRequest);
    }

}
