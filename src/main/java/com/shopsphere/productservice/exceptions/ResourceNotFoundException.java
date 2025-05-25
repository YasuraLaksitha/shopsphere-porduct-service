package com.shopsphere.productservice.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    protected String resourceName;
    protected String fieldName;
    protected String value;

    public ResourceNotFoundException(String resourceName, String fieldName, String value) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, value));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.value = value;
    }

}
