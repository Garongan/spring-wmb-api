package com.enigma.wmb_api_next.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TableMenuRequest {
    @NotBlank(message = "Table Menu Name is required")
    private String name;
}
