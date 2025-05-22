package com.shopsphere.porductservice.exceptions;

public class ResourceAlreadyUnavailableException extends RuntimeException {

    final String resourceName;

    final String fieldName;

    final String fieldValue;

    public ResourceAlreadyUnavailableException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s with %s: '%s' already unavailable", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
