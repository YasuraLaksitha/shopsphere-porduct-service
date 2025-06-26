package com.shopsphere.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "contact")
@Schema(
        name = "Contact Details",
        description = "Contact information of application crew"
)
public class ContactDetailsDTO {

    @Schema(description = "Welcome message", example = "Welcome to ShopSphere application")
    private String message;

    @Schema(description = "Contact information")
    private Map<String, String> contactDetails;

    @Schema(description = "Hotline")
    private List<String> onCallSupport;
}
