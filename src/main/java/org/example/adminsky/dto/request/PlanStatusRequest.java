package org.example.adminsky.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanStatusRequest {
    @NotNull(message = "INVALID_KEY")
    Boolean isActive;
}
