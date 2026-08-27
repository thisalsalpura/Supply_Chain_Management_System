package com.thisal.supply_chain_core.record;

import java.util.List;

public record PlaceOrderRequestRecord(String vendorEmail, List<String> skus, int qty) {
}