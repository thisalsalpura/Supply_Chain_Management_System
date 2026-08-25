package com.thisal.supply_chain_core.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class InventoryItemNotFoundException extends RuntimeException {

    public InventoryItemNotFoundException(String message) {
        super(message);
    }

}