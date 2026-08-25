package com.thisal.supply_chain_core.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class LowStockAlertModel {

    private List<String> affectedSkus;
    private LocalDateTime detectedAt;

}