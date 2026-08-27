package com.thisal.supply_chain_core.service;

import com.thisal.supply_chain_core.model.ResponseModel;
import jakarta.ejb.Local;

@Local
public interface OrderService {

    ResponseModel placeOrder(String vendorEmail, String sku, int qty);

    ResponseModel getAllOrders();

}