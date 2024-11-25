package com.example.featuretoggle.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class FeatureNotAvailableException extends Exception {
    public FeatureNotAvailableException(String feature) {
        super("Feature " + feature + " is not available");
    }
}