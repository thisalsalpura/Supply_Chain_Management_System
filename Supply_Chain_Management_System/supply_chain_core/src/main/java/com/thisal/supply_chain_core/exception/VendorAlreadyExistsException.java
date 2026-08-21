package com.thisal.supply_chain_core.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class VendorAlreadyExistsException extends RuntimeException {

    public VendorAlreadyExistsException(String message) {
        super(message);
    }

}