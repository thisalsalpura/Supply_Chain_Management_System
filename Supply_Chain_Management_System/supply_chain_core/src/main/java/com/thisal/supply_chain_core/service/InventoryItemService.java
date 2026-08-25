package com.thisal.supply_chain_core.service;

import com.thisal.supply_chain_core.model.ResponseModel;
import jakarta.ejb.Local;

@Local
public interface InventoryItemService {

    ResponseModel createInventoryItem(String sku, String name, int qtyOnHand, int reorderThreshold);

    ResponseModel updateStock(String sku, int newQty);

    ResponseModel getAllInventoryItems();

    ResponseModel getLowStockInventoryItems();

}