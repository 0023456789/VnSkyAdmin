package org.example.adminsky.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppResponse {
    Long id;
    String code;
    String name;
    @JsonProperty("isActive")
    Boolean active;
    Instant createdAt;
    Instant updatedAt;
}
