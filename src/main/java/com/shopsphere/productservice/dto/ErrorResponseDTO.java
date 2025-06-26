package com.shopsphere.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(
        name = "Error Response",
        description = "Schema to hold error response information"
)
public class ErrorResponseDTO {

    @Schema(description = "Status code for error response")
    private String status;

    @Schema(description = "URL path for response")
    private String path;

    @Schema(description = "Reason for error")
    private String message;

    @Schema(description = "Timestamp when error response generated")
    private LocalDateTime timestamp;
}
