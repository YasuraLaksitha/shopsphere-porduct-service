package com.shopsphere.productservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDTO {

    @NotEmpty(message = "Category name cannot be empty")
    private String categoryName;

    private String categoryDescription;
}
