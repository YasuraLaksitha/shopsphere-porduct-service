package com.shopsphere.porductservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CategoryDTO {

    @NotEmpty(message = "Category name cannot be empty")
    private String categoryName;

    private String categoryDescription;
}
