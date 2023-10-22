package ee.inbank.decisionengine.loan.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LoanLimitConfigurationProperties.class)
public class LoanLimitConfiguration {
}
