package com.thisal.supply_chain_core.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class VendorNotFoundException extends RuntimeException {

    public VendorNotFoundException(String message) {
        super(message);
    }

}