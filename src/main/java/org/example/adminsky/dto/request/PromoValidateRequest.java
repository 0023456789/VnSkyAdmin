package org.example.adminsky.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromoValidateRequest {
    @NotBlank(message = "PROMO_CODE_INVALID")
    @Pattern(regexp = "^[A-Za-z0-9_-]{2,50}$", message = "PROMO_CODE_INVALID")
    String code;

    @NotNull(message = "INVALID_KEY")
    Long planId;

    @Pattern(regexp = "^[0-9]{9,15}$", message = "MSISDN_INVALID")
    String msisdn;
}
