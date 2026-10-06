package org.example.adminsky.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;
import org.example.adminsky.enums.AppWarningCode;

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
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<String> planCodes;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<AppWarningCode> warnings;
}
