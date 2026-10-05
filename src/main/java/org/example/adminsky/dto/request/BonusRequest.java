package org.example.adminsky.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.adminsky.enums.BonusType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BonusRequest {

    @NotNull(message = "PLAN_BONUS_INVALID")
    BonusType bonusType;

    @NotNull(message = "PLAN_BONUS_INVALID")
    @Positive(message = "PLAN_BONUS_INVALID")
    Long amount;
}
