package com.shopsphere.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        name = "Product",
        description = "Schema to hold product details information"
)
public class ProductDTO {

    @Schema(name = "product name", example = "Tennis ball")
    @NotEmpty(message = "Product name is required")
    @Length(min = 3, message = "Minimum character length for product name should be 3")
    private String productName;

    @Schema(description = "Description fot the product")
    private String productDescription;

    @Schema(description = "Product quantity", example = "100")
    @Positive(message = "Product quantity should be positive")
    private Integer productQuantity;

    @Schema(description = "Product image")
    private String productImage;

    @Schema(description = "Product price is USD", example = "2.00")
    @NotNull(message = "Product price is required")
    private Double productPrice;

    @Schema(description = "Product discount price is USD", example = "1.00")
    @PositiveOrZero(message = "Product quantity should be zero or positive")
    private Double productDiscountPrice;

    @Schema(description = "Product discount is USD", example = "1.00")
    @PositiveOrZero(message = "Product special price should be zero or positive")
    private Double productSpecialPrice;
}
