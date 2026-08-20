package com.thisal.supply_chain_core.service;

import jakarta.ejb.Local;

@Local
public interface ShipmentService {

    void createShipment();

}