package com.shopsphere.porductservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDTO {

    @NotEmpty(message = "Product name is required")
    @Min(value = 3, message = "Minimum character length for product name should be 3")
    private String productName;

    private String productDescription;

    @NotEmpty(message = "Product quantity is required")
    @Positive(message = "Product quantity should be positive")
    private Integer productQuantity;

    private String image;

    @NotEmpty(message = "Product price is required")
    private double productPrice;

    @PositiveOrZero(message = "Product quantity should be zero or positive")
    private double productDiscountPrice;

    @PositiveOrZero(message = "Product quantity should be zero or positive")
    private double productSpecialPrice;
}
