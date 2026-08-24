package com.thisal.supply_chain_core.dto;

import com.thisal.supply_chain_core.enums.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItemDTO {

    private UUID _id;
    private String sku;
    private String name;
    private int qtyOnHand;
    private int reorderThreshold;
    private StockStatus stockStatus;
    private LocalDateTime updatedAt;

}