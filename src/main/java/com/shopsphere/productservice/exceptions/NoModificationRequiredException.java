package com.shopsphere.productservice.exceptions;

public class NoModificationRequiredException extends RuntimeException {

    final String resourceName;

    final String fieldName;

    final String fieldValue;

    public NoModificationRequiredException(final String resourceName, final String fieldName, final String fieldValue) {
        super(String.format("No modification required for resource '%s' with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
