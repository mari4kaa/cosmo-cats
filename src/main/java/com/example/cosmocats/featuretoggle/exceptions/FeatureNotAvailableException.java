package com.example.cosmocats.featuretoggle.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Feature not available")
public class FeatureNotAvailableException extends RuntimeException {
    public FeatureNotAvailableException(String feature) {
        super(String.format("Feature %s is not available", feature));
    }
}
