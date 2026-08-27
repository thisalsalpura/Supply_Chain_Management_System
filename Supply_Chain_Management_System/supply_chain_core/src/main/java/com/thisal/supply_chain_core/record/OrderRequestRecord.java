package com.thisal.supply_chain_core.record;

import com.thisal.supply_chain_core.dto.OrderRequestDTO;

import java.util.List;

public record OrderRequestRecord(List<OrderRequestDTO> orderRequestDTOs) {
}