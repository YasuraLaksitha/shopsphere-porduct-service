package com.shopsphere.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(
        name = "Response",
        description = "Schema to hold success response information"
)
public class ResponseDTO {

    @Schema(description = "Status code for response")
    private HttpStatus status;

    @Schema(description = "Success message for response")
    private String message;

    @Schema(description = "Timestamp when response created")
    private LocalDateTime timestamp;
}
