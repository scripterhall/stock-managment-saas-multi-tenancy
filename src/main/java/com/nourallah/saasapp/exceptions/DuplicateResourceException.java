package com.nourallah.saasapp.exceptions;

public class DuplicateResourceException extends BusinessException{

    public DuplicateResourceException(final String message) {
        super(message);
    }
}
