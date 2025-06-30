package com.shopsphere.productservice.annotations;

import com.shopsphere.productservice.validators.DecimalScaleValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DecimalScaleValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DecimalScale {

    String message() default "Value should contains less or equal than max decimal points";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int max() default Integer.MAX_VALUE;
}
