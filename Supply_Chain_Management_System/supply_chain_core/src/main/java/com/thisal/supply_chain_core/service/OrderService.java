package com.thisal.supply_chain_core.service;

import com.thisal.supply_chain_core.dto.OrderRequestDTO;
import com.thisal.supply_chain_core.model.ResponseModel;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface OrderService {

    ResponseModel placeOrder(String vendorEmail, List<OrderRequestDTO> orderRequestDTOs);

    ResponseModel getAllOrders();

}