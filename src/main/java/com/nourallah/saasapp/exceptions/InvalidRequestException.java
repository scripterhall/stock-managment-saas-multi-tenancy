package com.nourallah.saasapp.exceptions;

public class InvalidRequestException extends BusinessException {
    public InvalidRequestException(String tenantIsNotPending) {
        super(tenantIsNotPending);
    }
}
