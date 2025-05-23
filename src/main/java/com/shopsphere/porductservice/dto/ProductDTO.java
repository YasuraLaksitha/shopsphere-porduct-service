package com.shopsphere.porductservice.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class ProductDTO {

    @NotEmpty(message = "Product name is required")
    @Length(min = 3, message = "Minimum character length for product name should be 3")
    private String productName;

    private String productDescription;

    @Positive(message = "Product quantity should be positive")
    private Integer productQuantity;

    private String image;

    @NotNull(message = "Product price is required")
    private Double productPrice;

    @PositiveOrZero(message = "Product quantity should be zero or positive")
    private Double productDiscountPrice;

    @PositiveOrZero(message = "Product special price should be zero or positive")
    private Double productSpecialPrice;
}
