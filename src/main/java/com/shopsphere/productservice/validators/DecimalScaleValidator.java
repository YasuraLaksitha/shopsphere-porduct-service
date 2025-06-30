package com.shopsphere.productservice.validators;

import com.shopsphere.productservice.annotations.DecimalScale;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;
import java.util.Objects;

public class DecimalScaleValidator implements ConstraintValidator<DecimalScale, BigDecimal> {

    private int maxScale;

    @Override
    public void initialize(DecimalScale constraintAnnotation) {
        this.maxScale = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        return Objects.isNull(value) || value.scale() <= this.maxScale;
    }
}
