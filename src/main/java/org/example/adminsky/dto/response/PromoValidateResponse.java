package org.example.adminsky.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.adminsky.enums.PromoReasonCode;
import org.example.adminsky.enums.PromoWarningCode;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromoValidateResponse {
    boolean valid;
    PromoReasonCode reasonCode;
    List<PromoWarningCode> warnings;
    Long discountAmount;
    Long finalPrice;
}
