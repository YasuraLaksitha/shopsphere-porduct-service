package com.shopsphere.productservice.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "contact")
public class ContactDetailsDTO {

    private String message;

    private Map<String, String> contactDetails;

    private List<String> onCallSupport;
}
