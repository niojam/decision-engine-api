package ee.inbank.decisionengine.customer;

import ee.inbank.decisionengine.customer.dto.CustomerProfile;
import ee.inbank.decisionengine.customer.dto.CustomerSegment;
import org.springframework.stereotype.Service;

@Service
public class CustomerProfileProvider {

    // Mocked provider
    public CustomerProfile getCustomerProfile(String personalCode) {
        if (personalCode == null || personalCode.isEmpty()) {
            throw new IllegalArgumentException("Personal code is not valid");
        }
        CustomerProfile customerProfile = new CustomerProfile().setPersonalCode(personalCode);
        if (personalCode.equals("49002010976")) {
            return customerProfile
                    .setSegment(CustomerSegment.SEGMENT1)
                    .setCreditModifier(100L);
        }
        if (personalCode.equals("49002010987")) {
            return customerProfile
                    .setSegment(CustomerSegment.SEGMENT2)
                    .setCreditModifier(300L);
        }
        if (personalCode.equals("49002010998")) {
            return customerProfile
                    .setSegment(CustomerSegment.SEGMENT3)
                    .setCreditModifier(1000L);
        }
        return customerProfile
                .setSegment(CustomerSegment.DEBT)
                .setCreditModifier(0L);
    }

}
