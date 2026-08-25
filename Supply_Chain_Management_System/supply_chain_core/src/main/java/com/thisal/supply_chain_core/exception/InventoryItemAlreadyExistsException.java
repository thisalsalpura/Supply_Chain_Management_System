package com.thisal.supply_chain_core.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class InventoryItemAlreadyExistsException extends RuntimeException {

    public InventoryItemAlreadyExistsException(String message) {
        super(message);
    }

}