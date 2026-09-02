package com.thisal.supply_chain_core.dto;

import com.thisal.supply_chain_core.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private UUID _id;
    private VendorDTO vendorDTO;
    private List<OrderItemDTO> orderItemDTOs;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;

}