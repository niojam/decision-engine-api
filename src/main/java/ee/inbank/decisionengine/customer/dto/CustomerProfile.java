package ee.inbank.decisionengine.customer.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CustomerProfile {

    private String personalCode;
    private Long creditModifier;
    private CustomerSegment segment;

}
