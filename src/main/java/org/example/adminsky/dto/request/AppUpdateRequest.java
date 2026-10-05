package org.example.adminsky.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppUpdateRequest {
    @NotBlank(message = "APP_CODE_INVALID")
    @Pattern(regexp = "^[A-Za-z0-9_-]{2,50}$", message = "APP_CODE_INVALID")
    String code;
    @NotBlank(message = "APP_NAME_INVALID")
    @Size(max = 100, message = "APP_NAME_INVALID")
    String name;
}
