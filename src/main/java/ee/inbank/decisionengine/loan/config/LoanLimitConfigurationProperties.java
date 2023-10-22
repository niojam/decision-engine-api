package ee.inbank.decisionengine.loan.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.Period;

@Data
@Validated
@ConfigurationProperties("loan.limit")
public class LoanLimitConfigurationProperties {

    @NotNull
    private BigDecimal minimalAmount;

    @NotNull
    private BigDecimal maximumAmount;

    @NotNull
    private Period minimalPeriod;

    @NotNull
    private Period maximumPeriod;

}
