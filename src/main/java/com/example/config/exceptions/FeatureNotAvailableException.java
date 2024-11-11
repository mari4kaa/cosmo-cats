package com.example.config.exceptions;

public class FeatureNotAvailableException extends Exception {
    public FeatureNotAvailableException(String feature) {
        super("Feature" + feature + " is not available");
    }
}
