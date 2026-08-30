package com.thisal.supply_chain_core.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

}