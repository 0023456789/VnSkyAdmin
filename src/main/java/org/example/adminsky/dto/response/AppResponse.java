package org.example.adminsky.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

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
