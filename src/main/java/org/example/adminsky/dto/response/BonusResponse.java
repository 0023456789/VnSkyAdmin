package org.example.adminsky.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.adminsky.enums.BonusType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BonusResponse {
    BonusType bonusType;
    Long amount;
}
