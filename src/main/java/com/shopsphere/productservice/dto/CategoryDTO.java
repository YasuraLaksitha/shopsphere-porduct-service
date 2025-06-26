package com.shopsphere.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(
        name = "Category",
        description = "Schema to hold category information"
)
public class CategoryDTO {

    @Schema(description = "category name", example = "Sports")
    @NotEmpty(message = "Category name cannot be empty")
    private String categoryName;

    @Schema(description = "category description")
    private String categoryDescription;
}
