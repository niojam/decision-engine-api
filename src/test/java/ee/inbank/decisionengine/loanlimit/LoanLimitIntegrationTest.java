package ee.inbank.decisionengine.loanlimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.inbank.decisionengine.customer.CustomerProfileProvider;
import ee.inbank.decisionengine.customer.dto.CustomerProfile;
import ee.inbank.decisionengine.customer.dto.CustomerSegment;
import ee.inbank.decisionengine.loan.dto.LoanRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import java.math.BigDecimal;
import java.time.Period;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoanLimitIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerProfileProvider customerProfileProvider;


    @Test
    public void shouldApproveLimit() throws Exception {
        when(customerProfileProvider.getCustomerProfile("39909202229")).thenReturn(
                new CustomerProfile().setSegment(CustomerSegment.SEGMENT3)
                        .setPersonalCode("39909202229")
                        .setCreditModifier(1000L));

        LoanRequest loanRequest = new LoanRequest()
                .setLoanAmount(BigDecimal.valueOf(12_000))
                .setLoanPeriodInMonths(12)
                .setCustomerPersonalCode("39909202229");

        mvc.perform(post("/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.decision", Matchers.is("APPROVED")))
                .andExpect(jsonPath("$.details.approvedLimit", Matchers.is(12_000)))
                .andExpect(jsonPath("$.details.approvedPeriod", Matchers.is(Period.ofMonths(12).toString())));
    }

    @Test
    public void shouldFailAmountOverLimit() throws Exception {

        LoanRequest loanRequest = new LoanRequest()
                .setLoanAmount(BigDecimal.valueOf(16_000))
                .setLoanPeriodInMonths(12)
                .setCustomerPersonalCode("39909202229");

        mvc.perform(post("/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldFailPeriodOverLimit() throws Exception {

        LoanRequest loanRequest = new LoanRequest()
                .setLoanAmount(BigDecimal.valueOf(10_000))
                .setLoanPeriodInMonths(112)
                .setCustomerPersonalCode("39909202229");

        mvc.perform(post("/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldFailPersonalCodeInvalid() throws Exception {

        LoanRequest loanRequest = new LoanRequest()
                .setLoanAmount(BigDecimal.valueOf(10_000))
                .setLoanPeriodInMonths(12)
                .setCustomerPersonalCode("11");

        mvc.perform(post("/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andExpect(status().is4xxClientError());
    }


}
